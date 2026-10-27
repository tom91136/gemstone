// Generated from pinned LLVM MC/AMDGPU checks. Regenerate with: sbt codegen
// TARGET: tahiti

// SOURCE: ds-err.s

ds_swizzle_b32 v8, v2 offset:swizzle(BITMASK_PERM, "pppi2")
// ERROR: invalid mask

ds_swizzle_b32 v8, v2 offset:swizzle(QUAD_PERM, -1, 1, 2, 3)
// ERROR: expected a 2-bit lane id

ds_swizzle_b32 v8, v2 offset:swizzle(quad_perm, 0, 1, 2, 3)
// ERROR: expected a swizzle mode

ds_write2_b32 v2, v4, v6 offset1:0x100
// ERROR: invalid offset1 value.

// SOURCE: ds.s

ds_add_u32 v2, v4 offset:16
// ENCODING: [0x10, 0x00, 0x00, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_and_b64 v2, v[4:5]
// ENCODING: [0x00, 0x00, 0x24, 0xd9, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_append v8
// ENCODING: [0x00, 0x00, 0xf8, 0xd8, 0x00, 0x00, 0x00, 0x08] WAVE_SIZE: 64

ds_cmpst_f32 v2, v4, v6
// ENCODING: [0x00, 0x00, 0x44, 0xd8, 0x02, 0x04, 0x06, 0x00] WAVE_SIZE: 64

ds_cmpst_rtn_b64 v[8:9], v2, v[4:5], v[6:7]
// ENCODING: [0x00, 0x00, 0xc0, 0xd9, 0x02, 0x04, 0x06, 0x08] WAVE_SIZE: 64

ds_consume v8
// ENCODING: [0x00, 0x00, 0xf4, 0xd8, 0x00, 0x00, 0x00, 0x08] WAVE_SIZE: 64

ds_dec_u32 v2, v4
// ENCODING: [0x00, 0x00, 0x10, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_gws_barrier v2 gds
// ENCODING: [0x00, 0x00, 0x76, 0xd8, 0x02, 0x00, 0x00, 0x00] WAVE_SIZE: 64

ds_gws_init v2 gds
// ENCODING: [0x00, 0x00, 0x66, 0xd8, 0x02, 0x00, 0x00, 0x00] WAVE_SIZE: 64

ds_gws_sema_v gds
// ENCODING: [0x00, 0x00, 0x6a, 0xd8, 0x00, 0x00, 0x00, 0x00] WAVE_SIZE: 64

ds_inc_u32 v2, v4
// ENCODING: [0x00, 0x00, 0x0c, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_max_f32 v2, v4
// ENCODING: [0x00, 0x00, 0x4c, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_max_i64 v2, v[4:5]
// ENCODING: [0x00, 0x00, 0x18, 0xd9, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_max_rtn_i32 v8, v2, v4
// ENCODING: [0x00, 0x00, 0x98, 0xd8, 0x02, 0x04, 0x00, 0x08] WAVE_SIZE: 64

ds_max_rtn_u64 v[8:9], v2, v[4:5]
// ENCODING: [0x00, 0x00, 0xa0, 0xd9, 0x02, 0x04, 0x00, 0x08] WAVE_SIZE: 64

ds_max_u32 v2, v4
// ENCODING: [0x00, 0x00, 0x20, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_min_f32 v2, v4
// ENCODING: [0x00, 0x00, 0x48, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_min_i64 v2, v[4:5]
// ENCODING: [0x00, 0x00, 0x14, 0xd9, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_min_rtn_i32 v8, v2, v4
// ENCODING: [0x00, 0x00, 0x94, 0xd8, 0x02, 0x04, 0x00, 0x08] WAVE_SIZE: 64

ds_min_rtn_u64 v[8:9], v2, v[4:5]
// ENCODING: [0x00, 0x00, 0x9c, 0xd9, 0x02, 0x04, 0x00, 0x08] WAVE_SIZE: 64

ds_mskor_b32 v2, v4, v6
// ENCODING: [0x00, 0x00, 0x30, 0xd8, 0x02, 0x04, 0x06, 0x00] WAVE_SIZE: 64

ds_mskor_rtn_b64 v[8:9], v2, v[4:5], v[6:7]
// ENCODING: [0x00, 0x00, 0xb0, 0xd9, 0x02, 0x04, 0x06, 0x08] WAVE_SIZE: 64

