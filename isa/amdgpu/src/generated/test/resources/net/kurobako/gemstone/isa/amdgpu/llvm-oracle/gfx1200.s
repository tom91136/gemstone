// Generated from pinned LLVM MC/AMDGPU checks. Do not edit.
// TARGET: gfx1200

// SOURCE: ds_swizzle.s

ds_swizzle_b32 v5, v1 offset:swizzle(ROTATE,0,0)
// ENCODING: [0x00, 0xc0, 0xd4, 0xd8, 0x01, 0x00, 0x00, 0x05]

ds_swizzle_b32 v5, v1 offset:swizzle(ROTATE,2,31)
// ERROR: direction must be 0 (left) or 1 (right)

// SOURCE: exp.s

export mrtz, v4, v3, v2, v1
// ENCODING: [0x8f, 0x00, 0x00, 0xf8, 0x04, 0x03, 0x02, 0x01]

// SOURCE: gfx11_asm_vop2_fake16_promote.s

v_add_f16_e64 v5, v1, v255
// ENCODING: [0x05, 0x00, 0x32, 0xd5, 0x01, 0xff, 0x03, 0x02]

// SOURCE: gfx1250_asm_ds.s

ds_atomic_async_barrier_arrive_b64 v1 offset:65407
// ERROR: instruction not supported on this GPU

// SOURCE: gfx1250_asm_operands.s

s_getreg_b32 s1, hwreg(HW_REG_XNACK_STATE_PRIV)
// ERROR: invalid hardware register: not supported on this GPU

s_mov_b64 s[0:1], src_flat_scratch_base_lo
// ERROR: src_flat_scratch_base_lo register not available on this GPU

// SOURCE: gfx1250_asm_smem.s

s_buffer_load_i8 s5, s[4:7], s0 nv
// ERROR: nv is not supported on this GPU

// SOURCE: gfx1250_asm_sop1.s

s_add_pc_i64 4
// ERROR: instruction not supported on this GPU (gfx1200): s_add_pc_i64

s_rfe_i64 s[2:3]
// ERROR: instruction not supported on this GPU (gfx1200): s_rfe_i64

// SOURCE: gfx1250_asm_sopk.s

s_call_i64 s[0:1], 4660
// ERROR: instruction not supported on this GPU

// SOURCE: gfx1250_asm_sopp.s

s_wait_asynccnt 0xc1d1
// ERROR: instruction not supported on this GPU

// SOURCE: gfx1250_asm_vbuffer_mubuf.s

buffer_load_b32 v5, v1, s[8:11], s3 offen offset:4095 nv
// ERROR: nv is not supported on this GPU

// SOURCE: gfx1250_asm_vflat.s

cluster_load_async_to_lds_b32 v1, v[2:3], off th:TH_LOAD_BYPASS scope:SCOPE_SYS
// ERROR: instruction not supported on this GPU (gfx1200): cluster_load_async_to_lds_b32

cluster_load_b32 v1, v[2:3], off th:TH_LOAD_BYPASS scope:SCOPE_SYS
// ERROR: instruction not supported on this GPU (gfx1200): cluster_load_b32

flat_atomic_add_f32 v0, v1, v2, s[2:3] offset:8000000 scale_offset th:TH_ATOMIC_RETURN
// ERROR: not a valid operand.

flat_atomic_min_num_f64 v[0:1], v[2:3] offset:4095
// ERROR: instruction not supported on this GPU (gfx1200): flat_atomic_min_num_f64

flat_prefetch_b8 v[2:3]
// ERROR: instruction not supported on this GPU (gfx1200): flat_prefetch_b8

global_load_b32 v0, v[2:3], off nv
// ERROR: nv is not supported on this GPU

global_store_async_from_lds_b128 v[2:3], v1, off th:TH_STORE_BYPASS scope:SCOPE_SYS
// ERROR: instruction not supported on this GPU (gfx1200): global_store_async_from_lds_b128

global_store_async_from_lds_b64 v[2:3], v1, off th:TH_STORE_BYPASS scope:SCOPE_SYS
// ERROR: instruction not supported on this GPU (gfx1200): global_store_async_from_lds_b64

// SOURCE: gfx1250_asm_vop1_dpp16-fake16.s

v_cos_bf16 v5, v1 quad_perm:[3,2,1,0]
// ERROR: instruction not supported on this GPU (gfx1200): v_cos_bf16

v_cvt_f32_bf16 v5, v1 quad_perm:[3,2,1,0]
// ERROR: instruction not supported on this GPU (gfx1200): v_cvt_f32_bf16

v_tanh_f32 v5, v1 quad_perm:[0,1,2,3]
// ERROR: instruction not supported on this GPU (gfx1200): v_tanh_f32

// SOURCE: gfx1250_asm_vop1_dpp16.s

v_tanh_f16 v5.l, v1.l row_share:0 row_mask:0xf bank_mask:0xf
// ERROR: instruction not supported on this GPU (gfx1200): v_tanh_f16

// SOURCE: gfx1250_asm_vop1_dpp8-fake16.s

v_tanh_f32 v5, v1 dpp8:[7,6,5,4,3,2,1,0] fi:1
// ERROR: instruction not supported on this GPU (gfx1200): v_tanh_f32

// SOURCE: gfx1250_asm_vop1_dpp8.s

v_rsq_bf16 v127.l, v127.l dpp8:[0,0,0,0,0,0,0,0] fi:0
// ERROR: instruction not supported on this GPU (gfx1200): v_rsq_bf16

// SOURCE: gfx1250_asm_vop2.s

v_fmac_f64 v[4:5], v[2:3], 0
// ERROR: instruction not supported on this GPU (gfx1200): v_fmac_f64

v_mul_u64 v[4:5], v[2:3], v[4:5]
// ERROR: instruction not supported on this GPU (gfx1200): v_mul_u64

// SOURCE: gfx1250_asm_vop3_dpp16-fake16.s

v_ashr_pk_u8_i32 v2, v4, v7, v8 quad_perm:[1,2,3,1]
// ERROR: instruction not supported on this GPU (gfx1200): v_ashr_pk_u8_i32

v_bitop3_b32_e64_dpp v5, v1, v2, v3 bitop3:161 quad_perm:[0,1,2,3]
// ERROR: instruction not supported on this GPU (gfx1200): v_bitop3_b32

v_cvt_pk_bf8_f16 v1, v2 op_sel:[0,0] quad_perm:[1,2,3,0]
// ERROR: instruction not supported on this GPU (gfx1200): v_cvt_pk_bf8_f16

v_cvt_pk_fp8_f16 v1, v2 op_sel:[0,0] quad_perm:[1,2,3,0]
// ERROR: instruction not supported on this GPU (gfx1200): v_cvt_pk_fp8_f16

// SOURCE: gfx1250_asm_vop3_dpp16.s

v_bitop3_b16_e64_dpp v5.l, v1.l, v2.l, exec_lo row_share:0 row_mask:0xf bank_mask:0xf
// ERROR: instruction not supported on this GPU (gfx1200): v_bitop3_b16

// SOURCE: gfx1250_asm_vop3_dpp8-fake16.s

v_bitop3_b32_e64_dpp v5, v1, v2, v255 bitop3:161 dpp8:[7,6,5,4,3,2,1,0]
// ERROR: instruction not supported on this GPU (gfx1200): v_bitop3_b32

// SOURCE: gfx1250_asm_vop3_dpp8.s

v_bitop3_b16_e64_dpp v5.h, v1.h, v2.h, exec_hi bitop3:88 op_sel:[1,1,1,1] dpp8:[7,6,5,4,3,2,1,0]
// ERROR: instruction not supported on this GPU (gfx1200): v_bitop3_b16

// SOURCE: gfx1250_asm_vop3_from_vop1_dpp16-fake16.s

v_tanh_f32_e64_dpp v5, v1 quad_perm:[0,1,2,3]
// ERROR: instruction not supported on this GPU (gfx1200): v_tanh_f32

// SOURCE: gfx1250_asm_vop3_from_vop1_dpp16.s

v_tanh_f16_e64_dpp v5.l, v1.l row_share:0 row_mask:0xf bank_mask:0xf
// ERROR: instruction not supported on this GPU (gfx1200): v_tanh_f16

// SOURCE: gfx1250_asm_vop3_from_vop1_dpp8-fake16.s

v_tanh_f32_e64_dpp v5, v1 mul:2 dpp8:[7,6,5,4,3,2,1,0]
// ERROR: instruction not supported on this GPU (gfx1200): v_tanh_f32

// SOURCE: gfx1250_asm_vop3_from_vop1_dpp8.s

v_cos_bf16_e64_dpp v5.l, v1.l dpp8:[7,6,5,4,3,2,1,0]
// ERROR: instruction not supported on this GPU (gfx1200): v_cos_bf16

// SOURCE: gfx1250_asm_vop3p.s

v_pk_add_max_i16 v10, v1, v2, v3
// ERROR: instruction not supported on this GPU (gfx1200): v_pk_add_max_i16

v_pk_fma_f32 v[8:9], v[0:1], v[2:3], v[4:5] op_sel_hi:[0,0,0]
// ERROR: instruction not supported on this GPU (gfx1200): v_pk_fma_f32

v_pk_min3_num_f16 v8, v1, s1, v4
// ERROR: instruction not supported on this GPU (gfx1200): v_pk_min3_num_f16

v_pk_min3_u16 v10, v1, v2, v3
// ERROR: instruction not supported on this GPU (gfx1200): v_pk_min3_u16

// SOURCE: gfx1250_asm_vop3p_dpp16.s

v_fma_mixhi_bf16 v0, v1, v2, v3 op_sel_hi:[1,1,1] clamp quad_perm:[0,2,3,1] row_mask:0x0
// ERROR: instruction not supported on this GPU (gfx1200): v_fma_mixhi_bf16

// SOURCE: gfx1250_asm_vop3p_dpp8.s

v_fma_mix_f32_bf16 v0, v1, v2, v3 dpp8:[2,2,2,2,4,4,4,4]
// ERROR: instruction not supported on this GPU (gfx1200): v_fma_mix_f32_bf16

// SOURCE: gfx1250_asm_wmma_w32.s

v_swmmac_bf16_16x16x64_bf16 v[24:27], v[0:7], v[8:23], v28
// ERROR: instruction not supported on this GPU (gfx1200): v_swmmac_bf16_16x16x64_bf16

v_swmmac_f16_16x16x128_bf8_bf8 v[24:27], v[0:7], v[8:23], v[28:29]
// ERROR: instruction not supported on this GPU (gfx1200): v_swmmac_f16_16x16x128_bf8_bf8

v_swmmac_f32_16x16x64_f16 v[24:31], v[0:7], v[8:23], v32
// ERROR: instruction not supported on this GPU (gfx1200): v_swmmac_f32_16x16x64_f16

