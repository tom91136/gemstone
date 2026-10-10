// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private object DataShareInstructionDefs {
  case object DsAddF32 extends Opcode("ds_add_f32", 165)

  case object DsAddF64 extends Opcode("ds_add_f64", 166)

  case object DsAddGsRegRtn extends Opcode("ds_add_gs_reg_rtn", 167)

  case object DsAddRtnF32 extends Opcode("ds_add_rtn_f32", 168)

  case object DsAddRtnF64 extends Opcode("ds_add_rtn_f64", 169)

  case object DsAddRtnU32 extends Opcode("ds_add_rtn_u32", 170)

  case object DsAddRtnU64 extends Opcode("ds_add_rtn_u64", 171)

  case object DsAddSrc2F32 extends Opcode("ds_add_src2_f32", 172)

  case object DsAddSrc2U32 extends Opcode("ds_add_src2_u32", 173)

  case object DsAddSrc2U64 extends Opcode("ds_add_src2_u64", 174)

  case object DsAddU32 extends Opcode("ds_add_u32", 175)

  case object DsAddU64 extends Opcode("ds_add_u64", 176)

  case object DsAndB32 extends Opcode("ds_and_b32", 177)

  case object DsAndB64 extends Opcode("ds_and_b64", 178)

  case object DsAndRtnB32 extends Opcode("ds_and_rtn_b32", 179)

  case object DsAndRtnB64 extends Opcode("ds_and_rtn_b64", 180)

  case object DsAndSrc2B32 extends Opcode("ds_and_src2_b32", 181)

  case object DsAndSrc2B64 extends Opcode("ds_and_src2_b64", 182)

  case object DsAppend extends Opcode("ds_append", 183)

  case object DsAtomicAsyncBarrierArriveB64 extends Opcode("ds_atomic_async_barrier_arrive_b64", 184)

  case object DsAtomicBarrierArriveRtnB64 extends Opcode("ds_atomic_barrier_arrive_rtn_b64", 185)

  case object DsBpermuteB32 extends Opcode("ds_bpermute_b32", 186)

  case object DsBpermuteFiB32 extends Opcode("ds_bpermute_fi_b32", 187)

  case object DsBvhStackPush4Pop1RtnB32 extends Opcode("ds_bvh_stack_push4_pop1_rtn_b32", 188)

  case object DsBvhStackPush8Pop1RtnB32 extends Opcode("ds_bvh_stack_push8_pop1_rtn_b32", 189)

  case object DsBvhStackPush8Pop2RtnB64 extends Opcode("ds_bvh_stack_push8_pop2_rtn_b64", 190)

  case object DsBvhStackRtnB32 extends Opcode("ds_bvh_stack_rtn_b32", 191)

  case object DsCmpstB32 extends Opcode("ds_cmpst_b32", 192)

  case object DsCmpstB64 extends Opcode("ds_cmpst_b64", 193)

  case object DsCmpstF32 extends Opcode("ds_cmpst_f32", 194)

  case object DsCmpstF64 extends Opcode("ds_cmpst_f64", 195)

  case object DsCmpstRtnB32 extends Opcode("ds_cmpst_rtn_b32", 196)

  case object DsCmpstRtnB64 extends Opcode("ds_cmpst_rtn_b64", 197)

  case object DsCmpstRtnF32 extends Opcode("ds_cmpst_rtn_f32", 198)

  case object DsCmpstRtnF64 extends Opcode("ds_cmpst_rtn_f64", 199)

  case object DsCmpstoreB32 extends Opcode("ds_cmpstore_b32", 200)

  case object DsCmpstoreB64 extends Opcode("ds_cmpstore_b64", 201)

  case object DsCmpstoreF32 extends Opcode("ds_cmpstore_f32", 202)

  case object DsCmpstoreF64 extends Opcode("ds_cmpstore_f64", 203)

  case object DsCmpstoreRtnB32 extends Opcode("ds_cmpstore_rtn_b32", 204)

  case object DsCmpstoreRtnB64 extends Opcode("ds_cmpstore_rtn_b64", 205)

  case object DsCmpstoreRtnF32 extends Opcode("ds_cmpstore_rtn_f32", 206)

  case object DsCmpstoreRtnF64 extends Opcode("ds_cmpstore_rtn_f64", 207)

  case object DsCondSubRtnU32 extends Opcode("ds_cond_sub_rtn_u32", 208)

  case object DsCondSubU32 extends Opcode("ds_cond_sub_u32", 209)

  case object DsCondxchg32RtnB128 extends Opcode("ds_condxchg32_rtn_b128", 210)

  case object DsCondxchg32RtnB64 extends Opcode("ds_condxchg32_rtn_b64", 211)

  case object DsConsume extends Opcode("ds_consume", 212)

  case object DsDecRtnU32 extends Opcode("ds_dec_rtn_u32", 213)

  case object DsDecRtnU64 extends Opcode("ds_dec_rtn_u64", 214)

  case object DsDecSrc2U32 extends Opcode("ds_dec_src2_u32", 215)

  case object DsDecSrc2U64 extends Opcode("ds_dec_src2_u64", 216)

  case object DsDecU32 extends Opcode("ds_dec_u32", 217)

  case object DsDecU64 extends Opcode("ds_dec_u64", 218)

  case object DsDirectLoad extends Opcode("ds_direct_load", 219)

  case object DsGwsBarrier extends Opcode("ds_gws_barrier", 220)

  case object DsGwsInit extends Opcode("ds_gws_init", 221)

  case object DsGwsSemaBr extends Opcode("ds_gws_sema_br", 222)

  case object DsGwsSemaP extends Opcode("ds_gws_sema_p", 223)

  case object DsGwsSemaReleaseAll extends Opcode("ds_gws_sema_release_all", 224)

  case object DsGwsSemaV extends Opcode("ds_gws_sema_v", 225)

  case object DsIncRtnU32 extends Opcode("ds_inc_rtn_u32", 226)

  case object DsIncRtnU64 extends Opcode("ds_inc_rtn_u64", 227)

  case object DsIncSrc2U32 extends Opcode("ds_inc_src2_u32", 228)

  case object DsIncSrc2U64 extends Opcode("ds_inc_src2_u64", 229)

  case object DsIncU32 extends Opcode("ds_inc_u32", 230)

  case object DsIncU64 extends Opcode("ds_inc_u64", 231)

  case object DsLoad2addrB32 extends Opcode("ds_load_2addr_b32", 232)

  case object DsLoad2addrB64 extends Opcode("ds_load_2addr_b64", 233)

  case object DsLoad2addrStride64B32 extends Opcode("ds_load_2addr_stride64_b32", 234)

  case object DsLoad2addrStride64B64 extends Opcode("ds_load_2addr_stride64_b64", 235)

  case object DsLoadAddtidB32 extends Opcode("ds_load_addtid_b32", 236)

  case object DsLoadB128 extends Opcode("ds_load_b128", 237)

  case object DsLoadB32 extends Opcode("ds_load_b32", 238)

  case object DsLoadB64 extends Opcode("ds_load_b64", 239)

  case object DsLoadB96 extends Opcode("ds_load_b96", 240)

  case object DsLoadI16 extends Opcode("ds_load_i16", 241)

  case object DsLoadI8 extends Opcode("ds_load_i8", 242)

  case object DsLoadI8D16 extends Opcode("ds_load_i8_d16", 243)

  case object DsLoadI8D16Hi extends Opcode("ds_load_i8_d16_hi", 244)

  case object DsLoadTr16B128 extends Opcode("ds_load_tr16_b128", 245)

  case object DsLoadTr4B64 extends Opcode("ds_load_tr4_b64", 246)

  case object DsLoadTr6B96 extends Opcode("ds_load_tr6_b96", 247)

  case object DsLoadTr8B64 extends Opcode("ds_load_tr8_b64", 248)

  case object DsLoadU16 extends Opcode("ds_load_u16", 249)

  case object DsLoadU16D16 extends Opcode("ds_load_u16_d16", 250)

  case object DsLoadU16D16Hi extends Opcode("ds_load_u16_d16_hi", 251)

  case object DsLoadU8 extends Opcode("ds_load_u8", 252)

  case object DsLoadU8D16 extends Opcode("ds_load_u8_d16", 253)

  case object DsLoadU8D16Hi extends Opcode("ds_load_u8_d16_hi", 254)

  case object DsMaxF32 extends Opcode("ds_max_f32", 255)

  case object DsMaxF64 extends Opcode("ds_max_f64", 256)

  case object DsMaxI32 extends Opcode("ds_max_i32", 257)

  case object DsMaxI64 extends Opcode("ds_max_i64", 258)

  case object DsMaxNumF32 extends Opcode("ds_max_num_f32", 259)

  case object DsMaxNumF64 extends Opcode("ds_max_num_f64", 260)

  case object DsMaxNumRtnF32 extends Opcode("ds_max_num_rtn_f32", 261)

  case object DsMaxNumRtnF64 extends Opcode("ds_max_num_rtn_f64", 262)

  case object DsMaxRtnF32 extends Opcode("ds_max_rtn_f32", 263)

  case object DsMaxRtnF64 extends Opcode("ds_max_rtn_f64", 264)

  case object DsMaxRtnI32 extends Opcode("ds_max_rtn_i32", 265)

  case object DsMaxRtnI64 extends Opcode("ds_max_rtn_i64", 266)

  case object DsMaxRtnU32 extends Opcode("ds_max_rtn_u32", 267)

  case object DsMaxRtnU64 extends Opcode("ds_max_rtn_u64", 268)

  case object DsMaxSrc2F32 extends Opcode("ds_max_src2_f32", 269)

  case object DsMaxSrc2F64 extends Opcode("ds_max_src2_f64", 270)

  case object DsMaxSrc2I32 extends Opcode("ds_max_src2_i32", 271)

  case object DsMaxSrc2I64 extends Opcode("ds_max_src2_i64", 272)

  case object DsMaxSrc2U32 extends Opcode("ds_max_src2_u32", 273)

  case object DsMaxSrc2U64 extends Opcode("ds_max_src2_u64", 274)

  case object DsMaxU32 extends Opcode("ds_max_u32", 275)

  case object DsMaxU64 extends Opcode("ds_max_u64", 276)

  case object DsMinF32 extends Opcode("ds_min_f32", 277)

  case object DsMinF64 extends Opcode("ds_min_f64", 278)

  case object DsMinI32 extends Opcode("ds_min_i32", 279)

  case object DsMinI64 extends Opcode("ds_min_i64", 280)

  case object DsMinNumF32 extends Opcode("ds_min_num_f32", 281)

  case object DsMinNumF64 extends Opcode("ds_min_num_f64", 282)

  case object DsMinNumRtnF32 extends Opcode("ds_min_num_rtn_f32", 283)

  case object DsMinNumRtnF64 extends Opcode("ds_min_num_rtn_f64", 284)

  case object DsMinRtnF32 extends Opcode("ds_min_rtn_f32", 285)

  case object DsMinRtnF64 extends Opcode("ds_min_rtn_f64", 286)

  case object DsMinRtnI32 extends Opcode("ds_min_rtn_i32", 287)

  case object DsMinRtnI64 extends Opcode("ds_min_rtn_i64", 288)

  case object DsMinRtnU32 extends Opcode("ds_min_rtn_u32", 289)

  case object DsMinRtnU64 extends Opcode("ds_min_rtn_u64", 290)

  case object DsMinSrc2F32 extends Opcode("ds_min_src2_f32", 291)

  case object DsMinSrc2F64 extends Opcode("ds_min_src2_f64", 292)

  case object DsMinSrc2I32 extends Opcode("ds_min_src2_i32", 293)

  case object DsMinSrc2I64 extends Opcode("ds_min_src2_i64", 294)

  case object DsMinSrc2U32 extends Opcode("ds_min_src2_u32", 295)

  case object DsMinSrc2U64 extends Opcode("ds_min_src2_u64", 296)

  case object DsMinU32 extends Opcode("ds_min_u32", 297)

  case object DsMinU64 extends Opcode("ds_min_u64", 298)

  case object DsMskorB32 extends Opcode("ds_mskor_b32", 299)

  case object DsMskorB64 extends Opcode("ds_mskor_b64", 300)

  case object DsMskorRtnB32 extends Opcode("ds_mskor_rtn_b32", 301)

  case object DsMskorRtnB64 extends Opcode("ds_mskor_rtn_b64", 302)

  case object DsNop extends Opcode("ds_nop", 303)

  case object DsOrB32 extends Opcode("ds_or_b32", 304)

  case object DsOrB64 extends Opcode("ds_or_b64", 305)

  case object DsOrRtnB32 extends Opcode("ds_or_rtn_b32", 306)

  case object DsOrRtnB64 extends Opcode("ds_or_rtn_b64", 307)

  case object DsOrSrc2B32 extends Opcode("ds_or_src2_b32", 308)

  case object DsOrSrc2B64 extends Opcode("ds_or_src2_b64", 309)

  case object DsOrderedCount extends Opcode("ds_ordered_count", 310)

  case object DsParamLoad extends Opcode("ds_param_load", 311)

  case object DsPermuteB32 extends Opcode("ds_permute_b32", 312)

  case object DsPkAddBf16 extends Opcode("ds_pk_add_bf16", 313)

  case object DsPkAddF16 extends Opcode("ds_pk_add_f16", 314)

  case object DsPkAddRtnBf16 extends Opcode("ds_pk_add_rtn_bf16", 315)

  case object DsPkAddRtnF16 extends Opcode("ds_pk_add_rtn_f16", 316)

  case object DsRead2B32 extends Opcode("ds_read2_b32", 317)

  case object DsRead2B64 extends Opcode("ds_read2_b64", 318)

  case object DsRead2st64B32 extends Opcode("ds_read2st64_b32", 319)

  case object DsRead2st64B64 extends Opcode("ds_read2st64_b64", 320)

  case object DsReadAddtidB32 extends Opcode("ds_read_addtid_b32", 321)

  case object DsReadB128 extends Opcode("ds_read_b128", 322)

  case object DsReadB32 extends Opcode("ds_read_b32", 323)

  case object DsReadB64 extends Opcode("ds_read_b64", 324)

  case object DsReadB64TrB16 extends Opcode("ds_read_b64_tr_b16", 325)

  case object DsReadB64TrB4 extends Opcode("ds_read_b64_tr_b4", 326)

  case object DsReadB64TrB8 extends Opcode("ds_read_b64_tr_b8", 327)

  case object DsReadB96 extends Opcode("ds_read_b96", 328)

  case object DsReadB96TrB6 extends Opcode("ds_read_b96_tr_b6", 329)

  case object DsReadI16 extends Opcode("ds_read_i16", 330)

  case object DsReadI8 extends Opcode("ds_read_i8", 331)

  case object DsReadI8D16 extends Opcode("ds_read_i8_d16", 332)

  case object DsReadI8D16Hi extends Opcode("ds_read_i8_d16_hi", 333)

  case object DsReadU16 extends Opcode("ds_read_u16", 334)

  case object DsReadU16D16 extends Opcode("ds_read_u16_d16", 335)

  case object DsReadU16D16Hi extends Opcode("ds_read_u16_d16_hi", 336)

  case object DsReadU8 extends Opcode("ds_read_u8", 337)

  case object DsReadU8D16 extends Opcode("ds_read_u8_d16", 338)

  case object DsReadU8D16Hi extends Opcode("ds_read_u8_d16_hi", 339)

  case object DsRsubRtnU32 extends Opcode("ds_rsub_rtn_u32", 340)

  case object DsRsubRtnU64 extends Opcode("ds_rsub_rtn_u64", 341)

  case object DsRsubSrc2U32 extends Opcode("ds_rsub_src2_u32", 342)

  case object DsRsubSrc2U64 extends Opcode("ds_rsub_src2_u64", 343)

  case object DsRsubU32 extends Opcode("ds_rsub_u32", 344)

  case object DsRsubU64 extends Opcode("ds_rsub_u64", 345)

  case object DsStore2addrB32 extends Opcode("ds_store_2addr_b32", 346)

  case object DsStore2addrB64 extends Opcode("ds_store_2addr_b64", 347)

  case object DsStore2addrStride64B32 extends Opcode("ds_store_2addr_stride64_b32", 348)

  case object DsStore2addrStride64B64 extends Opcode("ds_store_2addr_stride64_b64", 349)

  case object DsStoreAddtidB32 extends Opcode("ds_store_addtid_b32", 350)

  case object DsStoreB128 extends Opcode("ds_store_b128", 351)

  case object DsStoreB16 extends Opcode("ds_store_b16", 352)

  case object DsStoreB16D16Hi extends Opcode("ds_store_b16_d16_hi", 353)

  case object DsStoreB32 extends Opcode("ds_store_b32", 354)

  case object DsStoreB64 extends Opcode("ds_store_b64", 355)

  case object DsStoreB8 extends Opcode("ds_store_b8", 356)

  case object DsStoreB8D16Hi extends Opcode("ds_store_b8_d16_hi", 357)

  case object DsStoreB96 extends Opcode("ds_store_b96", 358)

  case object DsStorexchg2addrRtnB32 extends Opcode("ds_storexchg_2addr_rtn_b32", 359)

  case object DsStorexchg2addrRtnB64 extends Opcode("ds_storexchg_2addr_rtn_b64", 360)

  case object DsStorexchg2addrStride64RtnB32 extends Opcode("ds_storexchg_2addr_stride64_rtn_b32", 361)

  case object DsStorexchg2addrStride64RtnB64 extends Opcode("ds_storexchg_2addr_stride64_rtn_b64", 362)

  case object DsStorexchgRtnB32 extends Opcode("ds_storexchg_rtn_b32", 363)

  case object DsStorexchgRtnB64 extends Opcode("ds_storexchg_rtn_b64", 364)

  case object DsSubClampRtnU32 extends Opcode("ds_sub_clamp_rtn_u32", 365)

  case object DsSubClampU32 extends Opcode("ds_sub_clamp_u32", 366)

  case object DsSubGsRegRtn extends Opcode("ds_sub_gs_reg_rtn", 367)

  case object DsSubRtnU32 extends Opcode("ds_sub_rtn_u32", 368)

  case object DsSubRtnU64 extends Opcode("ds_sub_rtn_u64", 369)

  case object DsSubSrc2U32 extends Opcode("ds_sub_src2_u32", 370)

  case object DsSubSrc2U64 extends Opcode("ds_sub_src2_u64", 371)

  case object DsSubU32 extends Opcode("ds_sub_u32", 372)

  case object DsSubU64 extends Opcode("ds_sub_u64", 373)

  case object DsSwizzleB32 extends Opcode("ds_swizzle_b32", 374)

  case object DsWrapRtnB32 extends Opcode("ds_wrap_rtn_b32", 375)

  case object DsWrite2B32 extends Opcode("ds_write2_b32", 376)

  case object DsWrite2B64 extends Opcode("ds_write2_b64", 377)

  case object DsWrite2st64B32 extends Opcode("ds_write2st64_b32", 378)

  case object DsWrite2st64B64 extends Opcode("ds_write2st64_b64", 379)

  case object DsWriteAddtidB32 extends Opcode("ds_write_addtid_b32", 380)

  case object DsWriteB128 extends Opcode("ds_write_b128", 381)

  case object DsWriteB16 extends Opcode("ds_write_b16", 382)

  case object DsWriteB16D16Hi extends Opcode("ds_write_b16_d16_hi", 383)

  case object DsWriteB32 extends Opcode("ds_write_b32", 384)

  case object DsWriteB64 extends Opcode("ds_write_b64", 385)

  case object DsWriteB8 extends Opcode("ds_write_b8", 386)

  case object DsWriteB8D16Hi extends Opcode("ds_write_b8_d16_hi", 387)

  case object DsWriteB96 extends Opcode("ds_write_b96", 388)

  case object DsWriteSrc2B32 extends Opcode("ds_write_src2_b32", 389)

  case object DsWriteSrc2B64 extends Opcode("ds_write_src2_b64", 390)

  case object DsWrxchg2RtnB32 extends Opcode("ds_wrxchg2_rtn_b32", 391)

  case object DsWrxchg2RtnB64 extends Opcode("ds_wrxchg2_rtn_b64", 392)

  case object DsWrxchg2st64RtnB32 extends Opcode("ds_wrxchg2st64_rtn_b32", 393)

  case object DsWrxchg2st64RtnB64 extends Opcode("ds_wrxchg2st64_rtn_b64", 394)

  case object DsWrxchgRtnB32 extends Opcode("ds_wrxchg_rtn_b32", 395)

  case object DsWrxchgRtnB64 extends Opcode("ds_wrxchg_rtn_b64", 396)

  case object DsXorB32 extends Opcode("ds_xor_b32", 397)

  case object DsXorB64 extends Opcode("ds_xor_b64", 398)

  case object DsXorRtnB32 extends Opcode("ds_xor_rtn_b32", 399)

  case object DsXorRtnB64 extends Opcode("ds_xor_rtn_b64", 400)

  case object DsXorSrc2B32 extends Opcode("ds_xor_src2_b32", 401)

  case object DsXorSrc2B64 extends Opcode("ds_xor_src2_b64", 402)

  val definitions: Vec[Opcode] = Vec(
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