ds_or_b64 v2, v[4:5]
// ENCODING: [0x00, 0x00, 0x28, 0xd9, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_ordered_count v8, v2 gds
// ENCODING: [0x00, 0x00, 0xfe, 0xd8, 0x02, 0x00, 0x00, 0x08] WAVE_SIZE: 64

ds_read2st64_b32 v[8:9], v2
// ENCODING: [0x00, 0x00, 0xe0, 0xd8, 0x02, 0x00, 0x00, 0x08] WAVE_SIZE: 64

ds_read_b64 v[8:9], v2
// ENCODING: [0x00, 0x00, 0xd8, 0xd9, 0x02, 0x00, 0x00, 0x08] WAVE_SIZE: 64

ds_read_u16 v8, v2
// ENCODING: [0x00, 0x00, 0xf0, 0xd8, 0x02, 0x00, 0x00, 0x08] WAVE_SIZE: 64

ds_rsub_rtn_u64 v[8:9], v2, v[4:5]
// ENCODING: [0x00, 0x00, 0x88, 0xd9, 0x02, 0x04, 0x00, 0x08] WAVE_SIZE: 64

ds_sub_rtn_u32 v8, v2, v4
// ENCODING: [0x00, 0x00, 0x84, 0xd8, 0x02, 0x04, 0x00, 0x08] WAVE_SIZE: 64

ds_sub_u64 v2, v[4:5]
// ENCODING: [0x00, 0x00, 0x04, 0xd9, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_swizzle_b32 v8, v2 offset:0x001
// ENCODING: [0x01, 0x00, 0xd4, 0xd8, 0x02, 0x00, 0x00, 0x08] WAVE_SIZE: 64

ds_write2_b64 v2, v[4:5], v[6:7]
// ENCODING: [0x00, 0x00, 0x38, 0xd9, 0x02, 0x04, 0x06, 0x00] WAVE_SIZE: 64

ds_write_b16 v2, v4
// ENCODING: [0x00, 0x00, 0x7c, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_write_b8 v2, v4
// ENCODING: [0x00, 0x00, 0x78, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_write_src2_b32 v2 offset:2052
// ENCODING: [0x04, 0x08, 0x34, 0xda, 0x02, 0x00, 0x00, 0x00] WAVE_SIZE: 64

ds_write_src2_b64 v2 offset:2052
// ENCODING: [0x04, 0x08, 0x34, 0xdb, 0x02, 0x00, 0x00, 0x00] WAVE_SIZE: 64

ds_wrxchg2st64_rtn_b32 v[8:9], v2, v4, v6
// ENCODING: [0x00, 0x00, 0xbc, 0xd8, 0x02, 0x04, 0x06, 0x08] WAVE_SIZE: 64

ds_wrxchg_rtn_b64 v[8:9], v2, v[4:5]
// ENCODING: [0x00, 0x00, 0xb4, 0xd9, 0x02, 0x04, 0x00, 0x08] WAVE_SIZE: 64

ds_xor_rtn_b32 v8, v2, v4
// ENCODING: [0x00, 0x00, 0xac, 0xd8, 0x02, 0x04, 0x00, 0x08] WAVE_SIZE: 64

// SOURCE: fixup64.s

s_mov_b64 vcc, .LL2-.LL1
// ERROR: invalid operand for instruction

// SOURCE: flat.s

flat_store_byte v[3:4], v1
// ERROR: instruction not supported on this GPU

// SOURCE: inline-imm-inv2pi.s

v_cvt_f32_f16_e32 v0, 0x3118
// ENCODING: [0xff, 0x16, 0x00, 0x7e, 0x18, 0x31, 0x00, 0x00] WAVE_SIZE: 64

// SOURCE: literals.s

buffer_atomic_add v0, off, s[0:3], src_scc offset:4095
// ENCODING: [0xff, 0x0f, 0xc8, 0xe0, 0x00, 0x00, 0x00, 0xfd] WAVE_SIZE: 64

s_add_i32 s0, src_execz, s0
// ENCODING: [0xfc, 0x00, 0x00, 0x81] WAVE_SIZE: 64