v_wmma_bf16f32_16x16x32_bf16 v[26:29], v[0:7], v[8:15], v[16:23]
// ERROR: instruction not supported on this GPU (gfx1200): v_wmma_bf16f32_16x16x32_bf16

v_wmma_f16_16x16x128_bf8_bf8 v[16:19], v[0:15], v[8:23], v[16:19]
// ERROR: instruction not supported on this GPU (gfx1200): v_wmma_f16_16x16x128_bf8_bf8

v_wmma_f16_16x16x32_f16 v[16:19], v[0:7], v[8:15], v[16:19]
// ERROR: instruction not supported on this GPU (gfx1200): v_wmma_f16_16x16x32_f16

v_wmma_f32_16x16x64_bf8_bf8 v[16:23], v[0:7], v[8:15], v[16:23]
// ERROR: instruction not supported on this GPU (gfx1200): v_wmma_f32_16x16x64_bf8_bf8

v_wmma_ld_scale_paired_b32 s0, s0 matrix_b_scale:MATRIX_SCALE_ROW1
// ERROR: instruction not supported on this GPU (gfx1200): v_wmma_ld_scale_paired_b32

v_wmma_scale16_f32_32x16x128_f4 v[0:15], v[8:23], v[24:31], v[40:55], v[2:3], v[4:5] matrix_a_scale:MATRIX_SCALE_ROW1 matrix_b_scale:MATRIX_SCALE_ROW1 neg_lo:[0,0,1] neg_hi:[0,0,1]
// ERROR: instruction not supported on this GPU (gfx1200): v_wmma_scale16_f32_32x16x128_f4

v_wmma_scale_f32_32x16x128_f4 v[0:15], v[8:23], v[24:31], v[40:55], v1, v2 matrix_a_scale:MATRIX_SCALE_ROW1 matrix_b_scale:MATRIX_SCALE_ROW1 neg_lo:[0,0,1] neg_hi:[0,0,1]
// ERROR: instruction not supported on this GPU (gfx1200): v_wmma_scale_f32_32x16x128_f4

// SOURCE: gfx12_asm_ds.s

ds_add_f32 v1, v2 offset:65535
// ENCODING: [0xff, 0xff, 0x54, 0xd8, 0x01, 0x02, 0x00, 0x00]

ds_add_u64 v1, v[2:3]
// ENCODING: [0x00, 0x00, 0x00, 0xd9, 0x01, 0x02, 0x00, 0x00]

ds_and_b32 v1, v2
// ENCODING: [0x00, 0x00, 0x24, 0xd8, 0x01, 0x02, 0x00, 0x00]

ds_bpermute_fi_b32 v255, v255, v255 offset:4
// ENCODING: [0x04, 0x00, 0x34, 0xdb, 0xff, 0xff, 0x00, 0xff]

ds_bvh_stack_push4_pop1_rtn_b32 v1, v0, v1, v[2:5]
// ENCODING: [0x00, 0x00, 0x80, 0xdb, 0x00, 0x01, 0x02, 0x01]

ds_bvh_stack_push8_pop1_rtn_b32 v1, v0, v1, v[2:9]
// ENCODING: [0x00, 0x00, 0x84, 0xdb, 0x00, 0x01, 0x02, 0x01]

ds_bvh_stack_push8_pop2_rtn_b64 v[254:255], v253, v252, v[244:251]
// ENCODING: [0x00, 0x00, 0x88, 0xdb, 0xfd, 0xfc, 0xf4, 0xfe]

ds_cmpstore_b32 v1, v2, v3
// ENCODING: [0x00, 0x00, 0x40, 0xd8, 0x01, 0x02, 0x03, 0x00]

ds_cond_sub_rtn_u32 v5, v1, v2
// ENCODING: [0x00, 0x00, 0xa0, 0xda, 0x01, 0x02, 0x00, 0x05]

ds_cond_sub_u32 v1, v2
// ENCODING: [0x00, 0x00, 0x60, 0xda, 0x01, 0x02, 0x00, 0x00]

ds_dec_u64 v1, v[2:3]
// ENCODING: [0x00, 0x00, 0x10, 0xd9, 0x01, 0x02, 0x00, 0x00]

ds_load_b128 v[252:255], v255 offset:4
// ENCODING: [0x04, 0x00, 0xfc, 0xdb, 0xff, 0x00, 0x00, 0xfc]

ds_load_u8 v255, v255 offset:4
// ENCODING: [0x04, 0x00, 0xe8, 0xd8, 0xff, 0x00, 0x00, 0xff]

ds_max_num_f32 v1, v2
// ENCODING: [0x00, 0x00, 0x4c, 0xd8, 0x01, 0x02, 0x00, 0x00]

ds_max_num_f64 v1, v[2:3]
// ENCODING: [0x00, 0x00, 0x4c, 0xd9, 0x01, 0x02, 0x00, 0x00]

ds_max_num_rtn_f32 v255, v255, v255 offset:4
// ENCODING: [0x04, 0x00, 0xcc, 0xd8, 0xff, 0xff, 0x00, 0xff]

ds_max_num_rtn_f64 v[254:255], v255, v[254:255] offset:4
// ENCODING: [0x04, 0x00, 0xcc, 0xd9, 0xff, 0xfe, 0x00, 0xfe]

ds_max_rtn_u32 v255, v255, v255 offset:4
// ENCODING: [0x04, 0x00, 0xa0, 0xd8, 0xff, 0xff, 0x00, 0xff]

ds_min_num_f32 v1, v2
// ENCODING: [0x00, 0x00, 0x48, 0xd8, 0x01, 0x02, 0x00, 0x00]

ds_min_num_f64 v1, v[2:3]
// ENCODING: [0x00, 0x00, 0x48, 0xd9, 0x01, 0x02, 0x00, 0x00]

ds_min_num_rtn_f32 v255, v255, v255 offset:4
// ENCODING: [0x04, 0x00, 0xc8, 0xd8, 0xff, 0xff, 0x00, 0xff]

ds_min_num_rtn_f64 v[254:255], v255, v[254:255] offset:4
// ENCODING: [0x04, 0x00, 0xc8, 0xd9, 0xff, 0xfe, 0x00, 0xfe]

ds_min_rtn_i64 v[254:255], v255, v[254:255] offset:4
// ENCODING: [0x04, 0x00, 0x94, 0xd9, 0xff, 0xfe, 0x00, 0xfe]

ds_min_rtn_u64 v[254:255], v255, v[254:255] offset:4
// ENCODING: [0x04, 0x00, 0x9c, 0xd9, 0xff, 0xfe, 0x00, 0xfe]

ds_or_b64 v1, v[2:3]
// ENCODING: [0x00, 0x00, 0x28, 0xd9, 0x01, 0x02, 0x00, 0x00]

ds_pk_add_bf16 v255, v255
// ENCODING: [0x00, 0x00, 0x6c, 0xda, 0xff, 0xff, 0x00, 0x00]

ds_pk_add_f16 v0, v0
// ENCODING: [0x00, 0x00, 0x68, 0xda, 0x00, 0x00, 0x00, 0x00]

ds_pk_add_rtn_bf16 v255, v0, v200
// ENCODING: [0x00, 0x00, 0xac, 0xda, 0x00, 0xc8, 0x00, 0xff]

ds_pk_add_rtn_f16 v255, v0, v200
// ENCODING: [0x00, 0x00, 0xa8, 0xda, 0x00, 0xc8, 0x00, 0xff]

ds_rsub_u32 v1, v2
// ENCODING: [0x00, 0x00, 0x08, 0xd8, 0x01, 0x02, 0x00, 0x00]

ds_store_b64 v1, v[2:3]
// ENCODING: [0x00, 0x00, 0x34, 0xd9, 0x01, 0x02, 0x00, 0x00]

ds_sub_clamp_rtn_u32 v255, v255, v255 offset:4
// ENCODING: [0x04, 0x00, 0xa4, 0xda, 0xff, 0xff, 0x00, 0xff]

ds_sub_clamp_u32 v1, v2
// ENCODING: [0x00, 0x00, 0x64, 0xda, 0x01, 0x02, 0x00, 0x00]

// SOURCE: gfx12_asm_exp.s

export dual_src_blend0, v0, v0, v0, v0
// ENCODING: [0x5f, 0x01, 0x00, 0xf8, 0x00, 0x00, 0x00, 0x00]

// SOURCE: gfx12_asm_features.s

buffer_load_b32 v5, off, s[8:11], s3 offset:8388607 th:TH_LOAD_NT_HT scope:SCOPE_DEV
// ENCODING: [0x03, 0x00, 0x05, 0xc4, 0x05, 0x10, 0xe8, 0x00, 0x00, 0xff, 0xff, 0x7f]

global_load_block v[9:40], v[5:6], off th:TH_LOAD_HT scope:SCOPE_SE
// ENCODING: [0x7c, 0xc0, 0x14, 0xee, 0x09, 0x00, 0x24, 0x00, 0x05, 0x00, 0x00, 0x00]

image_load v0, v0, s[0:7] dmask:0x1 dim:SQ_RSRC_IMG_1D th:TH_LOAD_HT scope:SCOPE_SE
// ENCODING: [0x00, 0x00, 0x40, 0xd0, 0x00, 0x00, 0x24, 0x00, 0x00, 0x00, 0x00, 0x00]

tbuffer_load_d16_format_x v4, off, ttmp[4:7], s3 format:[BUF_FMT_8_UINT] offset:8388607 th:TH_LOAD_BYPASS scope:SCOPE_SYS
// ENCODING: [0x03, 0x00, 0x22, 0xc4, 0x04, 0xe0, 0xbc, 0x02, 0x00, 0xff, 0xff, 0x7f]

v_add3_u32_e64_dpp v5, v1, 42, v3 quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0x05, 0x00, 0x55, 0xd6, 0xfa, 0x54, 0x0d, 0x04, 0x01, 0x1b, 0x00, 0xff]

v_cmp_le_f32 vcc_lo, v1, v2 row_mirror row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x04, 0x26, 0x7c, 0x01, 0x40, 0x01, 0xff]

// SOURCE: gfx12_asm_global_load_tr.s

global_load_tr_b64 v1, v[3:4], off
// ERROR: operands are not valid for this GPU or mode

// SOURCE: gfx12_asm_mimg_err.s

image_atomic_add_flt v0, v0, s[0:7] dmask:0x1
// ERROR: missing dim operand

// SOURCE: gfx12_asm_smem.s

s_atc_probe 7, s[4:5], 0x64
// ENCODING: [0xc2, 0x41, 0x04, 0xf4, 0x64, 0x00, 0x00, 0xf8]

