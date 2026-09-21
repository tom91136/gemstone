// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
// scalafix:off GemstoneNoAssume; values were validated by the generator
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import Instruction.*
import Program.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private[amdgpu] object DataShareInstructionDefs {
  case object DsAddF32 extends Definition(
        Opcode.assume("ds_add_f32"),
        decoder(form(2)((operands, mods) => Instruction.DsAddF32(operands(0), operands(1), mods))),
        164
      )
  case object DsAddF64 extends Definition(
        Opcode.assume("ds_add_f64"),
        decoder(form(2)((operands, mods) => Instruction.DsAddF64(operands(0), operands(1), mods))),
        165
      )
  case object DsAddGsRegRtn extends Definition(
        Opcode.assume("ds_add_gs_reg_rtn"),
        decoder(form(2)((operands, mods) => Instruction.DsAddGsRegRtn(operands(0), operands(1), mods))),
        166
      )
  case object DsAddRtnF32 extends Definition(
        Opcode.assume("ds_add_rtn_f32"),
        decoder(form(3)((operands, mods) => Instruction.DsAddRtnF32(operands(0), operands(1), operands(2), mods))),
        167
      )
  case object DsAddRtnF64 extends Definition(
        Opcode.assume("ds_add_rtn_f64"),
        decoder(form(3)((operands, mods) => Instruction.DsAddRtnF64(operands(0), operands(1), operands(2), mods))),
        168
      )
  case object DsAddRtnU32 extends Definition(
        Opcode.assume("ds_add_rtn_u32"),
        decoder(form(3)((operands, mods) => Instruction.DsAddRtnU32(operands(0), operands(1), operands(2), mods))),
        169
      )
  case object DsAddRtnU64 extends Definition(
        Opcode.assume("ds_add_rtn_u64"),
        decoder(form(3)((operands, mods) => Instruction.DsAddRtnU64(operands(0), operands(1), operands(2), mods))),
        170
      )
  case object DsAddSrc2F32 extends Definition(
        Opcode.assume("ds_add_src2_f32"),
        decoder(form(1)((operands, mods) => Instruction.DsAddSrc2F32(operands(0), mods))),
        171
      )
  case object DsAddSrc2U32 extends Definition(
        Opcode.assume("ds_add_src2_u32"),
        decoder(form(1)((operands, mods) => Instruction.DsAddSrc2U32(operands(0), mods))),
        172
      )
  case object DsAddSrc2U64 extends Definition(
        Opcode.assume("ds_add_src2_u64"),
        decoder(form(1)((operands, mods) => Instruction.DsAddSrc2U64(operands(0), mods))),
        173
      )
  case object DsAddU32 extends Definition(
        Opcode.assume("ds_add_u32"),
        decoder(form(2)((operands, mods) => Instruction.DsAddU32(operands(0), operands(1), mods))),
        174
      )
  case object DsAddU64 extends Definition(
        Opcode.assume("ds_add_u64"),
        decoder(form(2)((operands, mods) => Instruction.DsAddU64(operands(0), operands(1), mods))),
        175
      )
  case object DsAndB32 extends Definition(
        Opcode.assume("ds_and_b32"),
        decoder(form(2)((operands, mods) => Instruction.DsAndB32(operands(0), operands(1), mods))),
        176
      )
  case object DsAndB64 extends Definition(
        Opcode.assume("ds_and_b64"),
        decoder(form(2)((operands, mods) => Instruction.DsAndB64(operands(0), operands(1), mods))),
        177
      )
  case object DsAndRtnB32 extends Definition(
        Opcode.assume("ds_and_rtn_b32"),
        decoder(form(3)((operands, mods) => Instruction.DsAndRtnB32(operands(0), operands(1), operands(2), mods))),
        178
      )
  case object DsAndRtnB64 extends Definition(
        Opcode.assume("ds_and_rtn_b64"),
        decoder(form(3)((operands, mods) => Instruction.DsAndRtnB64(operands(0), operands(1), operands(2), mods))),
        179
      )
  case object DsAndSrc2B32 extends Definition(
        Opcode.assume("ds_and_src2_b32"),
        decoder(form(1)((operands, mods) => Instruction.DsAndSrc2B32(operands(0), mods))),
        180
      )
  case object DsAndSrc2B64 extends Definition(
        Opcode.assume("ds_and_src2_b64"),
        decoder(form(1)((operands, mods) => Instruction.DsAndSrc2B64(operands(0), mods))),
        181
      )
  case object DsAppend extends Definition(
        Opcode.assume("ds_append"),
        decoder(form(1)((operands, mods) => Instruction.DsAppend(operands(0), mods))),
        182
      )
  case object DsAtomicAsyncBarrierArriveB64 extends Definition(
        Opcode.assume("ds_atomic_async_barrier_arrive_b64"),
        decoder(form(1)((operands, mods) => Instruction.DsAtomicAsyncBarrierArriveB64(operands(0), mods))),
        183
      )
  case object DsAtomicBarrierArriveRtnB64 extends Definition(
        Opcode.assume("ds_atomic_barrier_arrive_rtn_b64"),
        decoder(form(3)((operands, mods) =>
          Instruction.DsAtomicBarrierArriveRtnB64(operands(0), operands(1), operands(2), mods)
        )),
        184
      )
  case object DsBpermuteB32 extends Definition(
        Opcode.assume("ds_bpermute_b32"),
        decoder(form(3)((operands, mods) => Instruction.DsBpermuteB32(operands(0), operands(1), operands(2), mods))),
        185
      )
  case object DsBpermuteFiB32 extends Definition(
        Opcode.assume("ds_bpermute_fi_b32"),
        decoder(form(3)((operands, mods) => Instruction.DsBpermuteFiB32(operands(0), operands(1), operands(2), mods))),
        186
      )
  case object DsBvhStackPush4Pop1RtnB32 extends Definition(
        Opcode.assume("ds_bvh_stack_push4_pop1_rtn_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsBvhStackPush4Pop1RtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        187
      )
  case object DsBvhStackPush8Pop1RtnB32 extends Definition(
        Opcode.assume("ds_bvh_stack_push8_pop1_rtn_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsBvhStackPush8Pop1RtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        188
      )
  case object DsBvhStackPush8Pop2RtnB64 extends Definition(
        Opcode.assume("ds_bvh_stack_push8_pop2_rtn_b64"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsBvhStackPush8Pop2RtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        189
      )
  case object DsBvhStackRtnB32 extends Definition(
        Opcode.assume("ds_bvh_stack_rtn_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsBvhStackRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        190
      )
  case object DsCmpstB32 extends Definition(
        Opcode.assume("ds_cmpst_b32"),
        decoder(form(3)((operands, mods) => Instruction.DsCmpstB32(operands(0), operands(1), operands(2), mods))),
        191
      )
  case object DsCmpstB64 extends Definition(
        Opcode.assume("ds_cmpst_b64"),
        decoder(form(3)((operands, mods) => Instruction.DsCmpstB64(operands(0), operands(1), operands(2), mods))),
        192
      )
  case object DsCmpstF32 extends Definition(
        Opcode.assume("ds_cmpst_f32"),
        decoder(form(3)((operands, mods) => Instruction.DsCmpstF32(operands(0), operands(1), operands(2), mods))),
        193
      )
  case object DsCmpstF64 extends Definition(
        Opcode.assume("ds_cmpst_f64"),
        decoder(form(3)((operands, mods) => Instruction.DsCmpstF64(operands(0), operands(1), operands(2), mods))),
        194
      )
  case object DsCmpstRtnB32 extends Definition(
        Opcode.assume("ds_cmpst_rtn_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        195
      )
  case object DsCmpstRtnB64 extends Definition(
        Opcode.assume("ds_cmpst_rtn_b64"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstRtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        196
      )
  case object DsCmpstRtnF32 extends Definition(
        Opcode.assume("ds_cmpst_rtn_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstRtnF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        197
      )
  case object DsCmpstRtnF64 extends Definition(
        Opcode.assume("ds_cmpst_rtn_f64"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstRtnF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        198
      )
  case object DsCmpstoreB32 extends Definition(
        Opcode.assume("ds_cmpstore_b32"),
        decoder(form(3)((operands, mods) => Instruction.DsCmpstoreB32(operands(0), operands(1), operands(2), mods))),
        199
      )
  case object DsCmpstoreB64 extends Definition(
        Opcode.assume("ds_cmpstore_b64"),
        decoder(form(3)((operands, mods) => Instruction.DsCmpstoreB64(operands(0), operands(1), operands(2), mods))),
        200
      )
  case object DsCmpstoreF32 extends Definition(
        Opcode.assume("ds_cmpstore_f32"),
        decoder(form(3)((operands, mods) => Instruction.DsCmpstoreF32(operands(0), operands(1), operands(2), mods))),
        201
      )
  case object DsCmpstoreF64 extends Definition(
        Opcode.assume("ds_cmpstore_f64"),
        decoder(form(3)((operands, mods) => Instruction.DsCmpstoreF64(operands(0), operands(1), operands(2), mods))),
        202
      )
  case object DsCmpstoreRtnB32 extends Definition(
        Opcode.assume("ds_cmpstore_rtn_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstoreRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        203
      )
  case object DsCmpstoreRtnB64 extends Definition(
        Opcode.assume("ds_cmpstore_rtn_b64"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstoreRtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        204
      )
  case object DsCmpstoreRtnF32 extends Definition(
        Opcode.assume("ds_cmpstore_rtn_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstoreRtnF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        205
      )
  case object DsCmpstoreRtnF64 extends Definition(
        Opcode.assume("ds_cmpstore_rtn_f64"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsCmpstoreRtnF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        206
      )
  case object DsCondSubRtnU32 extends Definition(
        Opcode.assume("ds_cond_sub_rtn_u32"),
        decoder(form(3)((operands, mods) => Instruction.DsCondSubRtnU32(operands(0), operands(1), operands(2), mods))),
        207
      )
  case object DsCondSubU32 extends Definition(
        Opcode.assume("ds_cond_sub_u32"),
        decoder(form(2)((operands, mods) => Instruction.DsCondSubU32(operands(0), operands(1), mods))),
        208
      )
  case object DsCondxchg32RtnB128 extends Definition(
        Opcode.assume("ds_condxchg32_rtn_b128"),
        decoder(form(0)((_, mods) => Instruction.DsCondxchg32RtnB128(mods))),
        209
      )
  case object DsCondxchg32RtnB64 extends Definition(
        Opcode.assume("ds_condxchg32_rtn_b64"),
        decoder(form(3)((operands, mods) =>
          Instruction.DsCondxchg32RtnB64(operands(0), operands(1), operands(2), mods)
        )),
        210
      )
  case object DsConsume extends Definition(
        Opcode.assume("ds_consume"),
        decoder(form(1)((operands, mods) => Instruction.DsConsume(operands(0), mods))),
        211
      )
  case object DsDecRtnU32 extends Definition(
        Opcode.assume("ds_dec_rtn_u32"),
        decoder(form(3)((operands, mods) => Instruction.DsDecRtnU32(operands(0), operands(1), operands(2), mods))),
        212
      )
  case object DsDecRtnU64 extends Definition(
        Opcode.assume("ds_dec_rtn_u64"),
        decoder(form(3)((operands, mods) => Instruction.DsDecRtnU64(operands(0), operands(1), operands(2), mods))),
        213
      )
  case object DsDecSrc2U32 extends Definition(
        Opcode.assume("ds_dec_src2_u32"),
        decoder(form(1)((operands, mods) => Instruction.DsDecSrc2U32(operands(0), mods))),
        214
      )
  case object DsDecSrc2U64 extends Definition(
        Opcode.assume("ds_dec_src2_u64"),
        decoder(form(1)((operands, mods) => Instruction.DsDecSrc2U64(operands(0), mods))),
        215
      )
  case object DsDecU32 extends Definition(
        Opcode.assume("ds_dec_u32"),
        decoder(form(2)((operands, mods) => Instruction.DsDecU32(operands(0), operands(1), mods))),
        216
      )
  case object DsDecU64 extends Definition(
        Opcode.assume("ds_dec_u64"),
        decoder(form(2)((operands, mods) => Instruction.DsDecU64(operands(0), operands(1), mods))),
        217
      )
  case object DsDirectLoad extends Definition(
        Opcode.assume("ds_direct_load"),
        decoder(form(1)((operands, mods) => Instruction.DsDirectLoad(operands(0), mods))),
        218
      )
  case object DsGwsBarrier extends Definition(
        Opcode.assume("ds_gws_barrier"),
        decoder(form(1)((operands, mods) => Instruction.DsGwsBarrier(operands(0), mods))),
        219
      )
  case object DsGwsInit extends Definition(
        Opcode.assume("ds_gws_init"),
        decoder(form(1)((operands, mods) => Instruction.DsGwsInit(operands(0), mods))),
        220
      )
  case object DsGwsSemaBr extends Definition(
        Opcode.assume("ds_gws_sema_br"),
        decoder(form(1)((operands, mods) => Instruction.DsGwsSemaBr(operands(0), mods))),
        221
      )
  case object DsGwsSemaP extends Definition(
        Opcode.assume("ds_gws_sema_p"),
        decoder(form(0)((_, mods) => Instruction.DsGwsSemaP(mods))),
        222
      )
  case object DsGwsSemaReleaseAll extends Definition(
        Opcode.assume("ds_gws_sema_release_all"),
        decoder(form(0)((_, mods) => Instruction.DsGwsSemaReleaseAll(mods))),
        223
      )
  case object DsGwsSemaV extends Definition(
        Opcode.assume("ds_gws_sema_v"),
        decoder(form(0)((_, mods) => Instruction.DsGwsSemaV(mods))),
        224
      )
  case object DsIncRtnU32 extends Definition(
        Opcode.assume("ds_inc_rtn_u32"),
        decoder(form(3)((operands, mods) => Instruction.DsIncRtnU32(operands(0), operands(1), operands(2), mods))),
        225
      )
  case object DsIncRtnU64 extends Definition(
        Opcode.assume("ds_inc_rtn_u64"),
        decoder(form(3)((operands, mods) => Instruction.DsIncRtnU64(operands(0), operands(1), operands(2), mods))),
        226
      )
  case object DsIncSrc2U32 extends Definition(
        Opcode.assume("ds_inc_src2_u32"),
        decoder(form(1)((operands, mods) => Instruction.DsIncSrc2U32(operands(0), mods))),
        227
      )
  case object DsIncSrc2U64 extends Definition(
        Opcode.assume("ds_inc_src2_u64"),
        decoder(form(1)((operands, mods) => Instruction.DsIncSrc2U64(operands(0), mods))),
        228
      )
  case object DsIncU32 extends Definition(
        Opcode.assume("ds_inc_u32"),
        decoder(form(2)((operands, mods) => Instruction.DsIncU32(operands(0), operands(1), mods))),
        229
      )
  case object DsIncU64 extends Definition(
        Opcode.assume("ds_inc_u64"),
        decoder(form(2)((operands, mods) => Instruction.DsIncU64(operands(0), operands(1), mods))),
        230
      )
  case object DsLoad2addrB32 extends Definition(
        Opcode.assume("ds_load_2addr_b32"),
        decoder(form(2)((operands, mods) => Instruction.DsLoad2addrB32(operands(0), operands(1), mods))),
        231
      )
  case object DsLoad2addrB64 extends Definition(
        Opcode.assume("ds_load_2addr_b64"),
        decoder(form(2)((operands, mods) => Instruction.DsLoad2addrB64(operands(0), operands(1), mods))),
        232
      )
  case object DsLoad2addrStride64B32 extends Definition(
        Opcode.assume("ds_load_2addr_stride64_b32"),
        decoder(form(2)((operands, mods) => Instruction.DsLoad2addrStride64B32(operands(0), operands(1), mods))),
        233
      )
  case object DsLoad2addrStride64B64 extends Definition(
        Opcode.assume("ds_load_2addr_stride64_b64"),
        decoder(form(2)((operands, mods) => Instruction.DsLoad2addrStride64B64(operands(0), operands(1), mods))),
        234
      )
  case object DsLoadAddtidB32 extends Definition(
        Opcode.assume("ds_load_addtid_b32"),
        decoder(form(1)((operands, mods) => Instruction.DsLoadAddtidB32(operands(0), mods))),
        235
      )
  case object DsLoadB128 extends Definition(
        Opcode.assume("ds_load_b128"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadB128(operands(0), operands(1), mods))),
        236
      )
  case object DsLoadB32 extends Definition(
        Opcode.assume("ds_load_b32"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadB32(operands(0), operands(1), mods))),
        237
      )
  case object DsLoadB64 extends Definition(
        Opcode.assume("ds_load_b64"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadB64(operands(0), operands(1), mods))),
        238
      )
  case object DsLoadB96 extends Definition(
        Opcode.assume("ds_load_b96"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadB96(operands(0), operands(1), mods))),
        239
      )
  case object DsLoadI16 extends Definition(
        Opcode.assume("ds_load_i16"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadI16(operands(0), operands(1), mods))),
        240
      )
  case object DsLoadI8 extends Definition(
        Opcode.assume("ds_load_i8"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadI8(operands(0), operands(1), mods))),
        241
      )
  case object DsLoadI8D16 extends Definition(
        Opcode.assume("ds_load_i8_d16"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadI8D16(operands(0), operands(1), mods))),
        242
      )
  case object DsLoadI8D16Hi extends Definition(
        Opcode.assume("ds_load_i8_d16_hi"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadI8D16Hi(operands(0), operands(1), mods))),
        243
      )
  case object DsLoadTr16B128 extends Definition(
        Opcode.assume("ds_load_tr16_b128"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadTr16B128(operands(0), operands(1), mods))),
        244
      )
  case object DsLoadTr4B64 extends Definition(
        Opcode.assume("ds_load_tr4_b64"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadTr4B64(operands(0), operands(1), mods))),
        245
      )
  case object DsLoadTr6B96 extends Definition(
        Opcode.assume("ds_load_tr6_b96"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadTr6B96(operands(0), operands(1), mods))),
        246
      )
  case object DsLoadTr8B64 extends Definition(
        Opcode.assume("ds_load_tr8_b64"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadTr8B64(operands(0), operands(1), mods))),
        247
      )
  case object DsLoadU16 extends Definition(
        Opcode.assume("ds_load_u16"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadU16(operands(0), operands(1), mods))),
        248
      )
  case object DsLoadU16D16 extends Definition(
        Opcode.assume("ds_load_u16_d16"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadU16D16(operands(0), operands(1), mods))),
        249
      )
  case object DsLoadU16D16Hi extends Definition(
        Opcode.assume("ds_load_u16_d16_hi"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadU16D16Hi(operands(0), operands(1), mods))),
        250
      )
  case object DsLoadU8 extends Definition(
        Opcode.assume("ds_load_u8"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadU8(operands(0), operands(1), mods))),
        251
      )
  case object DsLoadU8D16 extends Definition(
        Opcode.assume("ds_load_u8_d16"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadU8D16(operands(0), operands(1), mods))),
        252
      )
  case object DsLoadU8D16Hi extends Definition(
        Opcode.assume("ds_load_u8_d16_hi"),
        decoder(form(2)((operands, mods) => Instruction.DsLoadU8D16Hi(operands(0), operands(1), mods))),
        253
      )
  case object DsMaxF32 extends Definition(
        Opcode.assume("ds_max_f32"),
        decoder(form(2)((operands, mods) => Instruction.DsMaxF32(operands(0), operands(1), mods))),
        254
      )
  case object DsMaxF64 extends Definition(
        Opcode.assume("ds_max_f64"),
        decoder(form(2)((operands, mods) => Instruction.DsMaxF64(operands(0), operands(1), mods))),
        255
      )
  case object DsMaxI32 extends Definition(
        Opcode.assume("ds_max_i32"),
        decoder(form(2)((operands, mods) => Instruction.DsMaxI32(operands(0), operands(1), mods))),
        256
      )
  case object DsMaxI64 extends Definition(
        Opcode.assume("ds_max_i64"),
        decoder(form(2)((operands, mods) => Instruction.DsMaxI64(operands(0), operands(1), mods))),
        257
      )
  case object DsMaxNumF32 extends Definition(
        Opcode.assume("ds_max_num_f32"),
        decoder(form(2)((operands, mods) => Instruction.DsMaxNumF32(operands(0), operands(1), mods))),
        258
      )
  case object DsMaxNumF64 extends Definition(
        Opcode.assume("ds_max_num_f64"),
        decoder(form(2)((operands, mods) => Instruction.DsMaxNumF64(operands(0), operands(1), mods))),
        259
      )
  case object DsMaxNumRtnF32 extends Definition(
        Opcode.assume("ds_max_num_rtn_f32"),
        decoder(form(3)((operands, mods) => Instruction.DsMaxNumRtnF32(operands(0), operands(1), operands(2), mods))),
        260
      )
  case object DsMaxNumRtnF64 extends Definition(
        Opcode.assume("ds_max_num_rtn_f64"),
        decoder(form(3)((operands, mods) => Instruction.DsMaxNumRtnF64(operands(0), operands(1), operands(2), mods))),
        261
      )
  case object DsMaxRtnF32 extends Definition(
        Opcode.assume("ds_max_rtn_f32"),
        decoder(form(3)((operands, mods) => Instruction.DsMaxRtnF32(operands(0), operands(1), operands(2), mods))),
        262
      )
  case object DsMaxRtnF64 extends Definition(
        Opcode.assume("ds_max_rtn_f64"),
        decoder(form(3)((operands, mods) => Instruction.DsMaxRtnF64(operands(0), operands(1), operands(2), mods))),
        263
      )
  case object DsMaxRtnI32 extends Definition(
        Opcode.assume("ds_max_rtn_i32"),
        decoder(form(3)((operands, mods) => Instruction.DsMaxRtnI32(operands(0), operands(1), operands(2), mods))),
        264
      )
  case object DsMaxRtnI64 extends Definition(
        Opcode.assume("ds_max_rtn_i64"),
        decoder(form(3)((operands, mods) => Instruction.DsMaxRtnI64(operands(0), operands(1), operands(2), mods))),
        265
      )
  case object DsMaxRtnU32 extends Definition(
        Opcode.assume("ds_max_rtn_u32"),
        decoder(form(3)((operands, mods) => Instruction.DsMaxRtnU32(operands(0), operands(1), operands(2), mods))),
        266
      )
  case object DsMaxRtnU64 extends Definition(
        Opcode.assume("ds_max_rtn_u64"),
        decoder(form(3)((operands, mods) => Instruction.DsMaxRtnU64(operands(0), operands(1), operands(2), mods))),
        267
      )
  case object DsMaxSrc2F32 extends Definition(
        Opcode.assume("ds_max_src2_f32"),
        decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2F32(operands(0), mods))),
        268
      )
  case object DsMaxSrc2F64 extends Definition(
        Opcode.assume("ds_max_src2_f64"),
        decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2F64(operands(0), mods))),
        269
      )
  case object DsMaxSrc2I32 extends Definition(
        Opcode.assume("ds_max_src2_i32"),
        decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2I32(operands(0), mods))),
        270
      )
  case object DsMaxSrc2I64 extends Definition(
        Opcode.assume("ds_max_src2_i64"),
        decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2I64(operands(0), mods))),
        271
      )
  case object DsMaxSrc2U32 extends Definition(
        Opcode.assume("ds_max_src2_u32"),
        decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2U32(operands(0), mods))),
        272
      )
  case object DsMaxSrc2U64 extends Definition(
        Opcode.assume("ds_max_src2_u64"),
        decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2U64(operands(0), mods))),
        273
      )
  case object DsMaxU32 extends Definition(
        Opcode.assume("ds_max_u32"),
        decoder(form(2)((operands, mods) => Instruction.DsMaxU32(operands(0), operands(1), mods))),
        274
      )
  case object DsMaxU64 extends Definition(
        Opcode.assume("ds_max_u64"),
        decoder(form(2)((operands, mods) => Instruction.DsMaxU64(operands(0), operands(1), mods))),
        275
      )
  case object DsMinF32 extends Definition(
        Opcode.assume("ds_min_f32"),
        decoder(form(2)((operands, mods) => Instruction.DsMinF32(operands(0), operands(1), mods))),
        276
      )
  case object DsMinF64 extends Definition(
        Opcode.assume("ds_min_f64"),
        decoder(form(2)((operands, mods) => Instruction.DsMinF64(operands(0), operands(1), mods))),
        277
      )
  case object DsMinI32 extends Definition(
        Opcode.assume("ds_min_i32"),
        decoder(form(2)((operands, mods) => Instruction.DsMinI32(operands(0), operands(1), mods))),
        278
      )
  case object DsMinI64 extends Definition(
        Opcode.assume("ds_min_i64"),
        decoder(form(2)((operands, mods) => Instruction.DsMinI64(operands(0), operands(1), mods))),
        279
      )
  case object DsMinNumF32 extends Definition(
        Opcode.assume("ds_min_num_f32"),
        decoder(form(2)((operands, mods) => Instruction.DsMinNumF32(operands(0), operands(1), mods))),
        280
      )
  case object DsMinNumF64 extends Definition(
        Opcode.assume("ds_min_num_f64"),
        decoder(form(2)((operands, mods) => Instruction.DsMinNumF64(operands(0), operands(1), mods))),
        281
      )
  case object DsMinNumRtnF32 extends Definition(
        Opcode.assume("ds_min_num_rtn_f32"),
        decoder(form(3)((operands, mods) => Instruction.DsMinNumRtnF32(operands(0), operands(1), operands(2), mods))),
        282
      )
  case object DsMinNumRtnF64 extends Definition(
        Opcode.assume("ds_min_num_rtn_f64"),
        decoder(form(3)((operands, mods) => Instruction.DsMinNumRtnF64(operands(0), operands(1), operands(2), mods))),
        283
      )
  case object DsMinRtnF32 extends Definition(
        Opcode.assume("ds_min_rtn_f32"),
        decoder(form(3)((operands, mods) => Instruction.DsMinRtnF32(operands(0), operands(1), operands(2), mods))),
        284
      )
  case object DsMinRtnF64 extends Definition(
        Opcode.assume("ds_min_rtn_f64"),
        decoder(form(3)((operands, mods) => Instruction.DsMinRtnF64(operands(0), operands(1), operands(2), mods))),
        285
      )
  case object DsMinRtnI32 extends Definition(
        Opcode.assume("ds_min_rtn_i32"),
        decoder(form(3)((operands, mods) => Instruction.DsMinRtnI32(operands(0), operands(1), operands(2), mods))),
        286
      )
  case object DsMinRtnI64 extends Definition(
        Opcode.assume("ds_min_rtn_i64"),
        decoder(form(3)((operands, mods) => Instruction.DsMinRtnI64(operands(0), operands(1), operands(2), mods))),
        287
      )
  case object DsMinRtnU32 extends Definition(
        Opcode.assume("ds_min_rtn_u32"),
        decoder(form(3)((operands, mods) => Instruction.DsMinRtnU32(operands(0), operands(1), operands(2), mods))),
        288
      )
  case object DsMinRtnU64 extends Definition(
        Opcode.assume("ds_min_rtn_u64"),
        decoder(form(3)((operands, mods) => Instruction.DsMinRtnU64(operands(0), operands(1), operands(2), mods))),
        289
      )
  case object DsMinSrc2F32 extends Definition(
        Opcode.assume("ds_min_src2_f32"),
        decoder(form(1)((operands, mods) => Instruction.DsMinSrc2F32(operands(0), mods))),
        290
      )
  case object DsMinSrc2F64 extends Definition(
        Opcode.assume("ds_min_src2_f64"),
        decoder(form(1)((operands, mods) => Instruction.DsMinSrc2F64(operands(0), mods))),
        291
      )
  case object DsMinSrc2I32 extends Definition(
        Opcode.assume("ds_min_src2_i32"),
        decoder(form(1)((operands, mods) => Instruction.DsMinSrc2I32(operands(0), mods))),
        292
      )
  case object DsMinSrc2I64 extends Definition(
        Opcode.assume("ds_min_src2_i64"),
        decoder(form(1)((operands, mods) => Instruction.DsMinSrc2I64(operands(0), mods))),
        293
      )
  case object DsMinSrc2U32 extends Definition(
        Opcode.assume("ds_min_src2_u32"),
        decoder(form(1)((operands, mods) => Instruction.DsMinSrc2U32(operands(0), mods))),
        294
      )
  case object DsMinSrc2U64 extends Definition(
        Opcode.assume("ds_min_src2_u64"),
        decoder(form(1)((operands, mods) => Instruction.DsMinSrc2U64(operands(0), mods))),
        295
      )
  case object DsMinU32 extends Definition(
        Opcode.assume("ds_min_u32"),
        decoder(form(2)((operands, mods) => Instruction.DsMinU32(operands(0), operands(1), mods))),
        296
      )
  case object DsMinU64 extends Definition(
        Opcode.assume("ds_min_u64"),
        decoder(form(2)((operands, mods) => Instruction.DsMinU64(operands(0), operands(1), mods))),
        297
      )
  case object DsMskorB32 extends Definition(
        Opcode.assume("ds_mskor_b32"),
        decoder(form(3)((operands, mods) => Instruction.DsMskorB32(operands(0), operands(1), operands(2), mods))),
        298
      )
  case object DsMskorB64 extends Definition(
        Opcode.assume("ds_mskor_b64"),
        decoder(form(3)((operands, mods) => Instruction.DsMskorB64(operands(0), operands(1), operands(2), mods))),
        299
      )
  case object DsMskorRtnB32 extends Definition(
        Opcode.assume("ds_mskor_rtn_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsMskorRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        300
      )
  case object DsMskorRtnB64 extends Definition(
        Opcode.assume("ds_mskor_rtn_b64"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsMskorRtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        301
      )
  case object DsNop extends Definition(
        Opcode.assume("ds_nop"),
        decoder(form(0)((_, mods) => Instruction.DsNop(mods))),
        302
      )
  case object DsOrB32 extends Definition(
        Opcode.assume("ds_or_b32"),
        decoder(form(2)((operands, mods) => Instruction.DsOrB32(operands(0), operands(1), mods))),
        303
      )
  case object DsOrB64 extends Definition(
        Opcode.assume("ds_or_b64"),
        decoder(form(2)((operands, mods) => Instruction.DsOrB64(operands(0), operands(1), mods))),
        304
      )
  case object DsOrRtnB32 extends Definition(
        Opcode.assume("ds_or_rtn_b32"),
        decoder(form(3)((operands, mods) => Instruction.DsOrRtnB32(operands(0), operands(1), operands(2), mods))),
        305
      )
  case object DsOrRtnB64 extends Definition(
        Opcode.assume("ds_or_rtn_b64"),
        decoder(form(3)((operands, mods) => Instruction.DsOrRtnB64(operands(0), operands(1), operands(2), mods))),
        306
      )
  case object DsOrSrc2B32 extends Definition(
        Opcode.assume("ds_or_src2_b32"),
        decoder(form(1)((operands, mods) => Instruction.DsOrSrc2B32(operands(0), mods))),
        307
      )
  case object DsOrSrc2B64 extends Definition(
        Opcode.assume("ds_or_src2_b64"),
        decoder(form(1)((operands, mods) => Instruction.DsOrSrc2B64(operands(0), mods))),
        308
      )
  case object DsOrderedCount extends Definition(
        Opcode.assume("ds_ordered_count"),
        decoder(form(2)((operands, mods) => Instruction.DsOrderedCount(operands(0), operands(1), mods))),
        309
      )
  case object DsParamLoad extends Definition(
        Opcode.assume("ds_param_load"),
        decoder(form(2)((operands, mods) => Instruction.DsParamLoad(operands(0), operands(1), mods))),
        310
      )
  case object DsPermuteB32 extends Definition(
        Opcode.assume("ds_permute_b32"),
        decoder(form(3)((operands, mods) => Instruction.DsPermuteB32(operands(0), operands(1), operands(2), mods))),
        311
      )
  case object DsPkAddBf16 extends Definition(
        Opcode.assume("ds_pk_add_bf16"),
        decoder(form(2)((operands, mods) => Instruction.DsPkAddBf16(operands(0), operands(1), mods))),
        312
      )
  case object DsPkAddF16 extends Definition(
        Opcode.assume("ds_pk_add_f16"),
        decoder(form(2)((operands, mods) => Instruction.DsPkAddF16(operands(0), operands(1), mods))),
        313
      )
  case object DsPkAddRtnBf16 extends Definition(
        Opcode.assume("ds_pk_add_rtn_bf16"),
        decoder(form(3)((operands, mods) => Instruction.DsPkAddRtnBf16(operands(0), operands(1), operands(2), mods))),
        314
      )
  case object DsPkAddRtnF16 extends Definition(
        Opcode.assume("ds_pk_add_rtn_f16"),
        decoder(form(3)((operands, mods) => Instruction.DsPkAddRtnF16(operands(0), operands(1), operands(2), mods))),
        315
      )
  case object DsRead2B32 extends Definition(
        Opcode.assume("ds_read2_b32"),
        decoder(form(2)((operands, mods) => Instruction.DsRead2B32(operands(0), operands(1), mods))),
        316
      )
  case object DsRead2B64 extends Definition(
        Opcode.assume("ds_read2_b64"),
        decoder(form(2)((operands, mods) => Instruction.DsRead2B64(operands(0), operands(1), mods))),
        317
      )
  case object DsRead2st64B32 extends Definition(
        Opcode.assume("ds_read2st64_b32"),
        decoder(form(2)((operands, mods) => Instruction.DsRead2st64B32(operands(0), operands(1), mods))),
        318
      )
  case object DsRead2st64B64 extends Definition(
        Opcode.assume("ds_read2st64_b64"),
        decoder(form(2)((operands, mods) => Instruction.DsRead2st64B64(operands(0), operands(1), mods))),
        319
      )
  case object DsReadAddtidB32 extends Definition(
        Opcode.assume("ds_read_addtid_b32"),
        decoder(form(1)((operands, mods) => Instruction.DsReadAddtidB32(operands(0), mods))),
        320
      )
  case object DsReadB128 extends Definition(
        Opcode.assume("ds_read_b128"),
        decoder(form(2)((operands, mods) => Instruction.DsReadB128(operands(0), operands(1), mods))),
        321
      )
  case object DsReadB32 extends Definition(
        Opcode.assume("ds_read_b32"),
        decoder(form(2)((operands, mods) => Instruction.DsReadB32(operands(0), operands(1), mods))),
        322
      )
  case object DsReadB64 extends Definition(
        Opcode.assume("ds_read_b64"),
        decoder(form(2)((operands, mods) => Instruction.DsReadB64(operands(0), operands(1), mods))),
        323
      )
  case object DsReadB64TrB16 extends Definition(
        Opcode.assume("ds_read_b64_tr_b16"),
        decoder(form(2)((operands, mods) => Instruction.DsReadB64TrB16(operands(0), operands(1), mods))),
        324
      )
  case object DsReadB64TrB4 extends Definition(
        Opcode.assume("ds_read_b64_tr_b4"),
        decoder(form(2)((operands, mods) => Instruction.DsReadB64TrB4(operands(0), operands(1), mods))),
        325
      )
  case object DsReadB64TrB8 extends Definition(
        Opcode.assume("ds_read_b64_tr_b8"),
        decoder(form(2)((operands, mods) => Instruction.DsReadB64TrB8(operands(0), operands(1), mods))),
        326
      )
  case object DsReadB96 extends Definition(
        Opcode.assume("ds_read_b96"),
        decoder(form(2)((operands, mods) => Instruction.DsReadB96(operands(0), operands(1), mods))),
        327
      )
  case object DsReadB96TrB6 extends Definition(
        Opcode.assume("ds_read_b96_tr_b6"),
        decoder(form(2)((operands, mods) => Instruction.DsReadB96TrB6(operands(0), operands(1), mods))),
        328
      )
  case object DsReadI16 extends Definition(
        Opcode.assume("ds_read_i16"),
        decoder(form(2)((operands, mods) => Instruction.DsReadI16(operands(0), operands(1), mods))),
        329
      )
  case object DsReadI8 extends Definition(
        Opcode.assume("ds_read_i8"),
        decoder(form(2)((operands, mods) => Instruction.DsReadI8(operands(0), operands(1), mods))),
        330
      )
  case object DsReadI8D16 extends Definition(
        Opcode.assume("ds_read_i8_d16"),
        decoder(form(2)((operands, mods) => Instruction.DsReadI8D16(operands(0), operands(1), mods))),
        331
      )
  case object DsReadI8D16Hi extends Definition(
        Opcode.assume("ds_read_i8_d16_hi"),
        decoder(form(2)((operands, mods) => Instruction.DsReadI8D16Hi(operands(0), operands(1), mods))),
        332
      )
  case object DsReadU16 extends Definition(
        Opcode.assume("ds_read_u16"),
        decoder(form(2)((operands, mods) => Instruction.DsReadU16(operands(0), operands(1), mods))),
        333
      )
  case object DsReadU16D16 extends Definition(
        Opcode.assume("ds_read_u16_d16"),
        decoder(form(2)((operands, mods) => Instruction.DsReadU16D16(operands(0), operands(1), mods))),
        334
      )
  case object DsReadU16D16Hi extends Definition(
        Opcode.assume("ds_read_u16_d16_hi"),
        decoder(form(2)((operands, mods) => Instruction.DsReadU16D16Hi(operands(0), operands(1), mods))),
        335
      )
  case object DsReadU8 extends Definition(
        Opcode.assume("ds_read_u8"),
        decoder(form(2)((operands, mods) => Instruction.DsReadU8(operands(0), operands(1), mods))),
        336
      )
  case object DsReadU8D16 extends Definition(
        Opcode.assume("ds_read_u8_d16"),
        decoder(form(2)((operands, mods) => Instruction.DsReadU8D16(operands(0), operands(1), mods))),
        337
      )
  case object DsReadU8D16Hi extends Definition(
        Opcode.assume("ds_read_u8_d16_hi"),
        decoder(form(2)((operands, mods) => Instruction.DsReadU8D16Hi(operands(0), operands(1), mods))),
        338
      )
  case object DsRsubRtnU32 extends Definition(
        Opcode.assume("ds_rsub_rtn_u32"),
        decoder(form(3)((operands, mods) => Instruction.DsRsubRtnU32(operands(0), operands(1), operands(2), mods))),
        339
      )
  case object DsRsubRtnU64 extends Definition(
        Opcode.assume("ds_rsub_rtn_u64"),
        decoder(form(3)((operands, mods) => Instruction.DsRsubRtnU64(operands(0), operands(1), operands(2), mods))),
        340
      )
  case object DsRsubSrc2U32 extends Definition(
        Opcode.assume("ds_rsub_src2_u32"),
        decoder(form(1)((operands, mods) => Instruction.DsRsubSrc2U32(operands(0), mods))),
        341
      )
  case object DsRsubSrc2U64 extends Definition(
        Opcode.assume("ds_rsub_src2_u64"),
        decoder(form(1)((operands, mods) => Instruction.DsRsubSrc2U64(operands(0), mods))),
        342
      )
  case object DsRsubU32 extends Definition(
        Opcode.assume("ds_rsub_u32"),
        decoder(form(2)((operands, mods) => Instruction.DsRsubU32(operands(0), operands(1), mods))),
        343
      )
  case object DsRsubU64 extends Definition(
        Opcode.assume("ds_rsub_u64"),
        decoder(form(2)((operands, mods) => Instruction.DsRsubU64(operands(0), operands(1), mods))),
        344
      )
  case object DsStore2addrB32 extends Definition(
        Opcode.assume("ds_store_2addr_b32"),
        decoder(form(3)((operands, mods) => Instruction.DsStore2addrB32(operands(0), operands(1), operands(2), mods))),
        345
      )
  case object DsStore2addrB64 extends Definition(
        Opcode.assume("ds_store_2addr_b64"),
        decoder(form(3)((operands, mods) => Instruction.DsStore2addrB64(operands(0), operands(1), operands(2), mods))),
        346
      )
  case object DsStore2addrStride64B32 extends Definition(
        Opcode.assume("ds_store_2addr_stride64_b32"),
        decoder(form(3)((operands, mods) =>
          Instruction.DsStore2addrStride64B32(operands(0), operands(1), operands(2), mods)
        )),
        347
      )
  case object DsStore2addrStride64B64 extends Definition(
        Opcode.assume("ds_store_2addr_stride64_b64"),
        decoder(form(3)((operands, mods) =>
          Instruction.DsStore2addrStride64B64(operands(0), operands(1), operands(2), mods)
        )),
        348
      )
  case object DsStoreAddtidB32 extends Definition(
        Opcode.assume("ds_store_addtid_b32"),
        decoder(form(1)((operands, mods) => Instruction.DsStoreAddtidB32(operands(0), mods))),
        349
      )
  case object DsStoreB128 extends Definition(
        Opcode.assume("ds_store_b128"),
        decoder(form(2)((operands, mods) => Instruction.DsStoreB128(operands(0), operands(1), mods))),
        350
      )
  case object DsStoreB16 extends Definition(
        Opcode.assume("ds_store_b16"),
        decoder(form(2)((operands, mods) => Instruction.DsStoreB16(operands(0), operands(1), mods))),
        351
      )
  case object DsStoreB16D16Hi extends Definition(
        Opcode.assume("ds_store_b16_d16_hi"),
        decoder(form(2)((operands, mods) => Instruction.DsStoreB16D16Hi(operands(0), operands(1), mods))),
        352
      )
  case object DsStoreB32 extends Definition(
        Opcode.assume("ds_store_b32"),
        decoder(form(2)((operands, mods) => Instruction.DsStoreB32(operands(0), operands(1), mods))),
        353
      )
  case object DsStoreB64 extends Definition(
        Opcode.assume("ds_store_b64"),
        decoder(form(2)((operands, mods) => Instruction.DsStoreB64(operands(0), operands(1), mods))),
        354
      )
  case object DsStoreB8 extends Definition(
        Opcode.assume("ds_store_b8"),
        decoder(form(2)((operands, mods) => Instruction.DsStoreB8(operands(0), operands(1), mods))),
        355
      )
  case object DsStoreB8D16Hi extends Definition(
        Opcode.assume("ds_store_b8_d16_hi"),
        decoder(form(2)((operands, mods) => Instruction.DsStoreB8D16Hi(operands(0), operands(1), mods))),
        356
      )
  case object DsStoreB96 extends Definition(
        Opcode.assume("ds_store_b96"),
        decoder(form(2)((operands, mods) => Instruction.DsStoreB96(operands(0), operands(1), mods))),
        357
      )
  case object DsStorexchg2addrRtnB32 extends Definition(
        Opcode.assume("ds_storexchg_2addr_rtn_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsStorexchg2addrRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        358
      )
  case object DsStorexchg2addrRtnB64 extends Definition(
        Opcode.assume("ds_storexchg_2addr_rtn_b64"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsStorexchg2addrRtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        359
      )
  case object DsStorexchg2addrStride64RtnB32 extends Definition(
        Opcode.assume("ds_storexchg_2addr_stride64_rtn_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsStorexchg2addrStride64RtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        360
      )
  case object DsStorexchg2addrStride64RtnB64 extends Definition(
        Opcode.assume("ds_storexchg_2addr_stride64_rtn_b64"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsStorexchg2addrStride64RtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        361
      )
  case object DsStorexchgRtnB32 extends Definition(
        Opcode.assume("ds_storexchg_rtn_b32"),
        decoder(form(3)((operands, mods) =>
          Instruction.DsStorexchgRtnB32(operands(0), operands(1), operands(2), mods)
        )),
        362
      )
  case object DsStorexchgRtnB64 extends Definition(
        Opcode.assume("ds_storexchg_rtn_b64"),
        decoder(form(3)((operands, mods) =>
          Instruction.DsStorexchgRtnB64(operands(0), operands(1), operands(2), mods)
        )),
        363
      )
  case object DsSubClampRtnU32 extends Definition(
        Opcode.assume("ds_sub_clamp_rtn_u32"),
        decoder(form(3)((operands, mods) => Instruction.DsSubClampRtnU32(operands(0), operands(1), operands(2), mods))),
        364
      )
  case object DsSubClampU32 extends Definition(
        Opcode.assume("ds_sub_clamp_u32"),
        decoder(form(2)((operands, mods) => Instruction.DsSubClampU32(operands(0), operands(1), mods))),
        365
      )
  case object DsSubGsRegRtn extends Definition(
        Opcode.assume("ds_sub_gs_reg_rtn"),
        decoder(form(2)((operands, mods) => Instruction.DsSubGsRegRtn(operands(0), operands(1), mods))),
        366
      )
  case object DsSubRtnU32 extends Definition(
        Opcode.assume("ds_sub_rtn_u32"),
        decoder(form(3)((operands, mods) => Instruction.DsSubRtnU32(operands(0), operands(1), operands(2), mods))),
        367
      )
  case object DsSubRtnU64 extends Definition(
        Opcode.assume("ds_sub_rtn_u64"),
        decoder(form(3)((operands, mods) => Instruction.DsSubRtnU64(operands(0), operands(1), operands(2), mods))),
        368
      )
  case object DsSubSrc2U32 extends Definition(
        Opcode.assume("ds_sub_src2_u32"),
        decoder(form(1)((operands, mods) => Instruction.DsSubSrc2U32(operands(0), mods))),
        369
      )
  case object DsSubSrc2U64 extends Definition(
        Opcode.assume("ds_sub_src2_u64"),
        decoder(form(1)((operands, mods) => Instruction.DsSubSrc2U64(operands(0), mods))),
        370
      )
  case object DsSubU32 extends Definition(
        Opcode.assume("ds_sub_u32"),
        decoder(form(2)((operands, mods) => Instruction.DsSubU32(operands(0), operands(1), mods))),
        371
      )
  case object DsSubU64 extends Definition(
        Opcode.assume("ds_sub_u64"),
        decoder(form(2)((operands, mods) => Instruction.DsSubU64(operands(0), operands(1), mods))),
        372
      )
  case object DsSwizzleB32 extends Definition(
        Opcode.assume("ds_swizzle_b32"),
        decoder(form(2)((operands, mods) => Instruction.DsSwizzleB32(operands(0), operands(1), mods))),
        373
      )
  case object DsWrapRtnB32 extends Definition(
        Opcode.assume("ds_wrap_rtn_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsWrapRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        374
      )
  case object DsWrite2B32 extends Definition(
        Opcode.assume("ds_write2_b32"),
        decoder(form(3)((operands, mods) => Instruction.DsWrite2B32(operands(0), operands(1), operands(2), mods))),
        375
      )
  case object DsWrite2B64 extends Definition(
        Opcode.assume("ds_write2_b64"),
        decoder(form(3)((operands, mods) => Instruction.DsWrite2B64(operands(0), operands(1), operands(2), mods))),
        376
      )
  case object DsWrite2st64B32 extends Definition(
        Opcode.assume("ds_write2st64_b32"),
        decoder(form(3)((operands, mods) => Instruction.DsWrite2st64B32(operands(0), operands(1), operands(2), mods))),
        377
      )
  case object DsWrite2st64B64 extends Definition(
        Opcode.assume("ds_write2st64_b64"),
        decoder(form(3)((operands, mods) => Instruction.DsWrite2st64B64(operands(0), operands(1), operands(2), mods))),
        378
      )
  case object DsWriteAddtidB32 extends Definition(
        Opcode.assume("ds_write_addtid_b32"),
        decoder(form(1)((operands, mods) => Instruction.DsWriteAddtidB32(operands(0), mods))),
        379
      )
  case object DsWriteB128 extends Definition(
        Opcode.assume("ds_write_b128"),
        decoder(form(2)((operands, mods) => Instruction.DsWriteB128(operands(0), operands(1), mods))),
        380
      )
  case object DsWriteB16 extends Definition(
        Opcode.assume("ds_write_b16"),
        decoder(form(2)((operands, mods) => Instruction.DsWriteB16(operands(0), operands(1), mods))),
        381
      )
  case object DsWriteB16D16Hi extends Definition(
        Opcode.assume("ds_write_b16_d16_hi"),
        decoder(form(2)((operands, mods) => Instruction.DsWriteB16D16Hi(operands(0), operands(1), mods))),
        382
      )
  case object DsWriteB32 extends Definition(
        Opcode.assume("ds_write_b32"),
        decoder(form(2)((operands, mods) => Instruction.DsWriteB32(operands(0), operands(1), mods))),
        383
      )
  case object DsWriteB64 extends Definition(
        Opcode.assume("ds_write_b64"),
        decoder(form(2)((operands, mods) => Instruction.DsWriteB64(operands(0), operands(1), mods))),
        384
      )
  case object DsWriteB8 extends Definition(
        Opcode.assume("ds_write_b8"),
        decoder(form(2)((operands, mods) => Instruction.DsWriteB8(operands(0), operands(1), mods))),
        385
      )
  case object DsWriteB8D16Hi extends Definition(
        Opcode.assume("ds_write_b8_d16_hi"),
        decoder(form(2)((operands, mods) => Instruction.DsWriteB8D16Hi(operands(0), operands(1), mods))),
        386
      )
  case object DsWriteB96 extends Definition(
        Opcode.assume("ds_write_b96"),
        decoder(form(2)((operands, mods) => Instruction.DsWriteB96(operands(0), operands(1), mods))),
        387
      )
  case object DsWriteSrc2B32 extends Definition(
        Opcode.assume("ds_write_src2_b32"),
        decoder(form(1)((operands, mods) => Instruction.DsWriteSrc2B32(operands(0), mods))),
        388
      )
  case object DsWriteSrc2B64 extends Definition(
        Opcode.assume("ds_write_src2_b64"),
        decoder(form(1)((operands, mods) => Instruction.DsWriteSrc2B64(operands(0), mods))),
        389
      )
  case object DsWrxchg2RtnB32 extends Definition(
        Opcode.assume("ds_wrxchg2_rtn_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsWrxchg2RtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        390
      )
  case object DsWrxchg2RtnB64 extends Definition(
        Opcode.assume("ds_wrxchg2_rtn_b64"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsWrxchg2RtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        391
      )
  case object DsWrxchg2st64RtnB32 extends Definition(
        Opcode.assume("ds_wrxchg2st64_rtn_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsWrxchg2st64RtnB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        392
      )
  case object DsWrxchg2st64RtnB64 extends Definition(
        Opcode.assume("ds_wrxchg2st64_rtn_b64"),
        decoder(form(4)((operands, mods) =>
          Instruction.DsWrxchg2st64RtnB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        393
      )
  case object DsWrxchgRtnB32 extends Definition(
        Opcode.assume("ds_wrxchg_rtn_b32"),
        decoder(form(3)((operands, mods) => Instruction.DsWrxchgRtnB32(operands(0), operands(1), operands(2), mods))),
        394
      )
  case object DsWrxchgRtnB64 extends Definition(
        Opcode.assume("ds_wrxchg_rtn_b64"),
        decoder(form(3)((operands, mods) => Instruction.DsWrxchgRtnB64(operands(0), operands(1), operands(2), mods))),
        395
      )
  case object DsXorB32 extends Definition(
        Opcode.assume("ds_xor_b32"),
        decoder(form(2)((operands, mods) => Instruction.DsXorB32(operands(0), operands(1), mods))),
        396
      )
  case object DsXorB64 extends Definition(
        Opcode.assume("ds_xor_b64"),
        decoder(form(2)((operands, mods) => Instruction.DsXorB64(operands(0), operands(1), mods))),
        397
      )
  case object DsXorRtnB32 extends Definition(
        Opcode.assume("ds_xor_rtn_b32"),
        decoder(form(3)((operands, mods) => Instruction.DsXorRtnB32(operands(0), operands(1), operands(2), mods))),
        398
      )
  case object DsXorRtnB64 extends Definition(
        Opcode.assume("ds_xor_rtn_b64"),
        decoder(form(3)((operands, mods) => Instruction.DsXorRtnB64(operands(0), operands(1), operands(2), mods))),
        399
      )
  case object DsXorSrc2B32 extends Definition(
        Opcode.assume("ds_xor_src2_b32"),
        decoder(form(1)((operands, mods) => Instruction.DsXorSrc2B32(operands(0), mods))),
        400
      )
  case object DsXorSrc2B64 extends Definition(
        Opcode.assume("ds_xor_src2_b64"),
        decoder(form(1)((operands, mods) => Instruction.DsXorSrc2B64(operands(0), mods))),
        401
      )

  val definitions: Vec[Definition] = Vec(
    DsAddF32,
    DsAddF64,
    DsAddGsRegRtn,
    DsAddRtnF32,
    DsAddRtnF64,
    DsAddRtnU32,
    DsAddRtnU64,
    DsAddSrc2F32,
    DsAddSrc2U32,
    DsAddSrc2U64,
    DsAddU32,
    DsAddU64,
    DsAndB32,
    DsAndB64,
    DsAndRtnB32,
    DsAndRtnB64,
    DsAndSrc2B32,
    DsAndSrc2B64,
    DsAppend,
    DsAtomicAsyncBarrierArriveB64,
    DsAtomicBarrierArriveRtnB64,
    DsBpermuteB32,
    DsBpermuteFiB32,
    DsBvhStackPush4Pop1RtnB32,
    DsBvhStackPush8Pop1RtnB32,
    DsBvhStackPush8Pop2RtnB64,
    DsBvhStackRtnB32,
    DsCmpstB32,
    DsCmpstB64,
    DsCmpstF32,
    DsCmpstF64,
    DsCmpstRtnB32,
    DsCmpstRtnB64,
    DsCmpstRtnF32,
    DsCmpstRtnF64,
    DsCmpstoreB32,
    DsCmpstoreB64,
    DsCmpstoreF32,
    DsCmpstoreF64,
    DsCmpstoreRtnB32,
    DsCmpstoreRtnB64,
    DsCmpstoreRtnF32,
    DsCmpstoreRtnF64,
    DsCondSubRtnU32,
    DsCondSubU32,
    DsCondxchg32RtnB128,
    DsCondxchg32RtnB64,
    DsConsume,
    DsDecRtnU32,
    DsDecRtnU64,
    DsDecSrc2U32,
    DsDecSrc2U64,
    DsDecU32,
    DsDecU64,
    DsDirectLoad,
    DsGwsBarrier,
    DsGwsInit,
    DsGwsSemaBr,
    DsGwsSemaP,
    DsGwsSemaReleaseAll,
    DsGwsSemaV,
    DsIncRtnU32,
    DsIncRtnU64,
    DsIncSrc2U32,
    DsIncSrc2U64,
    DsIncU32,
    DsIncU64,
    DsLoad2addrB32,
    DsLoad2addrB64,
    DsLoad2addrStride64B32,
    DsLoad2addrStride64B64,
    DsLoadAddtidB32,
    DsLoadB128,
    DsLoadB32,
    DsLoadB64,
    DsLoadB96,
    DsLoadI16,
    DsLoadI8,
    DsLoadI8D16,
    DsLoadI8D16Hi,
    DsLoadTr16B128,
    DsLoadTr4B64,
    DsLoadTr6B96,
    DsLoadTr8B64,
    DsLoadU16,
    DsLoadU16D16,
    DsLoadU16D16Hi,
    DsLoadU8,
    DsLoadU8D16,
    DsLoadU8D16Hi,
    DsMaxF32,
    DsMaxF64,
    DsMaxI32,
    DsMaxI64,
    DsMaxNumF32,
    DsMaxNumF64,
    DsMaxNumRtnF32,
    DsMaxNumRtnF64,
    DsMaxRtnF32,
    DsMaxRtnF64,
    DsMaxRtnI32,
    DsMaxRtnI64,
    DsMaxRtnU32,
    DsMaxRtnU64,
    DsMaxSrc2F32,
    DsMaxSrc2F64,
    DsMaxSrc2I32,
    DsMaxSrc2I64,
    DsMaxSrc2U32,
    DsMaxSrc2U64,
    DsMaxU32,
    DsMaxU64,
    DsMinF32,
    DsMinF64,
    DsMinI32,
    DsMinI64,
    DsMinNumF32,
    DsMinNumF64,
    DsMinNumRtnF32,
    DsMinNumRtnF64,
    DsMinRtnF32,
    DsMinRtnF64,
    DsMinRtnI32,
    DsMinRtnI64,
    DsMinRtnU32,
    DsMinRtnU64,
    DsMinSrc2F32,
    DsMinSrc2F64,
    DsMinSrc2I32,
    DsMinSrc2I64,
    DsMinSrc2U32,
    DsMinSrc2U64,
    DsMinU32,
    DsMinU64,
    DsMskorB32,
    DsMskorB64,
    DsMskorRtnB32,
    DsMskorRtnB64,
    DsNop,
    DsOrB32,
    DsOrB64,
    DsOrRtnB32,
    DsOrRtnB64,
    DsOrSrc2B32,
    DsOrSrc2B64,
    DsOrderedCount,
    DsParamLoad,
    DsPermuteB32,
    DsPkAddBf16,
    DsPkAddF16,
    DsPkAddRtnBf16,
    DsPkAddRtnF16,
    DsRead2B32,
    DsRead2B64,
    DsRead2st64B32,
    DsRead2st64B64,
    DsReadAddtidB32,
    DsReadB128,
    DsReadB32,
    DsReadB64,
    DsReadB64TrB16,
    DsReadB64TrB4,
    DsReadB64TrB8,
    DsReadB96,
    DsReadB96TrB6,
    DsReadI16,
    DsReadI8,
    DsReadI8D16,
    DsReadI8D16Hi,
    DsReadU16,
    DsReadU16D16,
    DsReadU16D16Hi,
    DsReadU8,
    DsReadU8D16,
    DsReadU8D16Hi,
    DsRsubRtnU32,
    DsRsubRtnU64,
    DsRsubSrc2U32,
    DsRsubSrc2U64,
    DsRsubU32,
    DsRsubU64,
    DsStore2addrB32,
    DsStore2addrB64,
    DsStore2addrStride64B32,
    DsStore2addrStride64B64,
    DsStoreAddtidB32,
    DsStoreB128,
    DsStoreB16,
    DsStoreB16D16Hi,
    DsStoreB32,
    DsStoreB64,
    DsStoreB8,
    DsStoreB8D16Hi,
    DsStoreB96,
    DsStorexchg2addrRtnB32,
    DsStorexchg2addrRtnB64,
    DsStorexchg2addrStride64RtnB32,
    DsStorexchg2addrStride64RtnB64,
    DsStorexchgRtnB32,
    DsStorexchgRtnB64,
    DsSubClampRtnU32,
    DsSubClampU32,
    DsSubGsRegRtn,
    DsSubRtnU32,
    DsSubRtnU64,
    DsSubSrc2U32,
    DsSubSrc2U64,
    DsSubU32,
    DsSubU64,
    DsSwizzleB32,
    DsWrapRtnB32,
    DsWrite2B32,
    DsWrite2B64,
    DsWrite2st64B32,
    DsWrite2st64B64,
    DsWriteAddtidB32,
    DsWriteB128,
    DsWriteB16,
    DsWriteB16D16Hi,
    DsWriteB32,
    DsWriteB64,
    DsWriteB8,
    DsWriteB8D16Hi,
    DsWriteB96,
    DsWriteSrc2B32,
    DsWriteSrc2B64,
    DsWrxchg2RtnB32,
    DsWrxchg2RtnB64,
    DsWrxchg2st64RtnB32,
    DsWrxchg2st64RtnB64,
    DsWrxchgRtnB32,
    DsWrxchgRtnB64,
    DsXorB32,
    DsXorB64,
    DsXorRtnB32,
    DsXorRtnB64,
    DsXorSrc2B32,
    DsXorSrc2B64
  )
}