s_add_i32 s0, src_pops_exiting_wave_id, s0
// ERROR: src_pops_exiting_wave_id register not available on this GPU

s_add_i32 s0, src_vccz, s0
// ENCODING: [0xfb, 0x00, 0x00, 0x81] WAVE_SIZE: 64

s_and_b64 s[0:1], s[0:1], src_vccz
// ENCODING: [0x00, 0xfb, 0x80, 0x87] WAVE_SIZE: 64

s_mov_b64 s[0:1], 0.5
// ENCODING: [0xf0, 0x04, 0x80, 0xbe] WAVE_SIZE: 64

v_add_u16 v0, vccz, v0
// ERROR: instruction not supported on this GPU (tahiti): v_add_u16

v_and_b32_e32 v0, 0x3e22f983, v1
// ENCODING: [0xff, 0x02, 0x00, 0x36, 0x83, 0xf9, 0x22, 0x3e] WAVE_SIZE: 64

v_ceil_f64 v[5:6], |execz|
// ERROR: instruction not supported on this GPU (tahiti): v_ceil_f64

v_cos_f16_e32 v5.l, 1.0
// ERROR: instruction not supported on this GPU (tahiti): v_cos_f16

v_fract_f64_e32 v[0:1], 0x3e22f983
// ENCODING: [0xff, 0x7c, 0x00, 0x7e, 0x83, 0xf9, 0x22, 0x3e] WAVE_SIZE: 64

v_madak_f32 v4, lit(0x7e8), v8, lit(0x7e8)
// ENCODING: [0xff, 0x10, 0x08, 0x42, 0xe8, 0x07, 0x00, 0x00] WAVE_SIZE: 64

v_pk_add_f16 v0, execz, v0
// ERROR: instruction not supported on this GPU (tahiti): v_pk_add_f16

v_pk_add_u16 v5, exec_lo, 1.0
// ERROR: instruction not supported on this GPU (tahiti): v_pk_add_u16