s_buffer_load_b96 s[20:22], s[4:7], 0x0
// ENCODING: [0x02, 0xa5, 0x02, 0xf4, 0x00, 0x00, 0x00, 0xf8]

s_buffer_load_i16 s101, s[4:7], s0 offset:0x0
// ENCODING: [0x42, 0x59, 0x03, 0xf4, 0x00, 0x00, 0x00, 0x00]

s_buffer_load_i8 s101, s[4:7], s0 offset:0x0
// ENCODING: [0x42, 0x19, 0x03, 0xf4, 0x00, 0x00, 0x00, 0x00]

s_buffer_load_u16 s101, s[4:7], s0 offset:0x0
// ENCODING: [0x42, 0x79, 0x03, 0xf4, 0x00, 0x00, 0x00, 0x00]

s_buffer_load_u8 s101, s[4:7], s0 offset:0x0
// ENCODING: [0x42, 0x39, 0x03, 0xf4, 0x00, 0x00, 0x00, 0x00]

s_buffer_prefetch_data s[20:23], 0x64, null, 7
// ENCODING: [0xca, 0xe1, 0x04, 0xf4, 0x64, 0x00, 0x00, 0xf8]

s_load_b96 null, s[2:3], s0 offset:0x0
// ENCODING: [0x01, 0xbf, 0x00, 0xf4, 0x00, 0x00, 0x00, 0x00]

s_load_i16 s101, s[2:3], s0 offset:0x0
// ENCODING: [0x41, 0x59, 0x01, 0xf4, 0x00, 0x00, 0x00, 0x00]

s_load_i8 s101, s[2:3], s0 offset:0x0
// ENCODING: [0x41, 0x19, 0x01, 0xf4, 0x00, 0x00, 0x00, 0x00]

s_load_u16 s101, s[2:3], s0 offset:0x0
// ENCODING: [0x41, 0x79, 0x01, 0xf4, 0x00, 0x00, 0x00, 0x00]

s_load_u8 s101, s[2:3], s0 offset:0x0
// ENCODING: [0x41, 0x39, 0x01, 0xf4, 0x00, 0x00, 0x00, 0x00]

s_prefetch_data s[18:19], 0x64, s10, 7
// ENCODING: [0xc9, 0xc1, 0x04, 0xf4, 0x64, 0x00, 0x00, 0x14]

s_prefetch_data_pc_rel 0x64, s10, 7
// ENCODING: [0xc0, 0x01, 0x05, 0xf4, 0x64, 0x00, 0x00, 0x14]

s_prefetch_inst s[12:13], 0x10, s4, 2
// ENCODING: [0x86, 0x80, 0x04, 0xf4, 0x10, 0x00, 0x00, 0x08]

s_prefetch_inst_pc_rel 0x64, s10, 7
// ENCODING: [0xc0, 0xa1, 0x04, 0xf4, 0x64, 0x00, 0x00, 0x14]

// SOURCE: gfx12_asm_smem_err.s

s_buffer_load_b256 s4, null, s101
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_sop1.s

s_abs_i32 exec_lo, s1
// ENCODING: [0x01, 0x15, 0xfe, 0xbe]

s_abs_i32 s0, 0.5
// ENCODING: [0xf0, 0x15, 0x80, 0xbe]

s_abs_i32 s0, s1
// ENCODING: [0x01, 0x15, 0x80, 0xbe]

s_abs_i32 vcc_lo, s1
// ENCODING: [0x01, 0x15, 0xea, 0xbe]

s_alloc_vgpr 18
// ENCODING: [0x92, 0x53, 0x80, 0xbe]

s_and_not0_saveexec_b32 s5, ttmp15
// ENCODING: [0x7b, 0x2c, 0x85, 0xbe]

s_and_not1_saveexec_b64 s[0:1], s[2:3]
// ENCODING: [0x02, 0x31, 0x80, 0xbe]

s_barrier_init -1
// ENCODING: [0xc1, 0x51, 0x80, 0xbe]

s_barrier_join -1
// ENCODING: [0xc1, 0x52, 0x80, 0xbe]

s_barrier_signal -2
// ENCODING: [0xc2, 0x4e, 0x80, 0xbe]

s_barrier_signal_isfirst -2
// ENCODING: [0xc2, 0x4f, 0x80, 0xbe]

s_bitset1_b64 s[0:1], s2
// ENCODING: [0x02, 0x13, 0x80, 0xbe]

s_ceil_f16 s5, s1
// ENCODING: [0x01, 0x6b, 0x85, 0xbe]

s_ceil_f32 s5, s1
// ENCODING: [0x01, 0x60, 0x85, 0xbe]

s_clz_i32_u32 s0, s1
// ENCODING: [0x01, 0x0a, 0x80, 0xbe]

s_cvt_f16_f32 s5, s1
// ENCODING: [0x01, 0x68, 0x85, 0xbe]

s_cvt_f32_f16 s5, s1
// ENCODING: [0x01, 0x69, 0x85, 0xbe]

s_cvt_f32_i32 s5, s1
// ENCODING: [0x01, 0x64, 0x85, 0xbe]

s_cvt_f32_u32 s5, exec_lo
// ENCODING: [0x7e, 0x65, 0x85, 0xbe]

s_cvt_hi_f32_f16 s5, s1
// ENCODING: [0x01, 0x6a, 0x85, 0xbe]

s_cvt_i32_f32 s5, s1
// ENCODING: [0x01, 0x66, 0x85, 0xbe]

s_cvt_u32_f32 s5, s1
// ENCODING: [0x01, 0x67, 0x85, 0xbe]

s_floor_f16 s5, s1
// ENCODING: [0x01, 0x6c, 0x85, 0xbe]

s_floor_f32 s5, s1
// ENCODING: [0x01, 0x61, 0x85, 0xbe]

s_get_barrier_state s3, -1
// ENCODING: [0xc1, 0x50, 0x83, 0xbe]

s_mov_b32 s0, s1
// ENCODING: [0x01, 0x00, 0x80, 0xbe]

s_not_b32 m0, s1
// ENCODING: [0x01, 0x1e, 0xfd, 0xbe]

s_or_saveexec_b64 s[0:1], s[2:3]
// ENCODING: [0x02, 0x23, 0x80, 0xbe]

s_rfe_b64 s[0:1]
// ENCODING: [0x00, 0x4a, 0x80, 0xbe]

s_rndne_f16 s5, s1
// ENCODING: [0x01, 0x6e, 0x85, 0xbe]

s_rndne_f32 s5, s1
// ENCODING: [0x01, 0x63, 0x85, 0xbe]

s_sleep_var 0x1234
// ENCODING: [0xff, 0x58, 0x80, 0xbe, 0x34, 0x12, 0x00, 0x00]

s_swappc_b64 s[0:1], s[2:3]
// ENCODING: [0x02, 0x49, 0x80, 0xbe]

s_trunc_f16 s5, s1
// ENCODING: [0x01, 0x6d, 0x85, 0xbe]

s_trunc_f32 s5, s1
// ENCODING: [0x01, 0x62, 0x85, 0xbe]

// SOURCE: gfx12_asm_sop2.s

s_absdiff_i32 s0, s1, s2
// ENCODING: [0x01, 0x02, 0x00, 0x83]

s_add_co_ci_u32 s0, s1, s2
// ENCODING: [0x01, 0x02, 0x00, 0x82]

s_add_co_i32 s0, s1, s2
// ENCODING: [0x01, 0x02, 0x00, 0x81]

s_add_co_u32 s0, s1, s2
// ENCODING: [0x01, 0x02, 0x00, 0x80]

s_add_f16 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x85, 0xa4]

s_add_f32 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x05, 0xa0]

s_add_nc_u64 s[100:101], s[102:103], s[104:105]
// ENCODING: [0x66, 0x68, 0xe4, 0xa9]

s_and_b64 s[0:1], s[2:3], s[4:5]
// ENCODING: [0x02, 0x04, 0x80, 0x8b]

s_bfe_u64 s[0:1], s[2:3], s4
// ENCODING: [0x02, 0x04, 0x00, 0x94]

s_cvt_pk_rtz_f16_f32 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x05, 0xa4]

s_fmaak_f32 s5, s1, s2, 0x11213141
// ENCODING: [0x01, 0x02, 0x85, 0xa2, 0x41, 0x31, 0x21, 0x11]

s_fmac_f16 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x05, 0xa7]

s_fmac_f32 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x85, 0xa3]

s_fmamk_f32 s5, s1, 0x11213141, s3
// ENCODING: [0x01, 0x03, 0x05, 0xa3, 0x41, 0x31, 0x21, 0x11]

s_max_num_f16 s5, 0.5, s2
// ENCODING: [0xf0, 0x02, 0x05, 0xa6]

s_max_num_f32 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x85, 0xa1]

s_maximum_f16 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x05, 0xa9]

s_maximum_f32 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x05, 0xa8]

s_min_num_f16 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x85, 0xa5]

s_min_num_f32 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x05, 0xa1]

s_minimum_f16 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x85, 0xa8]

s_minimum_f32 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x85, 0xa7]

s_mul_f16 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x85, 0xa6]

s_mul_f32 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x05, 0xa2]

s_mul_i32 s0, s1, s2
// ENCODING: [0x01, 0x02, 0x00, 0x96]

s_mul_u64 s[0:1], s[2:3], s[4:5]
// ENCODING: [0x02, 0x04, 0x80, 0xaa]

s_or_not1_b64 s[0:1], s[2:3], s[4:5]
// ENCODING: [0x02, 0x04, 0x80, 0x92]

s_sub_co_ci_u32 s0, s1, s2
// ENCODING: [0x01, 0x02, 0x80, 0x82]

s_sub_co_i32 s0, s1, s2
// ENCODING: [0x01, 0x02, 0x80, 0x81]

s_sub_co_u32 s0, s1, s2
// ENCODING: [0x01, 0x02, 0x80, 0x80]

s_sub_f16 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x05, 0xa5]

s_sub_f32 s5, s1, s2
// ENCODING: [0x01, 0x02, 0x85, 0xa0]

s_sub_nc_u64 s[104:105], s[102:103], s[2:3]
// ENCODING: [0x66, 0x02, 0x68, 0xaa]

s_xnor_b32 s0, s1, s2
// ENCODING: [0x01, 0x02, 0x00, 0x90]

// SOURCE: gfx12_asm_sopc.s

s_bitcmp0_b32 s0, s1
// ENCODING: [0x00, 0x01, 0x0c, 0xbf]

s_cmp_eq_f16 s1, s2
// ENCODING: [0x01, 0x02, 0x52, 0xbf]

s_cmp_eq_f32 s1, s2
// ENCODING: [0x01, 0x02, 0x42, 0xbf]

s_cmp_eq_u32 s0, s1
// ENCODING: [0x00, 0x01, 0x06, 0xbf]

s_cmp_ge_f16 s1, s2
// ENCODING: [0x01, 0x02, 0x56, 0xbf]

s_cmp_ge_f32 s1, s2
// ENCODING: [0x01, 0x02, 0x46, 0xbf]

s_cmp_gt_f16 s1, s2
// ENCODING: [0x01, 0x02, 0x54, 0xbf]

s_cmp_gt_f32 s1, s2
// ENCODING: [0x01, 0x02, 0x44, 0xbf]

s_cmp_le_f16 s1, s2
// ENCODING: [0x01, 0x02, 0x53, 0xbf]

s_cmp_le_f32 s105, s2
// ENCODING: [0x69, 0x02, 0x43, 0xbf]

s_cmp_lg_f16 s1, s2
// ENCODING: [0x01, 0x02, 0x55, 0xbf]

s_cmp_lg_f32 s1, s2
// ENCODING: [0x01, 0x02, 0x45, 0xbf]

s_cmp_lt_f16 s1, s2
// ENCODING: [0x01, 0x02, 0x51, 0xbf]

s_cmp_lt_f32 s105, s2
// ENCODING: [0x69, 0x02, 0x41, 0xbf]

s_cmp_lt_u32 s0, s1
// ENCODING: [0x00, 0x01, 0x0a, 0xbf]

s_cmp_neq_f16 s1, s2
// ENCODING: [0x01, 0x02, 0x5d, 0xbf]

s_cmp_neq_f32 s1, s2
// ENCODING: [0x01, 0x02, 0x4d, 0xbf]

s_cmp_nge_f16 s1, s2
// ENCODING: [0x01, 0x02, 0x59, 0xbf]

s_cmp_nge_f32 s1, s2
// ENCODING: [0x01, 0x02, 0x49, 0xbf]

s_cmp_ngt_f16 s101, s2
// ENCODING: [0x65, 0x02, 0x5b, 0xbf]

s_cmp_ngt_f32 s1, s2
// ENCODING: [0x01, 0x02, 0x4b, 0xbf]

s_cmp_nle_f16 s1, s2
// ENCODING: [0x01, 0x02, 0x5c, 0xbf]

s_cmp_nle_f32 s1, s2
// ENCODING: [0x01, 0x02, 0x4c, 0xbf]

s_cmp_nlg_f16 s1, s2
// ENCODING: [0x01, 0x02, 0x5a, 0xbf]

s_cmp_nlg_f32 s1, s2
// ENCODING: [0x01, 0x02, 0x4a, 0xbf]

s_cmp_nlt_f16 s1, s2
// ENCODING: [0x01, 0x02, 0x5e, 0xbf]

s_cmp_nlt_f32 s1, s2
// ENCODING: [0x01, 0x02, 0x4e, 0xbf]

s_cmp_o_f16 s1, s2
// ENCODING: [0x01, 0x02, 0x57, 0xbf]

s_cmp_o_f32 s1, s2
// ENCODING: [0x01, 0x02, 0x47, 0xbf]

s_cmp_u_f16 s1, s2
// ENCODING: [0x01, 0x02, 0x58, 0xbf]

s_cmp_u_f32 s1, s2
// ENCODING: [0x01, 0x02, 0x48, 0xbf]

// SOURCE: gfx12_asm_sopk_alias.s

s_addk_co_i32 s0, 0x1234
// ENCODING: [0x34, 0x12, 0x80, 0xb7]

s_getreg_b32 s0, hwreg(HW_REG_WAVE_DVGPR_ALLOC_LO)
// ENCODING: [0x1f, 0xf8, 0x80, 0xb8]

// SOURCE: gfx12_asm_sopp.s

s_barrier_leave
// ENCODING: [0x00, 0x00, 0x95, 0xbf]

s_barrier_wait 0xffff
// ENCODING: [0xff, 0xff, 0x94, 0xbf]

s_denorm_mode 0
// ENCODING: [0x00, 0x00, 0x92, 0xbf]

s_setprio 0
// ENCODING: [0x00, 0x00, 0xb5, 0xbf]

s_wait_alu depctr_hold_cnt(0) depctr_sa_sdst(0) depctr_va_vdst(0) depctr_va_sdst(0) depctr_va_ssrc(0) depctr_va_vcc(0) depctr_vm_vsrc(0)
// ENCODING: [0x00, 0x00, 0x88, 0xbf]

s_wait_bvhcnt 0x1234
// ENCODING: [0x34, 0x12, 0xc3, 0xbf]

s_wait_dscnt 0x1234
// ENCODING: [0x34, 0x12, 0xc6, 0xbf]

s_wait_expcnt 0x1234
// ENCODING: [0x34, 0x12, 0xc4, 0xbf]

s_wait_idle
// ENCODING: [0x00, 0x00, 0x8a, 0xbf]

s_wait_kmcnt 0x1234
// ENCODING: [0x34, 0x12, 0xc7, 0xbf]

s_wait_loadcnt 0x1234
// ENCODING: [0x34, 0x12, 0xc0, 0xbf]

s_wait_loadcnt_dscnt 0x1234
// ENCODING: [0x34, 0x12, 0xc8, 0xbf]

s_wait_samplecnt 0x1234
// ENCODING: [0x34, 0x12, 0xc2, 0xbf]

s_wait_storecnt 0x1234
// ENCODING: [0x34, 0x12, 0xc1, 0xbf]

s_wait_storecnt_dscnt 0x1234
// ENCODING: [0x34, 0x12, 0xc9, 0xbf]

// SOURCE: gfx12_asm_vbuffer_mtbuf.s

tbuffer_load_d16_format_xy v4, off, ttmp[4:7], 0 format:[BUF_DATA_FORMAT_16, BUF_NUM_FORMAT_SSCALED] offset:8388607
// ERROR: operands are not valid for this GPU or mode

// SOURCE: gfx12_asm_vbuffer_mtbuf_alias.s

tbuffer_load_d16_format_x v4, off, s[8:11], s3 offset:8388607
// ENCODING: [0x03, 0x00, 0x22, 0xc4, 0x04, 0x10, 0x80, 0x00, 0x00, 0xff, 0xff, 0x7f]

tbuffer_load_d16_format_xyzw v[4:5], off, s[8:11], s3 format:[BUF_FMT_8_8_USCALED] offset:8388607
// ENCODING: [0x03, 0xc0, 0x22, 0xc4, 0x04, 0x10, 0x00, 0x08, 0x00, 0xff, 0xff, 0x7f]

// SOURCE: gfx12_asm_vbuffer_mtbuf_err.s

tbuffer_load_format_d16_xyz v[3:5], v0, null, s1 offen offset:4095
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vbuffer_mubuf.s

buffer_load_format_x v5, off, s[8:11], s3 offset:8388607 slc
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vbuffer_mubuf_alias.s

buffer_atomic_add_u32 v5, off, s[8:11], s3 offset:8388607
// ENCODING: [0x03, 0x40, 0x0d, 0xc4, 0x05, 0x10, 0x80, 0x00, 0x00, 0xff, 0xff, 0x7f]

buffer_atomic_add_u64 v[5:6], off, s[8:11], s3 offset:8388607
// ENCODING: [0x03, 0xc0, 0x10, 0xc4, 0x05, 0x10, 0x80, 0x00, 0x00, 0xff, 0xff, 0x7f]

buffer_atomic_max_i64 v[5:6], off, s[8:11], s3 offset:8388607
// ENCODING: [0x03, 0xc0, 0x11, 0xc4, 0x05, 0x10, 0x80, 0x00, 0x00, 0xff, 0xff, 0x7f]

buffer_atomic_max_num_f32 v5, off, s[8:11], s3 offset:8388607
// ENCODING: [0x03, 0x80, 0x14, 0xc4, 0x05, 0x10, 0x80, 0x00, 0x00, 0xff, 0xff, 0x7f]

buffer_atomic_min_num_f32 v5, off, s[8:11], s3 offset:8388607
// ENCODING: [0x03, 0x40, 0x14, 0xc4, 0x05, 0x10, 0x80, 0x00, 0x00, 0xff, 0xff, 0x7f]

buffer_atomic_sub_clamp_u32 v5, off, s[8:11], s3 offset:8388607
// ENCODING: [0x03, 0xc0, 0x0d, 0xc4, 0x05, 0x10, 0x80, 0x00, 0x00, 0xff, 0xff, 0x7f]

buffer_load_d16_b16 v5, off, s[8:11], s3 offset:8388607
// ENCODING: [0x03, 0x00, 0x08, 0xc4, 0x05, 0x10, 0x80, 0x00, 0x00, 0xff, 0xff, 0x7f]

buffer_load_i16 v5, off, s[8:11], s3 offset:8388607
// ENCODING: [0x03, 0xc0, 0x04, 0xc4, 0x05, 0x10, 0x80, 0x00, 0x00, 0xff, 0xff, 0x7f]

buffer_store_d16_format_xy v1, off, s[12:15], s4 offset:8388607
// ENCODING: [0x04, 0x40, 0x03, 0xc4, 0x01, 0x18, 0x80, 0x00, 0x00, 0xff, 0xff, 0x7f]

// SOURCE: gfx12_asm_vbuffer_mubuf_err.s

buffer_atomic_xor_b64 v[5:6], v0, null, s3 idxen
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vdsdir.s

ds_direct_load v9 wait_va_vdst:7 wait_vm_vsrc:0
// ENCODING: [0x09, 0x00, 0x17, 0xce]

ds_param_load v1, attr0.x wait_va_vdst:15 wait_vm_vsrc:0
// ENCODING: [0x01, 0x00, 0x0f, 0xce]

// SOURCE: gfx12_asm_vdsdir_alias.s

ds_direct_load v0 wait_va_vdst:0 wait_vm_vsrc:0
// ENCODING: [0x00, 0x00, 0x10, 0xce]

// SOURCE: gfx12_asm_vop1-fake16.s

v_bfrev_b32_e32 v5, -1
// ENCODING: [0xc1, 0x70, 0x0a, 0x7e]

v_cvt_f64_i32_e32 v[254:255], 0xaf123456
// ENCODING: [0xff, 0x08, 0xfc, 0x7f, 0x56, 0x34, 0x12, 0xaf]

v_permlane64_b32 v255, v255
// ENCODING: [0xff, 0xcf, 0xfe, 0x7f]

v_rsq_f64_e32 v[254:255], 0xaf123456
// ENCODING: [0xff, 0x62, 0xfc, 0x7f, 0x56, 0x34, 0x12, 0xaf]

// SOURCE: gfx12_asm_vop1.s

v_ceil_f16_e32 v5.l, v127.h
// ENCODING: [0xff, 0xb9, 0x0a, 0x7e]