v_sqrt_f32 v2, lit(123.0
// ERROR: expected closing parentheses

v_sqrt_f64_e32 v[2:3], lit(0x405ec000)
// ENCODING: [0xff, 0x68, 0x04, 0x7e, 0x00, 0xc0, 0x5e, 0x40] WAVE_SIZE: 64

v_trunc_f64 v[0:1], 0x1fffffffff0
// ERROR: instruction not supported on this GPU (tahiti): v_trunc_f64

v_wmma_i32_16x16x16_iu8 v[8:15], v[0:3], v[4:7], 1.0
// ERROR: instruction not supported on this GPU (tahiti): v_wmma_i32_16x16x16_iu8

// SOURCE: mimg.s

image_load v[4:6], v[237:240], s[28:35] dmask:0x7 unorm
// ENCODING: [0x00, 0x17, 0x00, 0xf0, 0xed, 0x04, 0x07, 0x00] WAVE_SIZE: 64

image_load_pck v5, v[1:4], s[8:15] dmask:0x1 glc
// ENCODING: [0x00, 0x21, 0x08, 0xf0, 0x01, 0x05, 0x02, 0x00] WAVE_SIZE: 64

image_sample  v193, v[237:240], s[28:35], s[4:7] d16
// ERROR: d16 modifier is not supported on this GPU

image_store v[193:195], v[237:240], s[28:35] dmask:0x7 unorm
// ENCODING: [0x00, 0x17, 0x20, 0xf0, 0xed, 0xc1, 0x07, 0x00] WAVE_SIZE: 64

// SOURCE: mtbuf.s

tbuffer_load_format_xy v[1:2], off, s[4:7], s1 format:[BUF_DATA_FORMAT_RESERVED_15,BUF_NUM_FORMAT_USCALED]
// ENCODING: [0x00, 0x00, 0x79, 0xe9, 0x00, 0x01, 0x01, 0x01] WAVE_SIZE: 64

tbuffer_store_format_x v1, off, s[4:7], s1 format:[BUF_DATA_FORMAT_RESERVED_15,BUF_NUM_FORMAT_USCALED]
// ENCODING: [0x00, 0x00, 0x7c, 0xe9, 0x00, 0x01, 0x01, 0x01] WAVE_SIZE: 64

tbuffer_store_format_xyzw v[1:4], off, ttmp[4:7], s0 format: offset:52
// ERROR: expected absolute expression

tbuffer_store_format_xyzw v[1:4], off, ttmp[4:7], s0 format:[BUF_DATA_FORMAT]
// ERROR: unsupported format

tbuffer_store_format_xyzw v[1:4], off, ttmp[4:7], s0 format:[BUF_DATA_FORMAT_INVALID]
// ENCODING: [0x00, 0x00, 0x07, 0xe8, 0x00, 0x01, 0x1d, 0x00] WAVE_SIZE: 64

// SOURCE: mubuf.s

buffer_atomic_add v5, off, s[8:11], 0.5 offset:4095 glc
// ENCODING: [0xff, 0x4f, 0xc8, 0xe0, 0x00, 0x05, 0x02, 0xf0] WAVE_SIZE: 64

buffer_atomic_fcmpswap v[0:1], off, s[0:3], s0 offset:4095
// ENCODING: [0xff, 0x0f, 0xf8, 0xe0, 0x00, 0x00, 0x00, 0x00] WAVE_SIZE: 64

buffer_atomic_fcmpswap_x2 v[0:3], off, s[0:3], s0 offset:4095
// ENCODING: [0xff, 0x0f, 0x78, 0xe1, 0x00, 0x00, 0x00, 0x00] WAVE_SIZE: 64

buffer_atomic_fmin v0, v[0:1], s[0:3], s0 addr64 offset:4095
// ENCODING: [0xff, 0x8f, 0xfc, 0xe0, 0x00, 0x00, 0x00, 0x00] WAVE_SIZE: 64

buffer_atomic_inc v1, v[2:3], s[8:11], 56 addr64
// ENCODING: [0x00, 0x80, 0xf0, 0xe0, 0x02, 0x01, 0x02, 0xb8] WAVE_SIZE: 64

buffer_load_dword v1, v2, s[4:7], s1 idxen offset:4
// ENCODING: [0x04, 0x20, 0x30, 0xe0, 0x02, 0x01, 0x01, 0x01] WAVE_SIZE: 64

buffer_load_dwordx4 v[1:5], v2, ttmp[4:7], s1 idxen offset:4 glc slc tfe
// ENCODING: [0x04, 0x60, 0x38, 0xe0, 0x02, 0x01, 0xdd, 0x01] WAVE_SIZE: 64

buffer_load_format_xyz v[1:3], off, s[4:7], s1
// ENCODING: [0x00, 0x00, 0x08, 0xe0, 0x00, 0x01, 0x01, 0x01] WAVE_SIZE: 64

buffer_load_sshort v1, off, s[4:7], s1
// ENCODING: [0x00, 0x00, 0x2c, 0xe0, 0x00, 0x01, 0x01, 0x01] WAVE_SIZE: 64

buffer_store_byte v1, off, s[4:7], s1
// ENCODING: [0x00, 0x00, 0x60, 0xe0, 0x00, 0x01, 0x01, 0x01] WAVE_SIZE: 64

buffer_store_dwordx3 v[0:2], off, s[4:7], s0 offset:4095
// ENCODING: [0xff, 0x0f, 0x7c, 0xe0, 0x00, 0x00, 0x01, 0x00] WAVE_SIZE: 64

buffer_store_dwordx4 v[1:4], off, s[4:7], s1
// ENCODING: [0x00, 0x00, 0x78, 0xe0, 0x00, 0x01, 0x01, 0x01] WAVE_SIZE: 64

buffer_store_format_xy v[1:2], off, s[4:7], s1
// ENCODING: [0x00, 0x00, 0x14, 0xe0, 0x00, 0x01, 0x01, 0x01] WAVE_SIZE: 64

buffer_store_short v1, off, s[4:7], s1
// ENCODING: [0x00, 0x00, 0x68, 0xe0, 0x00, 0x01, 0x01, 0x01] WAVE_SIZE: 64

buffer_wbinvl1_sc
// ENCODING: [0x00, 0x00, 0xc0, 0xe1, 0x00, 0x00, 0x00, 0x00] WAVE_SIZE: 64

// SOURCE: out-of-range-registers.s

s_mov_b64 s[104:105], -1
// ERROR: s[104:105] register not available on this GPU

// SOURCE: reg-syntax-extra.s

flat_load_dword v[8:8], v[2:3]
// ERROR: instruction not supported on this GPU (tahiti): flat_load_dword

v_mov_b32_e32 v1, v2
// ENCODING: [0x02, 0x03, 0x02, 0x7e] WAVE_SIZE: 64

v_rcp_f64_e32 v[1:2], v[2:3]
// ENCODING: [0x02, 0x5f, 0x02, 0x7e] WAVE_SIZE: 64

// SOURCE: regression/bug28165.s

v_cmp_eq_f64_e32 vcc, 0.5, v[254:255]
// ENCODING: [0xf0, 0xfc, 0x45, 0x7c] WAVE_SIZE: 64

v_cvt_f32_f64_e32 v0, 0.5
// ENCODING: [0xf0, 0x1e, 0x00, 0x7e] WAVE_SIZE: 64

// SOURCE: smem.s

s_buffer_load_dwordx2 s[10:11], s[92:95], m0
// ENCODING: [0x7c, 0x5c, 0x45, 0xc2] WAVE_SIZE: 64

s_load_dword s1, s[2:3], -1
// ERROR: operands are not valid for this GPU or mode

// SOURCE: smrd.s

s_dcache_inv
// ENCODING: [0x00, 0x00, 0xc0, 0xc7] WAVE_SIZE: 64

s_load_dwordx16 s[16:31], s[2:3], 0x1
// ENCODING: [0x01, 0x03, 0x08, 0xc1] WAVE_SIZE: 64

s_load_dwordx8 s[8:15], s[2:3], 0x1
// ENCODING: [0x01, 0x03, 0xc4, 0xc0] WAVE_SIZE: 64

// SOURCE: sop1-err.s

s_mov_b32 s0, s220
// ERROR: register index is out of range

// SOURCE: sop2.s

s_absdiff_i32 s2, s4, s6
// ENCODING: [0x04, 0x06, 0x02, 0x96] WAVE_SIZE: 64

s_add_u32 s1, s2, s3
// ENCODING: [0x02, 0x03, 0x01, 0x80] WAVE_SIZE: 64

s_add_u32 s101, s102, s103
// ENCODING: [0x66, 0x67, 0x65, 0x80] WAVE_SIZE: 64

s_addc_u32 s1, s2, s3
// ENCODING: [0x02, 0x03, 0x01, 0x82] WAVE_SIZE: 64

s_andn2_b64 s[2:3], s[4:5], s[6:7]
// ENCODING: [0x04, 0x06, 0x82, 0x8a] WAVE_SIZE: 64

s_bfe_i32 s2, s4, s6
// ENCODING: [0x04, 0x06, 0x02, 0x94] WAVE_SIZE: 64

s_bfe_u64 s[2:3], s[4:5], s6
// ENCODING: [0x04, 0x06, 0x82, 0x94] WAVE_SIZE: 64

s_cbranch_g_fork s[4:5], s[6:7]
// ENCODING: [0x04, 0x06, 0x80, 0x95] WAVE_SIZE: 64

s_lshr_b32 s2, s4, s6
// ENCODING: [0x04, 0x06, 0x02, 0x90] WAVE_SIZE: 64

s_max_u32 s1, s2, s3
// ENCODING: [0x02, 0x03, 0x81, 0x84] WAVE_SIZE: 64

s_min_u32 s1, s2, s3
// ENCODING: [0x02, 0x03, 0x81, 0x83] WAVE_SIZE: 64

s_nand_b32 s2, s4, s6
// ENCODING: [0x04, 0x06, 0x02, 0x8c] WAVE_SIZE: 64

s_nor_b32 s2, s4, s6
// ENCODING: [0x04, 0x06, 0x02, 0x8d] WAVE_SIZE: 64

s_or_b64 s[2:3], s[4:5], s[6:7]
// ENCODING: [0x04, 0x06, 0x82, 0x88] WAVE_SIZE: 64

s_subb_u32 s1, s2, s3
// ENCODING: [0x02, 0x03, 0x81, 0x82] WAVE_SIZE: 64

s_xor_b32 s2, s4, s6
// ENCODING: [0x04, 0x06, 0x02, 0x89] WAVE_SIZE: 64

// SOURCE: sopc.s

s_bitcmp0_b32 s1, s2
// ENCODING: [0x01, 0x02, 0x0c, 0xbf] WAVE_SIZE: 64

s_bitcmp1_b64 s[2:3], s4
// ENCODING: [0x02, 0x04, 0x0f, 0xbf] WAVE_SIZE: 64

s_cmp_eq_i32 s1, s2
// ENCODING: [0x01, 0x02, 0x00, 0xbf] WAVE_SIZE: 64

s_cmp_ge_u32 s1, s2
// ENCODING: [0x01, 0x02, 0x09, 0xbf] WAVE_SIZE: 64

s_cmp_le_i32 s1, s2
// ENCODING: [0x01, 0x02, 0x05, 0xbf] WAVE_SIZE: 64

s_cmp_lg_u32 s1, s2
// ENCODING: [0x01, 0x02, 0x07, 0xbf] WAVE_SIZE: 64

s_cmp_lt_u32 s1, s2
// ENCODING: [0x01, 0x02, 0x0a, 0xbf] WAVE_SIZE: 64

s_setvskip s3, s5
// ENCODING: [0x03, 0x05, 0x10, 0xbf] WAVE_SIZE: 64

// SOURCE: sopk-err.s

s_cbranch_i_fork s[2:3], 6
// ENCODING: [0x06, 0x00, 0x82, 0xb8] WAVE_SIZE: 64

s_setreg_b32  hwreg(0x40), s2
// ERROR: invalid hardware register: only 6-bit values are legal

s_setreg_b32  {id: 1, offset: 2, blah: 3}, s2
// ERROR: unknown field

// SOURCE: sopk.s

s_cmpk_ge_i32 s2, 0x6
// ENCODING: [0x06, 0x00, 0x02, 0xb3] WAVE_SIZE: 64

s_cmpk_gt_u32 s2, 0x6
// ENCODING: [0x06, 0x00, 0x82, 0xb5] WAVE_SIZE: 64

s_cmpk_lg_i32 s2, 0x6
// ENCODING: [0x06, 0x00, 0x02, 0xb2] WAVE_SIZE: 64

s_cmpk_lt_i32 s2, 0x6
// ENCODING: [0x06, 0x00, 0x82, 0xb3] WAVE_SIZE: 64

s_cmpk_lt_u32 s2, 0x6
// ENCODING: [0x06, 0x00, 0x82, 0xb6] WAVE_SIZE: 64

s_mulk_i32 s2, 0x6
// ENCODING: [0x06, 0x00, 0x02, 0xb8] WAVE_SIZE: 64

// SOURCE: sopp-err.s

s_sendmsg sendmsg(MSG_SYSMSG)
// ERROR: missing message operation

s_waitcnt vmcnt(0), expcnt(0), lgkmcnt(0),
// ERROR: expected a counter name

s_waitcnt vmcnt(16)
// ERROR: too large value for vmcnt

// SOURCE: trap.s

buffer_atomic_inc v1, off, ttmp[8:11], 56 glc
// ENCODING: [0x00, 0x40, 0xf0, 0xe0, 0x00, 0x01, 0x1e, 0xb8] WAVE_SIZE: 64

s_add_u32     ttmp0, ttmp13, 4
// ERROR: ttmp13 register not available on this GPU

s_setpc_b64 ttmp[2:3]
// ENCODING: [0x72, 0x20, 0x80, 0xbe] WAVE_SIZE: 64

v_readfirstlane_b32 ttmp8, v1
// ENCODING: [0x01, 0x05, 0xf0, 0x7e] WAVE_SIZE: 64

// SOURCE: unknown-target-cpu.s

v_cmp_lt_f32_e32 vcc, s2, v4
// ENCODING: [0x02, 0x08, 0x02, 0x7c] WAVE_SIZE: 64

v_cndmask_b32_e32 v1, v2, v3, vcc
// ENCODING: [0x02, 0x07, 0x02, 0x00] WAVE_SIZE: 64

v_lshr_b32_e32 v0, v1, v2
// ENCODING: [0x01, 0x05, 0x00, 0x2a] WAVE_SIZE: 64

// SOURCE: vintrp.s

v_interp_mov_f32 v1, p20, attr0.x
// ENCODING: [0x01, 0x00, 0x06, 0xc8] WAVE_SIZE: 64

v_interp_p1_f32 v1, v0, attr0.x
// ENCODING: [0x00, 0x00, 0x04, 0xc8] WAVE_SIZE: 64

// SOURCE: vop1.s

v_bfrev_b32_e32 v1, v2
// ENCODING: [0x02, 0x71, 0x02, 0x7e] WAVE_SIZE: 64

v_cvt_f32_ubyte0_e32 v1, v2
// ENCODING: [0x02, 0x23, 0x02, 0x7e] WAVE_SIZE: 64

v_cvt_f32_ubyte3_e32 v1, v2
// ENCODING: [0x02, 0x29, 0x02, 0x7e] WAVE_SIZE: 64

v_cvt_f64_u32_e32 v[1:2], v2
// ENCODING: [0x02, 0x2d, 0x02, 0x7e] WAVE_SIZE: 64

v_cvt_i32_f64_e32 v1, v[2:3]
// ENCODING: [0x02, 0x07, 0x02, 0x7e] WAVE_SIZE: 64

v_ffbh_i32_e32 v1, v2
// ENCODING: [0x02, 0x77, 0x02, 0x7e] WAVE_SIZE: 64

v_floor_f32_e32 v1, v2
// ENCODING: [0x02, 0x49, 0x02, 0x7e] WAVE_SIZE: 64

v_frexp_mant_f32_e32 v1, v2
// ENCODING: [0x02, 0x81, 0x02, 0x7e] WAVE_SIZE: 64

v_log_f32_e32 v1, v2
// ENCODING: [0x02, 0x4f, 0x02, 0x7e] WAVE_SIZE: 64

v_movrelsd_b32_e32 v1, v2
// ENCODING: [0x02, 0x89, 0x02, 0x7e] WAVE_SIZE: 64

v_nop
// ENCODING: [0x00, 0x00, 0x00, 0x7e] WAVE_SIZE: 64

v_rcp_clamp_f32_e32 v1, v2
// ENCODING: [0x02, 0x51, 0x02, 0x7e] WAVE_SIZE: 64

v_rsq_clamp_f64_e32 v[1:2], v[2:3]
// ENCODING: [0x02, 0x65, 0x02, 0x7e] WAVE_SIZE: 64

v_rsq_legacy_f32_e32 v1, v2
// ENCODING: [0x02, 0x5b, 0x02, 0x7e] WAVE_SIZE: 64

// SOURCE: vop2-err.s

v_add_i32_e32 v1, s[0:1], v2, v3
// ERROR: invalid operand for instruction

// SOURCE: vop3-convert.s

v_ashrrev_i32_e32 v1, v2, v3
// ENCODING: [0x02, 0x07, 0x02, 0x30] WAVE_SIZE: 64

v_cvt_pk_i16_i32_e32 v1, v2, v3
// ENCODING: [0x02, 0x07, 0x02, 0x62] WAVE_SIZE: 64

v_cvt_pk_u16_u32_e32 v1, v2, v3
// ENCODING: [0x02, 0x07, 0x02, 0x60] WAVE_SIZE: 64

v_max_i32_e32 v1, v2, v3
// ENCODING: [0x02, 0x07, 0x02, 0x24] WAVE_SIZE: 64

v_mbcnt_lo_u32_b32_e32 v1, v2, v3
// ENCODING: [0x02, 0x07, 0x02, 0x46] WAVE_SIZE: 64

v_mul_i32_i24_e32 v1, 3, v3
// ENCODING: [0x83, 0x06, 0x02, 0x12] WAVE_SIZE: 64

v_mul_u32_u24_e32 v1, v2, v3
// ENCODING: [0x02, 0x07, 0x02, 0x16] WAVE_SIZE: 64

v_xor_b32_e32 v1, v2, v3
// ENCODING: [0x02, 0x07, 0x02, 0x3a] WAVE_SIZE: 64

// SOURCE: vop3-gfx9.s

v_add_i32_e64 v84, vcc, v13, v31
// ENCODING: [0x54, 0x6a, 0x4a, 0xd2, 0x0d, 0x3f, 0x02, 0x00] WAVE_SIZE: 64

// SOURCE: vop3-vop1-nosrc.s

v_clrexcp_e64
// ENCODING: [0x00, 0x00, 0x82, 0xd3, 0x00, 0x00, 0x00, 0x00] WAVE_SIZE: 64

// SOURCE: vop3.s

v_add_f32_e64 v1, v3, v5
// ENCODING: [0x01, 0x00, 0x06, 0xd2, 0x03, 0x0b, 0x02, 0x00] WAVE_SIZE: 64

v_add_f64 v[0:1], -v[2:3], |v[5:6]| clamp mul:4
// ENCODING: [0x00, 0x0a, 0xc8, 0xd2, 0x02, 0x0b, 0x02, 0x30] WAVE_SIZE: 64

v_cmp_class_f32_e64 s[10:11], -v1, s2
// ENCODING: [0x0a, 0x00, 0x10, 0xd1, 0x01, 0x05, 0x00, 0x20] WAVE_SIZE: 64

v_cmp_le_f32_e64 s[2:3], v4, v6
// ENCODING: [0x02, 0x00, 0x06, 0xd0, 0x04, 0x0d, 0x02, 0x00] WAVE_SIZE: 64

v_cmp_lt_f32_e64 s[2:3], -|v4|, v6
// ENCODING: [0x02, 0x01, 0x02, 0xd0, 0x04, 0x0d, 0x02, 0x20] WAVE_SIZE: 64

v_cmpx_class_f32_e64 s[10:11], 0, s101
// ENCODING: [0x0a, 0x00, 0x30, 0xd1, 0x80, 0xca, 0x00, 0x00] WAVE_SIZE: 64

v_cubeid_f32 v0, |-1|, |-1.0|, |1.0|
// ENCODING: [0x00, 0x07, 0x88, 0xd2, 0xc1, 0xe6, 0xc9, 0x03] WAVE_SIZE: 64

v_div_scale_f32 v24, vcc, v22, v22, v20
// ENCODING: [0x18, 0x6a, 0xda, 0xd2, 0x16, 0x2d, 0x52, 0x04] WAVE_SIZE: 64

v_mac_legacy_f32_e64 v1, v3, s5
// ENCODING: [0x01, 0x00, 0x0c, 0xd2, 0x03, 0x0b, 0x00, 0x00] WAVE_SIZE: 64

v_mul_f64 v[0:1], |0|, |0|
// ENCODING: [0x00, 0x03, 0xca, 0xd2, 0x80, 0x00, 0x01, 0x00] WAVE_SIZE: 64

v_subrev_f32_e64 v1, v3, s5
// ENCODING: [0x01, 0x00, 0x0a, 0xd2, 0x03, 0x0b, 0x00, 0x00] WAVE_SIZE: 64

// SOURCE: vopc-errs.s

v_cmp_lt_f32_e32 s[0:1], v2, v4
// ERROR: invalid operand for instruction

// SOURCE: vopc-vi.s

v_cmp_f_i16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmp_f_i16

v_cmp_ge_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmp_ge_f16

v_cmp_gt_u16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmp_gt_u16

v_cmp_le_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmp_le_f16

v_cmp_lg_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmp_lg_f16

v_cmp_lt_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmp_lt_f16

v_cmp_neq_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmp_neq_f16

v_cmp_nle_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmp_nle_f16

v_cmp_o_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmp_o_f16

v_cmpx_class_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmpx_class_f16

v_cmpx_eq_i16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmpx_eq_i16

v_cmpx_f_u16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmpx_f_u16

v_cmpx_gt_u16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmpx_gt_u16

v_cmpx_lt_i16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmpx_lt_i16

v_cmpx_lt_u16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmpx_lt_u16

v_cmpx_ne_u16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmpx_ne_u16

v_cmpx_nge_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmpx_nge_f16

v_cmpx_tru_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (tahiti): v_cmpx_tru_f16

// SOURCE: vopc.s

v_cmp_f_f32_e32 vcc, v2, v4
// ENCODING: [0x02, 0x09, 0x00, 0x7c] WAVE_SIZE: 64

v_cmp_f_i64_e32 vcc, v[2:3], v[4:5]
// ENCODING: [0x02, 0x09, 0x40, 0x7d] WAVE_SIZE: 64

// SOURCE: xnack-mask.s

s_mov_b32 xnack_mask_hi, -1
// ERROR: xnack_mask_hi register not available on this GPU