// SOURCE: gfx12_asm_vop1_dpp16-fake16.s

v_bfrev_b32_dpp v5, v1 row_mirror row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x70, 0x0a, 0x7e, 0x01, 0x40, 0x01, 0xff]

v_cls_i32_dpp v5, v1 quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x76, 0x0a, 0x7e, 0x01, 0x1b, 0x00, 0xff]

v_cos_f16_dpp v5, v1 quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0xc2, 0x0a, 0x7e, 0x01, 0x1b, 0x00, 0xff]

v_cvt_f32_bf8_dpp v1, v3 quad_perm:[0,1,2,3] row_mask:0xa bank_mask:0xc
// ENCODING: [0xfa, 0xda, 0x02, 0x7e, 0x03, 0xe4, 0x00, 0xac]

v_exp_f16_dpp v5, v1 quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0xb0, 0x0a, 0x7e, 0x01, 0x1b, 0x00, 0xff]

v_frexp_exp_i32_f32_dpp v5, v1 quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x7e, 0x0a, 0x7e, 0x01, 0x1b, 0x00, 0xff]

v_movrels_b32_dpp v5, v1 quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x86, 0x0a, 0x7e, 0x01, 0x1b, 0x00, 0xff]

v_rndne_f16_dpp v5, v1 quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0xbc, 0x0a, 0x7e, 0x01, 0x1b, 0x00, 0xff]

v_sin_f32_dpp v5, v1 quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x6a, 0x0a, 0x7e, 0x01, 0x1b, 0x00, 0xff]

v_trunc_f32_dpp v5, v1 quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x42, 0x0a, 0x7e, 0x01, 0x1b, 0x00, 0xff]

// SOURCE: gfx12_asm_vop1_dpp16.s

v_ceil_f16_dpp v5.l, v1.l quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0xb8, 0x0a, 0x7e, 0x01, 0x1b, 0x00, 0xff]

// SOURCE: gfx12_asm_vop1_dpp8-fake16.s

v_bfrev_b32_dpp v255, v255 dpp8:[0,0,0,0,0,0,0,0]
// ENCODING: [0xe9, 0x70, 0xfe, 0x7f, 0xff, 0x00, 0x00, 0x00]

// SOURCE: gfx12_asm_vop1_dpp8.s

v_ceil_f16_dpp v5.l, v1.l dpp8:[7,6,5,4,3,2,1,0]
// ENCODING: [0xe9, 0xb8, 0x0a, 0x7e, 0x01, 0x77, 0x39, 0x05]

// SOURCE: gfx12_asm_vop1_fake16_err.s

v_cvt_f16_u16_e32 v255, v1 dpp8:[7,6,5,4,3,2,1,0]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop1_fake16_promote.s

v_ceil_f16_e64 v255, 0.5
// ENCODING: [0xff, 0x00, 0xdc, 0xd5, 0xf0, 0x00, 0x01, 0x02]

// SOURCE: gfx12_asm_vop1_t16_err.s

v_trunc_f16_e32 v255.h, v1.h quad_perm:[3,2,1,0]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop1_t16_promote.s

v_ceil_f16_e64 v255.l, v127.l
// ENCODING: [0xff, 0x00, 0xdc, 0xd5, 0x7f, 0x01, 0x01, 0x02]

// SOURCE: gfx12_asm_vop2-fake16.s

v_add_co_ci_u32 v5, vcc_lo, s105, v2, vcc_lo
// ERROR: operands are not valid for this GPU or mode

v_add_co_ci_u32_e32 v5, vcc_lo, s105, v2, vcc_lo
// ENCODING: [0x69, 0x04, 0x0a, 0x40]

// SOURCE: gfx12_asm_vop2.s

v_add_f16_e32 v5.l, v1.l, v2.l
// ENCODING: [0x01, 0x05, 0x0a, 0x64]

// SOURCE: gfx12_asm_vop2_aliases-fake16.s

v_max_num_f64_e32 v[5:6], v[1:2], v[2:3]
// ENCODING: [0x01, 0x05, 0x0a, 0x1c]

// SOURCE: gfx12_asm_vop2_dpp16-fake16.s

v_add_co_ci_u32 v5, vcc_lo, v1, v2, vcc_lo row_shl:15
// ERROR: operands are not valid for this GPU or mode

v_add_co_ci_u32_dpp v5, vcc_lo, v1, v2, vcc_lo row_shl:15 row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x04, 0x0a, 0x40, 0x01, 0x0f, 0x01, 0xff]

v_add_f16_dpp v127, -|v127|, -|v127| row_xmask:15 row_mask:0x3 bank_mask:0x0 fi:1
// ENCODING: [0xfa, 0xfe, 0xfe, 0x64, 0x7f, 0x6f, 0xf5, 0x30]

v_ashrrev_i32_dpp v255, v255, v255 row_xmask:15 row_mask:0x3 bank_mask:0x0 fi:1
// ENCODING: [0xfa, 0xfe, 0xff, 0x35, 0xff, 0x6f, 0x05, 0x30]

v_cndmask_b32_dpp v255, v255, v255, vcc row_xmask:15 row_mask:0x3 bank_mask:0x0 fi:1
// ENCODING: [0xfa, 0xfe, 0xff, 0x03, 0xff, 0x6f, 0x05, 0x30]

v_ldexp_f16_dpp v127, -|v127|, v127 row_xmask:15 row_mask:0x3 bank_mask:0x0 fi:1
// ENCODING: [0xfa, 0xfe, 0xfe, 0x76, 0x7f, 0x6f, 0x35, 0x30]

v_min_u32_dpp v255, v255, v255 row_xmask:15 row_mask:0x3 bank_mask:0x0 fi:1
// ENCODING: [0xfa, 0xfe, 0xff, 0x27, 0xff, 0x6f, 0x05, 0x30]

v_mul_f16_dpp v127, -|v127|, -|v127| row_xmask:15 row_mask:0x3 bank_mask:0x0 fi:1
// ENCODING: [0xfa, 0xfe, 0xfe, 0x6a, 0x7f, 0x6f, 0xf5, 0x30]

v_sub_nc_u32_dpp v255, v255, v255 row_xmask:15 row_mask:0x3 bank_mask:0x0 fi:1
// ENCODING: [0xfa, 0xfe, 0xff, 0x4d, 0xff, 0x6f, 0x05, 0x30]

v_xor_b32_dpp v255, v255, v255 row_xmask:15 row_mask:0x3 bank_mask:0x0 fi:1
// ENCODING: [0xfa, 0xfe, 0xff, 0x3b, 0xff, 0x6f, 0x05, 0x30]

// SOURCE: gfx12_asm_vop2_dpp16.s

v_add_f16_dpp v5.l, v1.l, v2.l quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x04, 0x0a, 0x64, 0x01, 0x1b, 0x00, 0xff]

// SOURCE: gfx12_asm_vop2_dpp8-fake16.s

v_add_co_ci_u32 v255, vcc_lo, v255, v255, vcc_lo dpp8:[0,0,0,0,0,0,0,0] fi:0
// ERROR: operands are not valid for this GPU or mode

v_add_co_ci_u32_dpp v255, vcc_lo, v255, v255, vcc_lo dpp8:[0,0,0,0,0,0,0,0]
// ENCODING: [0xe9, 0xfe, 0xff, 0x41, 0xff, 0x00, 0x00, 0x00]

// SOURCE: gfx12_asm_vop2_dpp8.s

v_add_f16_dpp v5.l, v1.l, v2.l dpp8:[7,6,5,4,3,2,1,0]
// ENCODING: [0xe9, 0x04, 0x0a, 0x64, 0x01, 0x77, 0x39, 0x05]

// SOURCE: gfx12_asm_vop2_err.s

v_pk_fmac_f16 v5, v1, v2 dpp8:[7,6,5,4,3,2,1,0]
// ERROR: not a valid operand.

// SOURCE: gfx12_asm_vop2_fake16_err.s

v_max_num_f16_dpp v5, v1, v255 dpp8:[7,6,5,4,3,2,1,0]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop2_t16_err.s

v_fmac_f16_dpp v5.l, v1.l, v255.l dpp8:[7,6,5,4,3,2,1,0]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop2_t16_promote.s

v_add_f16_e64 v255.l, v1.l, v2.l
// ENCODING: [0xff, 0x00, 0x32, 0xd5, 0x01, 0x05, 0x02, 0x02]

v_add_f16_e64 v5.h, v1.h, v255.h op_sel:[1,1,1]
// ENCODING: [0x05, 0x58, 0x32, 0xd5, 0x01, 0xff, 0x03, 0x02]

// SOURCE: gfx12_asm_vop3-fake16.s

v_add3_u32 v5, v255, s2, s105
// ENCODING: [0x05, 0x00, 0x55, 0xd6, 0xff, 0x05, 0xa4, 0x01]

v_add_co_u32 v5, vcc_hi, 0.5, m0
// ERROR: invalid operand for instruction

v_add_lshl_u32 v5, v1, v2, s3
// ENCODING: [0x05, 0x00, 0x47, 0xd6, 0x01, 0x05, 0x0e, 0x00]

v_cvt_pk_bf8_f32 v1, v2, v3
// ENCODING: [0x01, 0x00, 0x6a, 0xd7, 0x02, 0x07, 0x02, 0x02]

v_cvt_pk_u16_f32 v5, v1, v2
// ENCODING: [0x05, 0x00, 0x07, 0xd7, 0x01, 0x05, 0x02, 0x02]

v_div_fixup_f64 v[5:6], v[1:2], v[2:3], v[3:4]
// ENCODING: [0x05, 0x00, 0x28, 0xd6, 0x01, 0x05, 0x0e, 0x04]

v_lshrrev_b16 v5, v1, v2
// ENCODING: [0x05, 0x00, 0x39, 0xd7, 0x01, 0x05, 0x02, 0x02]

v_mad_co_i64_i32 v[5:6], s6, s105, s105, s[6:7]
// ENCODING: [0x05, 0x06, 0xff, 0xd6, 0x69, 0xd2, 0x18, 0x00]

v_mad_co_u64_u32 v[5:6], s6, s105, s105, s[6:7]
// ENCODING: [0x05, 0x06, 0xfe, 0xd6, 0x69, 0xd2, 0x18, 0x00]

v_max3_i16 v5, v1, v2, s3
// ENCODING: [0x05, 0x00, 0x4d, 0xd6, 0x01, 0x05, 0x0e, 0x00]

v_max_u16 v5, v1, v2
// ENCODING: [0x05, 0x00, 0x09, 0xd7, 0x01, 0x05, 0x02, 0x02]

v_maxmin_num_f32 v5, v1, v2, s3
// ENCODING: [0x05, 0x00, 0x69, 0xd6, 0x01, 0x05, 0x0e, 0x00]

v_min3_i32 v5, v1, v2, s3
// ENCODING: [0x05, 0x00, 0x1a, 0xd6, 0x01, 0x05, 0x0e, 0x00]

v_minmax_u32 v5, v1, v2, s3
// ENCODING: [0x05, 0x00, 0x63, 0xd6, 0x01, 0x05, 0x0e, 0x00]

v_mullit_f32 v5, v1, v2, s3
// ENCODING: [0x05, 0x00, 0x18, 0xd6, 0x01, 0x05, 0x0e, 0x00]

v_permlane16_var_b32 v5, v1, v2
// ENCODING: [0x05, 0x00, 0x0f, 0xd7, 0x01, 0x05, 0x02, 0x02]

v_permlanex16_var_b32 v5, v1, v2
// ENCODING: [0x05, 0x00, 0x10, 0xd7, 0x01, 0x05, 0x02, 0x02]

v_s_exp_f16 s5, s1
// ENCODING: [0x05, 0x00, 0x81, 0xd6, 0x01, 0x00, 0x01, 0x02]

v_s_exp_f32 s5, s1
// ENCODING: [0x05, 0x00, 0x80, 0xd6, 0x01, 0x00, 0x01, 0x02]

v_s_log_f16 s5, s1
// ENCODING: [0x05, 0x00, 0x83, 0xd6, 0x01, 0x00, 0x01, 0x02]

v_s_log_f32 s5, s1
// ENCODING: [0x05, 0x00, 0x82, 0xd6, 0x01, 0x00, 0x01, 0x02]

v_s_rcp_f16 s5, s1
// ENCODING: [0x05, 0x00, 0x85, 0xd6, 0x01, 0x00, 0x01, 0x02]

v_s_rcp_f32 s5, s1
// ENCODING: [0x05, 0x00, 0x84, 0xd6, 0x01, 0x00, 0x01, 0x02]

v_s_rsq_f16 s5, s1
// ENCODING: [0x05, 0x00, 0x87, 0xd6, 0x01, 0x00, 0x01, 0x02]

v_s_rsq_f32 s5, s1
// ENCODING: [0x05, 0x00, 0x86, 0xd6, 0x01, 0x00, 0x01, 0x02]

v_s_sqrt_f16 s5, s1
// ENCODING: [0x05, 0x00, 0x89, 0xd6, 0x01, 0x00, 0x01, 0x02]

v_s_sqrt_f32 s5, s1
// ENCODING: [0x05, 0x00, 0x88, 0xd6, 0x01, 0x00, 0x01, 0x02]

// SOURCE: gfx12_asm_vop3.s

v_add_nc_i16 v5.l, v1.l, v2.l
// ENCODING: [0x05, 0x00, 0x0d, 0xd7, 0x01, 0x05, 0x02, 0x02]

v_cndmask_b16 v5.l, v1.l, src_scc, s3
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop3_aliases-fake16.s

v_mad_co_i64_i32 v[5:6], s12, v1, v2, v[3:4]
// ENCODING: [0x05, 0x0c, 0xff, 0xd6, 0x01, 0x05, 0x0e, 0x04]

// SOURCE: gfx12_asm_vop3_aliases.s

v_max3_num_f16 v5.l, v1.l, v2.l, v3.l
// ENCODING: [0x05, 0x00, 0x2c, 0xd6, 0x01, 0x05, 0x0e, 0x04]

// SOURCE: gfx12_asm_vop3_dpp16-fake16.s

v_add_co_u32_e64_dpp v5, vcc_lo, v1, v2 row_share:0 row_mask:0xf bank_mask:0xf
// ERROR: invalid operand for instruction

v_cvt_sr_bf8_f32_e64_dpp v1, v2, v3 quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0x01, 0x00, 0x6c, 0xd7, 0xfa, 0x06, 0x02, 0x00, 0x02, 0x1b, 0x00, 0xff]

// SOURCE: gfx12_asm_vop3_dpp16.s

v_add3_u32_e64_dpp v5, v1, v2, v3 quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0x05, 0x00, 0x55, 0xd6, 0xfa, 0x04, 0x0e, 0x04, 0x01, 0x1b, 0x00, 0xff]

v_sub_co_u32_e64_dpp v5, s105, v1, v2 quad_perm:[3,2,1,0]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop3_dpp8-fake16.s

v_cvt_sr_bf8_f32_e64_dpp v1, v2, v3 dpp8:[7,6,5,4,3,2,1,0]
// ENCODING: [0x01, 0x00, 0x6c, 0xd7, 0xe9, 0x06, 0x02, 0x00, 0x02, 0x77, 0x39, 0x05]

v_sub_co_u32_e64_dpp v5, s6, v1, v2 dpp8:[7,6,5,4,3,2,1,0]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop3_dpp8.s

v_add3_u32_e64_dpp v5, v1, v2, v3 dpp8:[7,6,5,4,3,2,1,0]
// ENCODING: [0x05, 0x00, 0x55, 0xd6, 0xe9, 0x04, 0x0e, 0x04, 0x01, 0x77, 0x39, 0x05]

v_dot2_f16_f16_e64_dpp v0.l, v1, v2, v3.l op_sel:[1,1,0,0] dpp8:[0,1,2,3,4,4,4,4]
// ERROR: invalid op_sel operand

// SOURCE: gfx12_asm_vop3_err.s

v_permlane16_var_b32 v5, v1, v2 clamp
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop3_from_vop1-fake16.s

v_bfrev_b32_e64 v5, 0.5
// ENCODING: [0x05, 0x00, 0xb8, 0xd5, 0xf0, 0x00, 0x01, 0x02]

// SOURCE: gfx12_asm_vop3_from_vop1.s

v_ceil_f16_e64 v255.h, -|0xfe0b| op_sel:[0,1] clamp div:2
// ENCODING: [0xff, 0xc1, 0xdc, 0xd5, 0xff, 0x00, 0x01, 0x3a, 0x0b, 0xfe, 0x00, 0x00]

// SOURCE: gfx12_asm_vop3_from_vop1_dpp16-fake16.s

v_bfrev_b32_e64_dpp v255, v255 row_xmask:15 row_mask:0x3 bank_mask:0x0 fi:1
// ENCODING: [0xff, 0x00, 0xb8, 0xd5, 0xfa, 0x00, 0x00, 0x00, 0xff, 0x6f, 0x05, 0x30]

// SOURCE: gfx12_asm_vop3_from_vop1_dpp16.s

v_ceil_f16_e64_dpp v5.l, v1.l quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0x05, 0x00, 0xdc, 0xd5, 0xfa, 0x00, 0x00, 0x00, 0x01, 0x1b, 0x00, 0xff]

// SOURCE: gfx12_asm_vop3_from_vop1_dpp8-fake16.s

v_bfrev_b32_e64_dpp v5, v1 dpp8:[7,6,5,4,3,2,1,0] fi:1
// ENCODING: [0x05, 0x00, 0xb8, 0xd5, 0xea, 0x00, 0x00, 0x00, 0x01, 0x77, 0x39, 0x05]

// SOURCE: gfx12_asm_vop3_from_vop1_dpp8.s

v_ceil_f16_e64_dpp v5.l, v1.l dpp8:[7,6,5,4,3,2,1,0]
// ENCODING: [0x05, 0x00, 0xdc, 0xd5, 0xe9, 0x00, 0x00, 0x00, 0x01, 0x77, 0x39, 0x05]

// SOURCE: gfx12_asm_vop3_from_vop2-fake16.s

v_add_co_ci_u32_e64 v5, s[104:105], -1, m0, s[104:105]
// ENCODING: [0x05, 0x68, 0x20, 0xd5, 0xc1, 0xfa, 0xa0, 0x01]

v_add_co_ci_u32_e64 v5, s[104:105], -1, m0, s[104:105]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop3_from_vop2.s

v_add_f16_e64 v5.l, v1.l, v2.l
// ENCODING: [0x05, 0x00, 0x32, 0xd5, 0x01, 0x05, 0x02, 0x02]

// SOURCE: gfx12_asm_vop3_from_vop2_dpp16-fake16.s

v_add_co_ci_u32_e64_dpp v5, s[104:105], v1, v2, s[104:105] row_share:0 row_mask:0xf bank_mask:0xf
// ENCODING: [0x05, 0x68, 0x20, 0xd5, 0xfa, 0x04, 0xa2, 0x01, 0x01, 0x50, 0x01, 0xff]

v_add_co_ci_u32_e64_dpp v5, s[104:105], v1, v2, s[104:105] row_share:0 row_mask:0xf bank_mask:0xf
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop3_from_vop2_dpp16.s

v_add_f16_e64_dpp v5.l, v1.l, v2.l quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0x05, 0x00, 0x32, 0xd5, 0xfa, 0x04, 0x02, 0x00, 0x01, 0x1b, 0x00, 0xff]

// SOURCE: gfx12_asm_vop3_from_vop2_dpp8-fake16.s

v_add_co_ci_u32_e64_dpp v5, s105, v1, v2, s105 dpp8:[7,6,5,4,3,2,1,0]
// ENCODING: [0x05, 0x69, 0x20, 0xd5, 0xe9, 0x04, 0xa6, 0x01, 0x01, 0x77, 0x39, 0x05]

v_cndmask_b32_e64_dpp v5, v1, v2, s3 dpp8:[7,6,5,4,3,2,1,0]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop3_from_vop2_dpp8.s

v_add_f16_e64_dpp v5.l, v1.l, v2.l dpp8:[7,6,5,4,3,2,1,0]
// ENCODING: [0x05, 0x00, 0x32, 0xd5, 0xe9, 0x04, 0x02, 0x00, 0x01, 0x77, 0x39, 0x05]

// SOURCE: gfx12_asm_vop3c-fake16.s

v_cmp_class_f16_e64 s[10:11], vcc_lo, s2
// ENCODING: [0x0a, 0x00, 0x7d, 0xd4, 0x6a, 0x04, 0x00, 0x02]

v_cmp_eq_u16_e64 s5, exec_hi, null
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop3c.s

v_cmp_class_f16_e64 s5, v1.l, v2.l
// ENCODING: [0x05, 0x00, 0x7d, 0xd4, 0x01, 0x05, 0x02, 0x02]

v_cmp_eq_u16_e64 s5, v1.l, v2.l
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop3c_dpp16-fake16.s

v_cmp_class_f16_e64_dpp s[10:11], v1, v2 row_shr:1
// ERROR: invalid operand for instruction

v_cmp_class_f16_e64_dpp s[10:11], v1, v2 row_shr:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0x0a, 0x00, 0x7d, 0xd4, 0xfa, 0x04, 0x02, 0x00, 0x01, 0x11, 0x01, 0xff]

// SOURCE: gfx12_asm_vop3c_dpp16.s

v_cmp_class_f16_e64_dpp s[10:11], v1.l, v2.l row_shl:15
// ERROR: invalid operand for instruction

v_cmp_class_f16_e64_dpp s[10:11], v1.l, v2.l row_shl:15 row_mask:0xf bank_mask:0xf
// ENCODING: [0x0a, 0x00, 0x7d, 0xd4, 0xfa, 0x04, 0x02, 0x00, 0x01, 0x0f, 0x01, 0xff]

// SOURCE: gfx12_asm_vop3c_dpp8-fake16.s

v_cmp_class_f16_e64_dpp s5, v1, s2 dpp8:[7,6,5,4,3,2,1,0]
// ENCODING: [0x05, 0x00, 0x7d, 0xd4, 0xe9, 0x04, 0x00, 0x00, 0x01, 0x77, 0x39, 0x05]

v_cmp_gt_u16_e64_dpp s5, v1, s2 dpp8:[7,6,5,4,3,2,1,0]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop3c_dpp8.s

v_cmp_class_f16_e64_dpp s5, v1.l, v2.l dpp8:[7,6,5,4,3,2,1,0]
// ENCODING: [0x05, 0x00, 0x7d, 0xd4, 0xe9, 0x04, 0x02, 0x00, 0x01, 0x77, 0x39, 0x05]

v_cmp_gt_i16_e64_dpp s5, v1.l, 10 dpp8:[7,6,5,4,3,2,1,0]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop3cx-fake16.s

v_cmpx_class_f16_e64 s1, v2
// ENCODING: [0x7e, 0x00, 0xfd, 0xd4, 0x01, 0x04, 0x02, 0x02]

// SOURCE: gfx12_asm_vop3cx.s

v_cmpx_class_f16_e64 v255.l, v2.l
// ENCODING: [0x7e, 0x00, 0xfd, 0xd4, 0xff, 0x05, 0x02, 0x02]

// SOURCE: gfx12_asm_vop3cx_dpp16-fake16.s

v_cmpx_class_f16_e64_dpp v1, 2.0 quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0x7e, 0x00, 0xfd, 0xd4, 0xfa, 0xe8, 0x01, 0x02, 0x01, 0x1b, 0x00, 0xff]

// SOURCE: gfx12_asm_vop3cx_dpp16.s

v_cmpx_class_f16_e64_dpp v1.l, s2 quad_perm:[3,2,1,0] row_mask:0xf bank_mask:0xf
// ENCODING: [0x7e, 0x00, 0xfd, 0xd4, 0xfa, 0x04, 0x00, 0x02, 0x01, 0x1b, 0x00, 0xff]

// SOURCE: gfx12_asm_vop3cx_dpp8-fake16.s

v_cmpx_class_f16_e64_dpp v1, s2 dpp8:[7,6,5,4,3,2,1,0] fi:1
// ENCODING: [0x7e, 0x00, 0xfd, 0xd4, 0xea, 0x04, 0x00, 0x02, 0x01, 0x77, 0x39, 0x05]

// SOURCE: gfx12_asm_vop3cx_dpp8.s

v_cmpx_class_f16_e64_dpp v1.l, v2.l dpp8:[7,6,5,4,3,2,1,0] fi:1
// ENCODING: [0x7e, 0x00, 0xfd, 0xd4, 0xea, 0x04, 0x02, 0x02, 0x01, 0x77, 0x39, 0x05]

// SOURCE: gfx12_asm_vop3p.s

v_dot2_f32_bf16 v5, exec_hi, exec_lo, null
// ENCODING: [0x05, 0x40, 0x1a, 0xcc, 0x7f, 0xfc, 0xf0, 0x19]

v_dot4_f32_fp8_bf8 v0, 1.0, v2, v3
// ENCODING: [0x00, 0x40, 0x24, 0xcc, 0xf2, 0x04, 0x0e, 0x1c]

v_fma_mixlo_f16 v255, -|src_scc|, -|vcc_hi|, null op_sel:[0,0,1] clamp
// ENCODING: [0xff, 0xa3, 0x21, 0xcc, 0xfd, 0xd6, 0xf0, 0x61]

v_pk_lshrrev_b16 v255, 0xfe0b, vcc_hi op_sel:[0,1] op_sel_hi:[1,0]
// ENCODING: [0xff, 0x50, 0x05, 0xcc, 0xff, 0xd6, 0x00, 0x0a, 0x0b, 0xfe, 0x00, 0x00]

v_pk_mul_f16 v255, 0xfe0b, vcc_hi op_sel:[0,1] op_sel_hi:[1,0] neg_lo:[1,1] neg_hi:[1,1] clamp
// ENCODING: [0xff, 0xd3, 0x10, 0xcc, 0xff, 0xd6, 0x00, 0x6a, 0x0b, 0xfe, 0x00, 0x00]

// SOURCE: gfx12_asm_vop3p_aliases.s

v_dot4_i32_i8 v5, v1, v2, s3
// ENCODING: [0x05, 0x40, 0x16, 0xcc, 0x01, 0x05, 0x0e, 0x78]

// SOURCE: gfx12_asm_vop3p_dpp16.s

v_dot2_f32_f16_e64_dpp v0, v1, v2, v3 quad_perm:[2,2,3,1] row_mask:0xf bank_mask:0xf fi:1
// ENCODING: [0x00, 0x40, 0x13, 0xcc, 0xfa, 0x04, 0x0e, 0x1c, 0x01, 0x7a, 0x04, 0xff]

// SOURCE: gfx12_asm_vop3p_dpp16_err.s

v_dot4_f32_bf8_fp8 v0, 1.0, v2, v3 row_ror:15 row_mask:0x1 bank_mask:0x1 bound_ctrl:1 fi:1
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop3p_dpp8.s

v_dot2_f32_f16_e64_dpp v0, v1, v2, v3 neg_lo:[0,1,1] neg_hi:[1,0,1] dpp8:[7,6,5,4,3,2,1,0]
// ENCODING: [0x00, 0x45, 0x13, 0xcc, 0xe9, 0x04, 0x0e, 0xdc, 0x01, 0x77, 0x39, 0x05]

// SOURCE: gfx12_asm_vop3p_dpp8_err.s

v_dot4_f32_bf8_bf8 v0, v1, v2, 1.0 dpp8:[0,1,2,3,4,5,6,7]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vop3p_err.s

v_dot4_f32_bf8_bf8 v0, v1, v2, v3 neg_hi:[0,1,0]
// ERROR: invalid neg_hi operand

v_dot4_f32_fp8_fp8 v0, v1, v2, v3 neg_lo:[0,1,0]
// ERROR: invalid neg_lo operand

// SOURCE: gfx12_asm_vop3p_features.s

v_dot2_f32_bf16 v0, v1, v2, v3
// ENCODING: [0x00, 0x40, 0x1a, 0xcc, 0x01, 0x05, 0x0e, 0x1c]

// SOURCE: gfx12_asm_vopc-fake16.s

v_cmp_class_f16_e32 vcc_lo, s105, v2
// ENCODING: [0x69, 0x04, 0xfa, 0x7c]

v_cmp_eq_i64 vcc_lo, ttmp[14:15], v[2:3]
// ERROR: operands are not valid for this GPU or mode

v_cmp_ge_u64_e32 vcc, -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0xbc, 0x7c]

v_cmp_gt_u64_e32 vcc, -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0xb8, 0x7c]

v_cmp_ne_i64_e32 vcc, -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0xaa, 0x7c]

// SOURCE: gfx12_asm_vopc.s

v_cmp_class_f16_e32 vcc, -1, v2.l
// ENCODING: [0xc1, 0x04, 0xfa, 0x7c]

v_cmp_nlt_f32 vcc, 0xaf123456, v2
// ERROR: operands are not valid for this GPU or mode

// SOURCE: gfx12_asm_vopc_dpp16-fake16.s

v_cmp_class_f16 vcc_lo, v1, v2 row_shl:15 row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x04, 0xfa, 0x7c, 0x01, 0x0f, 0x01, 0xff]

v_cmp_eq_u16 vcc, v1, v2 quad_perm:[0,1,2,3] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x04, 0x74, 0x7c, 0x01, 0xe4, 0x00, 0xff]

v_cmp_ge_f16 vcc_lo, v1, v2 row_shl:15
// ERROR: operands are not valid for this GPU or mode

v_cmp_lt_f16 vcc, -|v127|, -|v127| row_xmask:15 row_mask:0x3 bank_mask:0x0 fi:1
// ENCODING: [0xfa, 0xfe, 0x02, 0x7c, 0x7f, 0x6f, 0xf5, 0x30]

v_cmp_ngt_f32 vcc, -|v255|, -|v255| row_xmask:15 row_mask:0x3 bank_mask:0x0 fi:1
// ENCODING: [0xfa, 0xfe, 0x37, 0x7c, 0xff, 0x6f, 0xf5, 0x30]

v_cmp_o_f16 vcc, -|v127|, -|v127| row_xmask:15 row_mask:0x3 bank_mask:0x0 fi:1
// ENCODING: [0xfa, 0xfe, 0x0e, 0x7c, 0x7f, 0x6f, 0xf5, 0x30]

// SOURCE: gfx12_asm_vopc_dpp16.s

v_cmp_class_f16 vcc, v1.l, v2.l row_ror:1
// ERROR: operands are not valid for this GPU or mode

v_cmp_class_f16 vcc, v1.l, v2.l row_ror:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x04, 0xfa, 0x7c, 0x01, 0x21, 0x01, 0xff]

// SOURCE: gfx12_asm_vopc_dpp8-fake16.s

v_cmp_class_f16 vcc, v1, v2 dpp8:[7,6,5,4,3,2,1,0] fi:1
// ENCODING: [0xea, 0x04, 0xfa, 0x7c, 0x01, 0x77, 0x39, 0x05]

v_cmp_nge_f32 vcc, v255, v255 dpp8:[0,0,0,0,0,0,0,0] fi:0
// ERROR: operands are not valid for this GPU or mode

// SOURCE: gfx12_asm_vopc_dpp8.s

v_cmp_class_f16 vcc_lo, v1.l, v2.l dpp8:[7,6,5,4,3,2,1,0]
// ENCODING: [0xe9, 0x04, 0xfa, 0x7c, 0x01, 0x77, 0x39, 0x05]

v_cmp_lt_i16 vcc, v1.h, v2.h dpp8:[7,6,5,4,3,2,1,0] fi:1
// ERROR: operands are not valid for this GPU or mode

// SOURCE: gfx12_asm_vopc_fake16_err.s

v_cmp_lt_i16_e32 vcc, v127, v255 quad_perm:[3,2,1,0]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vopc_fake16_promote.s

v_cmp_class_f16_e64 vcc, v127, v255
// ENCODING: [0x6a, 0x00, 0x7d, 0xd4, 0x7f, 0xff, 0x03, 0x02]

v_cmp_eq_f16 vcc, v127, v255 dpp8:[7,6,5,4,3,2,1,0]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vopc_t16_err.s

v_cmp_gt_f16_e32 vcc_lo, v127.h, v255.h quad_perm:[3,2,1,0]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vopc_t16_promote.s

v_cmp_class_f16_e64 vcc_lo, v127.h, v255.h op_sel:[1,1,0]
// ENCODING: [0x6a, 0x18, 0x7d, 0xd4, 0x7f, 0xff, 0x03, 0x02]

v_cmp_ge_i16 vcc_lo, v127.l, v255.l quad_perm:[3,2,1,0]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vopcx-fake16.s

v_cmpx_class_f16_e32 0xfe0b, v127
// ENCODING: [0xff, 0xfe, 0xfa, 0x7d, 0x0b, 0xfe, 0x00, 0x00]

v_cmpx_eq_f64_e32 -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0x44, 0x7d]

v_cmpx_le_u64_e32 -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0xb6, 0x7d]

v_cmpx_nlg_f64_e32 -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0x54, 0x7d]

// SOURCE: gfx12_asm_vopcx.s

v_cmpx_class_f16_e32 v127.l, v2.l
// ENCODING: [0x7f, 0x05, 0xfa, 0x7d]

// SOURCE: gfx12_asm_vopcx_dpp16-fake16.s

v_cmpx_class_f16 v1, v2 row_half_mirror row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x04, 0xfa, 0x7d, 0x01, 0x41, 0x01, 0xff]

v_cmpx_ge_i16 v1, v2 quad_perm:[0,1,2,3] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x04, 0x6c, 0x7d, 0x01, 0xe4, 0x00, 0xff]

v_cmpx_ge_i32 v1, v2 quad_perm:[0,1,2,3] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x04, 0x8c, 0x7d, 0x01, 0xe4, 0x00, 0xff]

v_cmpx_gt_u16 v1, v2 quad_perm:[0,1,2,3] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x04, 0x78, 0x7d, 0x01, 0xe4, 0x00, 0xff]

v_cmpx_lt_u32 v1, v2 quad_perm:[0,1,2,3] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x04, 0x92, 0x7d, 0x01, 0xe4, 0x00, 0xff]

v_cmpx_nge_f16 -|v127|, -|v127| row_xmask:15 row_mask:0x3 bank_mask:0x0 fi:1
// ENCODING: [0xfa, 0xfe, 0x12, 0x7d, 0x7f, 0x6f, 0xf5, 0x30]

// SOURCE: gfx12_asm_vopcx_dpp16.s

v_cmpx_class_f16 v1.l, v2.l quad_perm:[0,1,2,3] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x04, 0xfa, 0x7d, 0x01, 0xe4, 0x00, 0xff]

// SOURCE: gfx12_asm_vopcx_dpp8-fake16.s

v_cmpx_class_f16 v1, v2 dpp8:[7,6,5,4,3,2,1,0]
// ENCODING: [0xe9, 0x04, 0xfa, 0x7d, 0x01, 0x77, 0x39, 0x05]

// SOURCE: gfx12_asm_vopcx_dpp8.s

v_cmpx_class_f16 v1.l, v2.l dpp8:[7,6,5,4,3,2,1,0] fi:1
// ENCODING: [0xea, 0x04, 0xfa, 0x7d, 0x01, 0x77, 0x39, 0x05]

// SOURCE: gfx12_asm_vopcx_fake16_err.s

v_cmpx_ge_i16_e32 v1, v255 quad_perm:[3,2,1,0]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vopcx_fake16_promote.s

v_cmpx_class_f16_e64 v1, v255
// ENCODING: [0x7e, 0x00, 0xfd, 0xd4, 0x01, 0xff, 0x03, 0x02]

// SOURCE: gfx12_asm_vopcx_t16_err.s

v_cmpx_eq_i16_e32 v255.h, v2.h quad_perm:[3,2,1,0]
// ERROR: invalid operand for instruction

// SOURCE: gfx12_asm_vopcx_t16_promote.s

v_cmpx_class_f16_e64 v1.l, v255.l
// ENCODING: [0x7e, 0x00, 0xfd, 0xd4, 0x01, 0xff, 0x03, 0x02]

// SOURCE: gfx12_asm_vopd.s

v_dual_add_f32 v255, 0.5, v3 :: v_dual_fmaak_f32 v6, 0.5, v2, 0xaf123456
// ERROR: instruction requires wavesize=32

v_dual_add_f32 v255, v4, v2 :: v_dual_mul_dx9_zero_f32 v6, v1, v3
// ENCODING: [0x04, 0x05, 0x0e, 0xc9, 0x01, 0x07, 0x06, 0xff]

v_dual_dot2acc_f32_f16 v255, -1, v4 :: v_dual_add_f32 v6, src_scc, v5
// ENCODING: [0xc1, 0x08, 0x08, 0xcb, 0xfd, 0x0a, 0x06, 0xff]

// SOURCE: gfx12_asm_vopd_errs.s

v_dual_add_f32      v255, s1, v2                 ::  v_dual_cndmask_b32   v6, s2, v3
// ERROR: invalid operand (violates constant bus restrictions)

// SOURCE: gfx12_asm_vopd_features.s

v_dual_add_f32 v2, v2, v5 :: v_dual_mul_f32 v3, v2, v5
// ENCODING: [0x02, 0x0b, 0x06, 0xc9, 0x02, 0x0b, 0x02, 0x02]

// SOURCE: gfx12_asm_wmma_w32.s

v_swmmac_bf16_16x16x32_bf16 v[12:15], 1.0, v[4:11], v16
// ERROR: invalid operand for instruction

v_swmmac_bf16_16x16x32_bf16 v[12:15], v[0:3], v[4:11], v16
// ENCODING: [0x0c, 0x40, 0x53, 0xcc, 0x00, 0x09, 0x42, 0x1c]

v_swmmac_f32_16x16x32_bf16 v[12:19], v[0:3], v[4:11], v20
// ENCODING: [0x0c, 0x40, 0x51, 0xcc, 0x00, 0x09, 0x52, 0x1c]

v_wmma_i32_16x16x16_iu8 v[4:11], v[0:1], v[2:3], v[4:11]
// ENCODING: [0x04, 0x40, 0x44, 0xcc, 0x00, 0x05, 0x12, 0x1c]

// SOURCE: gfx12_asm_wmma_w64.s

v_swmmac_bf16_16x16x32_bf16 v[6:7], 1.0, v[2:5], v8
// ERROR: invalid operand for instruction

v_swmmac_bf16_16x16x32_bf16 v[6:7], v[0:1], v[2:5], v8
// ENCODING: [0x06, 0x40, 0x53, 0xcc, 0x00, 0x05, 0x22, 0x1c]

// SOURCE: gfx12_err.s

image_load v0, v0, s[0:7] dmask:0x1 dim:SQ_RSRC_IMG_1D th:TH_STORE_NT
// ERROR: invalid th value for load instructions

image_store v0, v0, s[0:7] dmask:0x1 dim:SQ_RSRC_IMG_1D th:TH_STORE_WB scope:SCOPE_SYS
// ERROR: scope and th combination is not valid

s_buffer_load_b64 s[10:11], s[4:7], s0 offset:0x0 th:TH_LOAD_RT_NT
// ERROR: invalid th value for SMEM instruction

s_wait_event { export_ready 1 }
// ERROR: colon expected

// SOURCE: gfx12_flat_instructions_err.s

global_atomic_max_u64 v0, v[2:3], null
// ERROR: invalid operand for instruction

// SOURCE: gfx12_unsupported.s

buffer_wbinvl1
// ERROR: instruction not supported on this GPU (gfx1200): buffer_wbinvl1

buffer_wbinvl1_vol
// ERROR: instruction not supported on this GPU (gfx1200): buffer_wbinvl1_vol

ds_add_f32 v255, v255 offset:4 gds
// ERROR: gds modifier is not supported on this GPU

ds_cmpstore_rtn_f32 v0, v1, v2, v3
// ERROR: instruction not supported on this GPU (gfx1200): ds_cmpstore_rtn_f32

flat_atomic_csub v1, v[0:1], v2 offset:64 th:TH_ATOMIC_RETURN
// ERROR: invalid instruction

s_cbranch_cdbgsys_and_user 0
// ERROR: instruction not supported on this GPU (gfx1200): s_cbranch_cdbgsys_and_user

s_cmpk_ge_i32 s0, 0
// ERROR: instruction not supported on this GPU (gfx1200): s_cmpk_ge_i32

s_cmpk_le_i32 s0, 0
// ERROR: instruction not supported on this GPU (gfx1200): s_cmpk_le_i32

s_cmpk_lt_i32 s0, 0
// ERROR: instruction not supported on this GPU (gfx1200): s_cmpk_lt_i32

s_waitcnt_lgkmcnt exec_hi, 0x1234
// ERROR: instruction not supported on this GPU (gfx1200): s_waitcnt_lgkmcnt

v_cmp_f_f32 v0, v1
// ERROR: instruction not supported on this GPU (gfx1200): v_cmp_f_f32

v_cmp_t_f32 v0, v1
// ERROR: instruction not supported on this GPU (gfx1200): v_cmp_t_f32

v_cmpx_f_f16 v0, v1
// ERROR: instruction not supported on this GPU (gfx1200): v_cmpx_f_f16

// SOURCE: gfx950_asm_vop3.s

v_bitop3_b32 v5, v1, v2, s3
// ERROR: instruction not supported on this GPU (gfx1200): v_bitop3_b32

// SOURCE: literals.s

buffer_atomic_add_u32 v0, off, s[0:3], src_scc offset:4095
// ENCODING: [0x7d, 0x40, 0x0d, 0xc4, 0x00, 0x00, 0x80, 0x00, 0x00, 0xff, 0x0f, 0x00]

s_add_i32 s0, execz, s0
// ERROR: src_execz register not available on this GPU

v_add_u16 v0, vccz, v0
// ERROR: instruction not supported on this GPU (gfx1200): v_add_u16

v_fract_f64_e32 v[0:1], lit64(1.0)
// ERROR: lit64 is not supported on this GPU

// SOURCE: sopp-err.s

s_sendmsg sendmsg(2, 2, -1)
// ERROR: invalid message stream id

s_waitcnt expcnt(8)
// ERROR: too large value for expcnt

s_waitcnt_depctr depctr_vm_vsrc(-1)
// ERROR: invalid value for depctr_vm_vsrc
