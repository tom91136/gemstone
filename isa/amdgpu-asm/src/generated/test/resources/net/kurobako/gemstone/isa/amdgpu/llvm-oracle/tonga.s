// Generated from pinned LLVM MC/AMDGPU checks. Regenerate with: sbt codegen
// TARGET: tonga

// SOURCE: buffer_wbinv1l_vol_vi.s

buffer_wbinvl1_vol
// ENCODING: [0x00, 0x00, 0xfc, 0xe0, 0x00, 0x00, 0x00, 0x00] WAVE_SIZE: 64

// SOURCE: dpp-err.s

v_mov_b32_dpp v0, v1 row_xmask:1 row_mask:0x1 bank_mask:0x1
// ERROR: not a valid operand.

// SOURCE: ds-gfx9.s

ds_read_i8_d16 v8, v2
// ERROR: instruction not supported on this GPU (tonga): ds_read_i8_d16

ds_write_b8_d16_hi v8, v2
// ERROR: instruction not supported on this GPU (tonga): ds_write_b8_d16_hi

// SOURCE: ds.s

ds_add_rtn_u64 v[8:9], v2, v[4:5]
// ENCODING: [0x00, 0x00, 0xc0, 0xd8, 0x02, 0x04, 0x00, 0x08] WAVE_SIZE: 64

ds_cmpst_f64 v2, v[4:5], v[6:7]
// ENCODING: [0x00, 0x00, 0xa2, 0xd8, 0x02, 0x04, 0x06, 0x00] WAVE_SIZE: 64

ds_dec_rtn_u32 v8, v2, v4
// ENCODING: [0x00, 0x00, 0x48, 0xd8, 0x02, 0x04, 0x00, 0x08] WAVE_SIZE: 64

ds_gws_sema_p gds
// ENCODING: [0x00, 0x00, 0x39, 0xd9, 0x00, 0x00, 0x00, 0x00] WAVE_SIZE: 64

ds_inc_u32 v2, v4
// ENCODING: [0x00, 0x00, 0x06, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_max_rtn_i32 v8, v2, v4
// ENCODING: [0x00, 0x00, 0x4c, 0xd8, 0x02, 0x04, 0x00, 0x08] WAVE_SIZE: 64

ds_min_i32 v2, v4
// ENCODING: [0x00, 0x00, 0x0a, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_min_i64 v2, v[4:5]
// ENCODING: [0x00, 0x00, 0x8a, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_min_u64 v2, v[4:5]
// ENCODING: [0x00, 0x00, 0x8e, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_or_rtn_b32 v8, v2, v4
// ENCODING: [0x00, 0x00, 0x54, 0xd8, 0x02, 0x04, 0x00, 0x08] WAVE_SIZE: 64

ds_read2_b64 v[8:11], v2
// ENCODING: [0x00, 0x00, 0xee, 0xd8, 0x02, 0x00, 0x00, 0x08] WAVE_SIZE: 64

ds_read_i16 v8, v2
// ENCODING: [0x00, 0x00, 0x76, 0xd8, 0x02, 0x00, 0x00, 0x08] WAVE_SIZE: 64

ds_rsub_u32 v2, v4
// ENCODING: [0x00, 0x00, 0x04, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_sub_u64 v2, v[4:5]
// ENCODING: [0x00, 0x00, 0x82, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_swizzle_b32 v8, v2 offset:0x001
// ENCODING: [0x01, 0x00, 0x7a, 0xd8, 0x02, 0x00, 0x00, 0x08] WAVE_SIZE: 64

ds_write_b16 v2, v4
// ENCODING: [0x00, 0x00, 0x3e, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

ds_wrxchg2_rtn_b32 v[8:9], v2, v4, v6
// ENCODING: [0x00, 0x00, 0x5c, 0xd8, 0x02, 0x04, 0x06, 0x08] WAVE_SIZE: 64

ds_xor_b64 v2, v[4:5]
// ENCODING: [0x00, 0x00, 0x96, 0xd8, 0x02, 0x04, 0x00, 0x00] WAVE_SIZE: 64

// SOURCE: ds_swizzle.s

ds_swizzle_b32 v5, v1 offset:swizzle(ROTATE,2,31)
// ERROR: Rotate mode swizzle not supported on this GPU

// SOURCE: exp-gfx10.s

exp pos4 v4, v3, v2, v1
// ERROR: exp target is not supported on this GPU

// SOURCE: flat-scratch-st-mode.s

scratch_load_dwordx3 v[1:3], off, off
// ERROR: instruction not supported on this GPU (tonga): scratch_load_dwordx3

scratch_load_short_d16 v1, off, off
// ERROR: instruction not supported on this GPU (tonga): scratch_load_short_d16

scratch_load_ubyte_d16_hi v1, off, off
// ERROR: instruction not supported on this GPU (tonga): scratch_load_ubyte_d16_hi

// SOURCE: gfx8_asm_ds.s

ds_add_f32 v1, v2 offset:4
// ENCODING: [0x04, 0x00, 0x2a, 0xd8, 0x01, 0x02, 0x00, 0x00] WAVE_SIZE: 64

ds_add_f32 v1, v2 offset:65535 gds
// ENCODING: [0xff, 0xff, 0x2b, 0xd8, 0x01, 0x02, 0x00, 0x00] WAVE_SIZE: 64

ds_add_src2_u32 v1
// ENCODING: [0x00, 0x00, 0x00, 0xd9, 0x01, 0x00, 0x00, 0x00] WAVE_SIZE: 64

ds_and_src2_b32 v1
// ENCODING: [0x00, 0x00, 0x12, 0xd9, 0x01, 0x00, 0x00, 0x00] WAVE_SIZE: 64

ds_max_src2_i64 v1
// ENCODING: [0x00, 0x00, 0x8c, 0xd9, 0x01, 0x00, 0x00, 0x00] WAVE_SIZE: 64

ds_min_src2_f32 v1
// ENCODING: [0x00, 0x00, 0x24, 0xd9, 0x01, 0x00, 0x00, 0x00] WAVE_SIZE: 64

ds_write_b96 v1, v[253:255] offset:65535
// ENCODING: [0xff, 0xff, 0xbc, 0xd9, 0x01, 0xfd, 0x00, 0x00] WAVE_SIZE: 64

// SOURCE: gfx8_asm_ds_err.s

ds_gws_init v1 nogds
// ERROR: nogds is not allowed

// SOURCE: gfx8_asm_exp.s

exp mrt0, off, off, v0, off
// ENCODING: [0x04, 0x00, 0x00, 0xc4, 0x00, 0x00, 0x00, 0x00] WAVE_SIZE: 64

// SOURCE: gfx8_asm_flat.s

flat_atomic_add v[1:2], v2 slc
// ENCODING: [0x00, 0x00, 0x0a, 0xdd, 0x01, 0x02, 0x00, 0x00] WAVE_SIZE: 64

flat_atomic_and v0, v[1:2], v2 glc
// ENCODING: [0x00, 0x00, 0x21, 0xdd, 0x01, 0x02, 0x00, 0x00] WAVE_SIZE: 64

flat_atomic_inc_x2 v[0:1], v[1:2], v[2:3] glc
// ENCODING: [0x00, 0x00, 0xad, 0xdd, 0x01, 0x02, 0x00, 0x00] WAVE_SIZE: 64

flat_atomic_sub_x2 v[0:1], v[1:2], v[2:3] glc
// ENCODING: [0x00, 0x00, 0x8d, 0xdd, 0x01, 0x02, 0x00, 0x00] WAVE_SIZE: 64

flat_atomic_xor v0, v[1:2], v2 glc
// ENCODING: [0x00, 0x00, 0x29, 0xdd, 0x01, 0x02, 0x00, 0x00] WAVE_SIZE: 64

flat_load_ushort v255, v[1:2]
// ENCODING: [0x00, 0x00, 0x48, 0xdc, 0x01, 0x00, 0x00, 0xff] WAVE_SIZE: 64

// SOURCE: gfx8_asm_mimg.s

image_atomic_add v5, v1, s[8:15] dmask:0x1 unorm
// ENCODING: [0x00, 0x11, 0x48, 0xf0, 0x01, 0x05, 0x02, 0x00] WAVE_SIZE: 64

image_atomic_and v252, v1, s[8:15] dmask:0x1 unorm
// ENCODING: [0x00, 0x11, 0x60, 0xf0, 0x01, 0xfc, 0x02, 0x00] WAVE_SIZE: 64

image_atomic_smax v252, v1, s[8:15] dmask:0x1 unorm
// ENCODING: [0x00, 0x11, 0x58, 0xf0, 0x01, 0xfc, 0x02, 0x00] WAVE_SIZE: 64

image_atomic_sub v252, v1, s[8:15] dmask:0x1 unorm
// ENCODING: [0x00, 0x11, 0x4c, 0xf0, 0x01, 0xfc, 0x02, 0x00] WAVE_SIZE: 64

image_gather4_b_o v[252:255], v[1:4], s[8:15], s[12:15] dmask:0x1
// ENCODING: [0x00, 0x01, 0x54, 0xf1, 0x01, 0xfc, 0x62, 0x00] WAVE_SIZE: 64

image_gather4_c_cl_o v[252:255], v[1:8], s[8:15], s[12:15] dmask:0x1
// ENCODING: [0x00, 0x01, 0x64, 0xf1, 0x01, 0xfc, 0x62, 0x00] WAVE_SIZE: 64

image_gather4_cl_o v[252:255], v[1:4], s[8:15], s[12:15] dmask:0x1
// ENCODING: [0x00, 0x01, 0x44, 0xf1, 0x01, 0xfc, 0x62, 0x00] WAVE_SIZE: 64

image_load_mip v252, v[1:2], s[8:15] dmask:0x1
// ENCODING: [0x00, 0x01, 0x04, 0xf0, 0x01, 0xfc, 0x02, 0x00] WAVE_SIZE: 64

image_sample_c_b_cl v252, v[1:4], s[8:15], s[12:15] dmask:0x1
// ENCODING: [0x00, 0x01, 0xb8, 0xf0, 0x01, 0xfc, 0x62, 0x00] WAVE_SIZE: 64

image_sample_c_cl_o v252, v[1:4], s[8:15], s[12:15] dmask:0x1
// ENCODING: [0x00, 0x01, 0xe4, 0xf0, 0x01, 0xfc, 0x62, 0x00] WAVE_SIZE: 64

image_sample_c_lz v252, v[1:2], s[8:15], s[12:15] dmask:0x1
// ENCODING: [0x00, 0x01, 0xbc, 0xf0, 0x01, 0xfc, 0x62, 0x00] WAVE_SIZE: 64

image_sample_c_o v252, v[1:3], s[8:15], s[12:15] dmask:0x1
// ENCODING: [0x00, 0x01, 0xe0, 0xf0, 0x01, 0xfc, 0x62, 0x00] WAVE_SIZE: 64

image_sample_cl v252, v[1:2], s[8:15], s[12:15] dmask:0x1
// ENCODING: [0x00, 0x01, 0x84, 0xf0, 0x01, 0xfc, 0x62, 0x00] WAVE_SIZE: 64

image_sample_lz v252, v1, s[8:15], s[12:15] dmask:0x1
// ENCODING: [0x00, 0x01, 0x9c, 0xf0, 0x01, 0xfc, 0x62, 0x00] WAVE_SIZE: 64

// SOURCE: gfx8_asm_mtbuf.s

tbuffer_load_format_d16_x v5, off, s[8:11], s3 format:[BUF_NUM_FORMAT_SNORM] offset:4095
// ENCODING: [0xff, 0x0f, 0x8c, 0xe8, 0x00, 0x05, 0x02, 0x03] WAVE_SIZE: 64

tbuffer_load_format_x v255, off, s[8:11], s3 offset:4095
// ENCODING: [0xff, 0x0f, 0x08, 0xe8, 0x00, 0xff, 0x02, 0x03] WAVE_SIZE: 64

tbuffer_store_format_xyzw v[1:4], off, s[12:15], -1 offset:4095
// ENCODING: [0xff, 0x8f, 0x0b, 0xe8, 0x00, 0x01, 0x03, 0xc1] WAVE_SIZE: 64

// SOURCE: gfx8_asm_mubuf.s

buffer_atomic_add v5, off, s[8:11], -1 offset:4095
// ENCODING: [0xff, 0x0f, 0x08, 0xe1, 0x00, 0x05, 0x02, 0xc1] WAVE_SIZE: 64

buffer_atomic_add v5, off, s[8:11], -4.0 offset:4095
// ENCODING: [0xff, 0x0f, 0x08, 0xe1, 0x00, 0x05, 0x02, 0xf7] WAVE_SIZE: 64

buffer_atomic_add v5, off, s[8:11], exec_hi offset:4095
// ENCODING: [0xff, 0x0f, 0x08, 0xe1, 0x00, 0x05, 0x02, 0x7f] WAVE_SIZE: 64

buffer_atomic_add v5, off, s[8:11], s101 offset:4095
// ENCODING: [0xff, 0x0f, 0x08, 0xe1, 0x00, 0x05, 0x02, 0x65] WAVE_SIZE: 64

buffer_atomic_add v5, off, s[8:11], s3 offset:4095 glc
// ENCODING: [0xff, 0x4f, 0x08, 0xe1, 0x00, 0x05, 0x02, 0x03] WAVE_SIZE: 64

buffer_atomic_add v5, off, s[8:11], src_execz offset:4095
// ENCODING: [0xff, 0x0f, 0x08, 0xe1, 0x00, 0x05, 0x02, 0xfc] WAVE_SIZE: 64

buffer_atomic_add v5, off, s[8:11], src_vccz offset:4095
// ENCODING: [0xff, 0x0f, 0x08, 0xe1, 0x00, 0x05, 0x02, 0xfb] WAVE_SIZE: 64

buffer_atomic_add v5, off, s[8:11], ttmp11 offset:4095
// ENCODING: [0xff, 0x0f, 0x08, 0xe1, 0x00, 0x05, 0x02, 0x7b] WAVE_SIZE: 64

buffer_atomic_add_x2 v[254:255], off, s[8:11], s3 offset:4095
// ENCODING: [0xff, 0x0f, 0x88, 0xe1, 0x00, 0xfe, 0x02, 0x03] WAVE_SIZE: 64

buffer_atomic_dec v255, off, s[8:11], s3 offset:4095
// ENCODING: [0xff, 0x0f, 0x30, 0xe1, 0x00, 0xff, 0x02, 0x03] WAVE_SIZE: 64

buffer_atomic_smin v255, off, s[8:11], s3 offset:4095
// ENCODING: [0xff, 0x0f, 0x10, 0xe1, 0x00, 0xff, 0x02, 0x03] WAVE_SIZE: 64

buffer_atomic_umin v255, off, s[8:11], s3 offset:4095
// ENCODING: [0xff, 0x0f, 0x14, 0xe1, 0x00, 0xff, 0x02, 0x03] WAVE_SIZE: 64

buffer_load_format_xy v[254:255], off, s[8:11], s3 offset:4095
// ENCODING: [0xff, 0x0f, 0x04, 0xe0, 0x00, 0xfe, 0x02, 0x03] WAVE_SIZE: 64

buffer_store_dword v1, off, s[12:15], -1 offset:4095
// ENCODING: [0xff, 0x0f, 0x70, 0xe0, 0x00, 0x01, 0x03, 0xc1] WAVE_SIZE: 64

buffer_wbinvl1
// ENCODING: [0x00, 0x00, 0xf8, 0xe0, 0x00, 0x00, 0x00, 0x00] WAVE_SIZE: 64

// SOURCE: gfx8_asm_smem.s

s_atc_probe 0, s[4:5], 0x7ffff
// ENCODING: [0x02, 0x00, 0x9a, 0xc0, 0xff, 0xff, 0x07, 0x00] WAVE_SIZE: 64

s_buffer_store_dword flat_scratch_hi, s[8:11], m0
// ENCODING: [0xc4, 0x19, 0x60, 0xc0, 0x7c, 0x00, 0x00, 0x00] WAVE_SIZE: 64

s_buffer_store_dwordx4 s[4:7], s[12:15], m0
// ENCODING: [0x06, 0x01, 0x68, 0xc0, 0x7c, 0x00, 0x00, 0x00] WAVE_SIZE: 64

s_store_dword flat_scratch_hi, s[4:5], m0
// ENCODING: [0xc2, 0x19, 0x40, 0xc0, 0x7c, 0x00, 0x00, 0x00] WAVE_SIZE: 64

// SOURCE: gfx8_asm_sop1.s

s_abs_i32 tma_lo, s1
// ENCODING: [0x01, 0x30, 0xee, 0xbe] WAVE_SIZE: 64

s_getpc_b64 exec
// ENCODING: [0x00, 0x1c, 0xfe, 0xbe] WAVE_SIZE: 64

// SOURCE: gfx8_asm_sop2.s

s_absdiff_i32 s5, s1, src_vccz
// ENCODING: [0x01, 0xfb, 0x05, 0x95] WAVE_SIZE: 64

// SOURCE: gfx8_asm_sopc.s

s_bitcmp0_b32 s1, vcc_hi
// ENCODING: [0x01, 0x6b, 0x0c, 0xbf] WAVE_SIZE: 64

// SOURCE: gfx8_asm_sopk.s

s_addk_i32 flat_scratch_hi, 0x3141
// ENCODING: [0x41, 0x31, 0x67, 0xb7] WAVE_SIZE: 64

// SOURCE: gfx8_asm_sopp.s

s_barrier
// ENCODING: [0x00, 0x00, 0x8a, 0xbf] WAVE_SIZE: 64

// SOURCE: gfx8_asm_vintrp.s

v_interp_mov_f32_e32 v5, p10, attr0.w
// ENCODING: [0x00, 0x03, 0x16, 0xd4] WAVE_SIZE: 64

// SOURCE: gfx8_asm_vop1.s

v_bfrev_b32_dpp v5, v1 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x1
// ENCODING: [0xfa, 0x58, 0x0a, 0x7e, 0x01, 0xe4, 0x00, 0x01] WAVE_SIZE: 64

v_cos_f32_dpp v255, v1 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x54, 0xfe, 0x7f, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

v_cvt_f32_f64_e32 v255, v[1:2]
// ENCODING: [0x01, 0x1f, 0xfe, 0x7f] WAVE_SIZE: 64

v_cvt_f64_f32_e32 v[254:255], v1
// ENCODING: [0x01, 0x21, 0xfc, 0x7f] WAVE_SIZE: 64

v_cvt_off_f32_i4_dpp v255, v1 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x1c, 0xfe, 0x7f, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

v_cvt_rpi_i32_f32_dpp v255, v1 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x18, 0xfe, 0x7f, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

v_floor_f32_dpp v255, v1 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x3e, 0xfe, 0x7f, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

v_fract_f64_e32 v[254:255], v[1:2]
// ENCODING: [0x01, 0x65, 0xfc, 0x7f] WAVE_SIZE: 64

v_frexp_exp_i32_f32_dpp v255, v1 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x66, 0xfe, 0x7f, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

v_rcp_f16_dpp v255, v1 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x7a, 0xfe, 0x7f, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

v_rsq_f32_dpp v255, v1 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x48, 0xfe, 0x7f, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

// SOURCE: gfx8_asm_vop2.s

v_add_f16_dpp v5, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0xf
// ENCODING: [0xfa, 0x04, 0x0a, 0x3e, 0x01, 0xe4, 0x00, 0x0f] WAVE_SIZE: 64

v_add_f16_e32 v5, -4.0, v2
// ENCODING: [0xf7, 0x04, 0x0a, 0x3e] WAVE_SIZE: 64

v_add_f16_sdwa v255, v1, v2 dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:DWORD src1_sel:DWORD
// ENCODING: [0xf9, 0x04, 0xfe, 0x3f, 0x01, 0x06, 0x06, 0x06] WAVE_SIZE: 64

v_lshrrev_b16_dpp v255, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0xfe, 0x57, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

v_max_u32_dpp v255, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0xfe, 0x1f, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

v_min_i32_dpp v255, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0xfe, 0x19, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

v_min_u16_dpp v255, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0xfe, 0x63, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

v_sub_u16_dpp v255, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0xfe, 0x4f, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

v_xor_b32_dpp v255, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0xfe, 0x2b, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

// SOURCE: gfx8_asm_vop3.s

v_add_f16_e64 v5, v1, m0
// ENCODING: [0x05, 0x00, 0x1f, 0xd1, 0x01, 0xf9, 0x00, 0x00] WAVE_SIZE: 64

v_add_f16_e64 v5, v1, v2 clamp
// ENCODING: [0x05, 0x80, 0x1f, 0xd1, 0x01, 0x05, 0x02, 0x00] WAVE_SIZE: 64

// SOURCE: gfx8_asm_vop3_e64.s

v_add_f64 v[5:6], v[1:2], tma
// ENCODING: [0x05, 0x00, 0x80, 0xd2, 0x01, 0xdd, 0x00, 0x00] WAVE_SIZE: 64

v_bcnt_u32_b32 v255, v1, v2
// ENCODING: [0xff, 0x00, 0x8b, 0xd2, 0x01, 0x05, 0x02, 0x00] WAVE_SIZE: 64

v_div_fmas_f64 v[254:255], v[1:2], v[2:3], v[3:4]
// ENCODING: [0xfe, 0x00, 0xe3, 0xd1, 0x01, 0x05, 0x0e, 0x04] WAVE_SIZE: 64

v_ldexp_f64 v[254:255], v[1:2], v2
// ENCODING: [0xfe, 0x00, 0x84, 0xd2, 0x01, 0x05, 0x02, 0x00] WAVE_SIZE: 64

v_max3_i32 v255, v1, v2, v3
// ENCODING: [0xff, 0x00, 0xd4, 0xd1, 0x01, 0x05, 0x0e, 0x04] WAVE_SIZE: 64

v_min3_i32 v255, v1, v2, v3
// ENCODING: [0xff, 0x00, 0xd1, 0xd1, 0x01, 0x05, 0x0e, 0x04] WAVE_SIZE: 64

v_mul_hi_u32 v255, v1, v2
// ENCODING: [0xff, 0x00, 0x86, 0xd2, 0x01, 0x05, 0x02, 0x00] WAVE_SIZE: 64

// SOURCE: gfx8_asm_vopc.s

v_cmp_class_f16 vcc, v1, v2 src0_sel:BYTE_3 src1_sel:DWORD
// ENCODING: [0xf9, 0x04, 0x28, 0x7c, 0x01, 0x00, 0x03, 0x06] WAVE_SIZE: 64

v_cmp_eq_i16 vcc, sext(v1), v2 src0_sel:DWORD src1_sel:DWORD
// ENCODING: [0xf9, 0x04, 0x44, 0x7d, 0x01, 0x00, 0x0e, 0x06] WAVE_SIZE: 64

v_cmp_eq_i32 vcc, sext(v1), v2 src0_sel:DWORD src1_sel:DWORD
// ENCODING: [0xf9, 0x04, 0x84, 0x7d, 0x01, 0x00, 0x0e, 0x06] WAVE_SIZE: 64

v_cmp_ge_i32 vcc, sext(v1), v2 src0_sel:DWORD src1_sel:DWORD
// ENCODING: [0xf9, 0x04, 0x8c, 0x7d, 0x01, 0x00, 0x0e, 0x06] WAVE_SIZE: 64

v_cmp_gt_u64_e32 vcc, -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0xd8, 0x7d] WAVE_SIZE: 64

v_cmp_lt_f64_e32 vcc, -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0xc2, 0x7c] WAVE_SIZE: 64

v_cmp_ne_u32 vcc, sext(v1), v2 src0_sel:DWORD src1_sel:DWORD
// ENCODING: [0xf9, 0x04, 0x9a, 0x7d, 0x01, 0x00, 0x0e, 0x06] WAVE_SIZE: 64

v_cmp_neq_f64_e32 vcc, -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0xda, 0x7c] WAVE_SIZE: 64

v_cmp_nle_f32 vcc, -v1, v2 src0_sel:DWORD src1_sel:DWORD
// ENCODING: [0xf9, 0x04, 0x98, 0x7c, 0x01, 0x00, 0x16, 0x06] WAVE_SIZE: 64

v_cmp_o_f64_e32 vcc, -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0xce, 0x7c] WAVE_SIZE: 64

v_cmp_u_f64_e32 vcc, -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0xd0, 0x7c] WAVE_SIZE: 64

v_cmpx_f_f32 vcc, -v1, v2 src0_sel:DWORD src1_sel:DWORD
// ENCODING: [0xf9, 0x04, 0xa0, 0x7c, 0x01, 0x00, 0x16, 0x06] WAVE_SIZE: 64

v_cmpx_ge_f64_e32 vcc, -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0xec, 0x7c] WAVE_SIZE: 64

v_cmpx_gt_f64_e32 vcc, -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0xe8, 0x7c] WAVE_SIZE: 64

v_cmpx_gt_i64_e32 vcc, -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0xe8, 0x7d] WAVE_SIZE: 64

v_cmpx_le_u32 vcc, sext(v1), v2 src0_sel:DWORD src1_sel:DWORD
// ENCODING: [0xf9, 0x04, 0xb6, 0x7d, 0x01, 0x00, 0x0e, 0x06] WAVE_SIZE: 64

v_cmpx_lt_u32 vcc, sext(v1), v2 src0_sel:DWORD src1_sel:DWORD
// ENCODING: [0xf9, 0x04, 0xb2, 0x7d, 0x01, 0x00, 0x0e, 0x06] WAVE_SIZE: 64

v_cmpx_neq_f64_e32 vcc, -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0xfa, 0x7c] WAVE_SIZE: 64

v_cmpx_nlg_f32 vcc, -v1, v2 src0_sel:DWORD src1_sel:DWORD
// ENCODING: [0xf9, 0x04, 0xb4, 0x7c, 0x01, 0x00, 0x16, 0x06] WAVE_SIZE: 64

v_cmpx_t_u32 vcc, sext(v1), v2 src0_sel:DWORD src1_sel:DWORD
// ENCODING: [0xf9, 0x04, 0xbe, 0x7d, 0x01, 0x00, 0x0e, 0x06] WAVE_SIZE: 64

v_cmpx_tru_f64_e32 vcc, -1, v[2:3]
// ENCODING: [0xc1, 0x04, 0xfe, 0x7c] WAVE_SIZE: 64

// SOURCE: gfx8_err_pos.s

image_gather4 v[5:8], v1, s[8:15], s[12:15] dmask:0x1 noa16
// ERROR: a16 modifier is not supported on this GPU

// SOURCE: gfx8_unsupported.s

buffer_atomic_add_f32 v255, off, s[8:11], s3 offset:4095
// ERROR: instruction not supported on this GPU (tonga): buffer_atomic_add_f32

buffer_atomic_fcmpswap v[0:1], off, s[0:3], s0 offset:4095
// ERROR: instruction not supported on this GPU (tonga): buffer_atomic_fcmpswap

buffer_atomic_fcmpswap_x2 v[0:3], off, s[0:3], s0 offset:4095
// ERROR: instruction not supported on this GPU (tonga): buffer_atomic_fcmpswap_x2

buffer_atomic_fmax v0, off, s[0:3], s0 offset:4095 glc
// ERROR: instruction not supported on this GPU (tonga): buffer_atomic_fmax

buffer_atomic_fmax_x2 v[0:1], v0, s[0:3], s0 idxen offset:4095
// ERROR: instruction not supported on this GPU (tonga): buffer_atomic_fmax_x2

buffer_store_byte_d16_hi v1, off, s[12:15], -1 offset:4095
// ERROR: instruction not supported on this GPU (tonga): buffer_store_byte_d16_hi

buffer_store_format_d16_hi_x v1, off, s[12:15], s4 offset:4095 glc
// ERROR: instruction not supported on this GPU (tonga): buffer_store_format_d16_hi_x

flat_load_sbyte_d16_hi v1, v[3:4]
// ERROR: instruction not supported on this GPU (tonga): flat_load_sbyte_d16_hi

flat_load_ubyte_d16 v1, v[3:4]
// ERROR: instruction not supported on this GPU (tonga): flat_load_ubyte_d16

global_atomic_cmpswap_x2 v[1:2], v[252:255], off offset:-1
// ERROR: instruction not supported on this GPU (tonga): global_atomic_cmpswap_x2

global_atomic_xor v[1:2], v2, off
// ERROR: instruction not supported on this GPU (tonga): global_atomic_xor

global_load_ushort v1, v[3:4], off
// ERROR: instruction not supported on this GPU (tonga): global_load_ushort

image_sample_c_d_o_g16 v[5:6], v[1:5], s[8:15], s[12:15] dmask:0x3
// ERROR: instruction not supported on this GPU (tonga): image_sample_c_d_o_g16

image_sample_d_o_g16 v[5:6], v[1:4], s[8:15], s[12:15] dmask:0x3
// ERROR: instruction not supported on this GPU (tonga): image_sample_d_o_g16

s_atomic_add s5, s[2:3], 0x0
// ERROR: instruction not supported on this GPU (tonga): s_atomic_add

s_atomic_add_x2 s[10:11], s[2:3], s101
// ERROR: instruction not supported on this GPU (tonga): s_atomic_add_x2

s_atomic_or s5, s[2:3], 0x0
// ERROR: instruction not supported on this GPU (tonga): s_atomic_or

s_atomic_sub s5, s[2:3], s101
// ERROR: instruction not supported on this GPU (tonga): s_atomic_sub

s_atomic_umax s5, s[2:3], s0 glc
// ERROR: instruction not supported on this GPU (tonga): s_atomic_umax

s_buffer_atomic_and s101, s[4:7], s0
// ERROR: instruction not supported on this GPU (tonga): s_buffer_atomic_and

s_buffer_atomic_dec s5, s[4:7], s0
// ERROR: instruction not supported on this GPU (tonga): s_buffer_atomic_dec

s_buffer_atomic_smax s5, s[4:7], s101
// ERROR: instruction not supported on this GPU (tonga): s_buffer_atomic_smax

s_buffer_atomic_umin s5, s[4:7], s0
// ERROR: instruction not supported on this GPU (tonga): s_buffer_atomic_umin

s_buffer_atomic_umin_x2 s[10:11], s[4:7], s0 glc
// ERROR: instruction not supported on this GPU (tonga): s_buffer_atomic_umin_x2

s_denorm_mode 0x0
// ERROR: instruction not supported on this GPU (tonga): s_denorm_mode

s_lshl4_add_u32 exec_hi, s1, s2
// ERROR: instruction not supported on this GPU (tonga): s_lshl4_add_u32

s_mul_hi_i32 exec_hi, s1, s2
// ERROR: instruction not supported on this GPU (tonga): s_mul_hi_i32

s_nand_saveexec_b32 exec_hi, s1
// ERROR: instruction not supported on this GPU (tonga): s_nand_saveexec_b32

s_pack_lh_b32_b16 exec_hi, s1, s2
// ERROR: instruction not supported on this GPU (tonga): s_pack_lh_b32_b16

s_scratch_load_dwordx4 s[20:23], s[4:5], s0
// ERROR: instruction not supported on this GPU (tonga): s_scratch_load_dwordx4

s_subvector_loop_end exec_hi, 0x1234
// ERROR: instruction not supported on this GPU (tonga): s_subvector_loop_end

v_add_lshl_u32 v1, v2, v3, v4
// ERROR: instruction not supported on this GPU (tonga): v_add_lshl_u32

v_cmps_f_f32 vcc, -1, v2
// ERROR: instruction not supported on this GPU (tonga): v_cmps_f_f32

v_cmps_lt_f32 vcc, -1, v2
// ERROR: instruction not supported on this GPU (tonga): v_cmps_lt_f32

v_cmps_nle_f32 vcc, -1, v2
// ERROR: instruction not supported on this GPU (tonga): v_cmps_nle_f32

v_cmps_nlg_f64 vcc, -1, v[2:3]
// ERROR: instruction not supported on this GPU (tonga): v_cmps_nlg_f64

v_cmps_o_f32 vcc, -1, v2
// ERROR: instruction not supported on this GPU (tonga): v_cmps_o_f32

v_cmpsx_eq_f64 vcc, -1, v[2:3]
// ERROR: instruction not supported on this GPU (tonga): v_cmpsx_eq_f64

v_cmpsx_lg_f64 vcc, -1, v[2:3]
// ERROR: instruction not supported on this GPU (tonga): v_cmpsx_lg_f64

v_cmpsx_nge_f64 vcc, -1, v[2:3]
// ERROR: instruction not supported on this GPU (tonga): v_cmpsx_nge_f64

v_cmpsx_nle_f32 vcc, -1, v2
// ERROR: instruction not supported on this GPU (tonga): v_cmpsx_nle_f32

v_cmpsx_nlg_f32 vcc, -1, v2
// ERROR: instruction not supported on this GPU (tonga): v_cmpsx_nlg_f32

v_cmpsx_tru_f32 vcc, -1, v2
// ERROR: instruction not supported on this GPU (tonga): v_cmpsx_tru_f32

v_cvt_norm_u16_f16 v5, s101
// ERROR: instruction not supported on this GPU (tonga): v_cvt_norm_u16_f16

v_dot2_f32_f16 v0, -v1, -v2, -v3
// ERROR: instruction not supported on this GPU (tonga): v_dot2_f32_f16

v_dot8c_i32_i4 v0, v1, v2
// ERROR: instruction not supported on this GPU (tonga): v_dot8c_i32_i4

v_fmamk_f32 v255, v1, 0x1121, v3
// ERROR: instruction not supported on this GPU (tonga): v_fmamk_f32

v_lshr_b64 v[254:255], v[1:2], v2
// ERROR: instruction not supported on this GPU (tonga): v_lshr_b64

v_max3_f16 v0, src_lds_direct, v0, v0
// ERROR: instruction not supported on this GPU (tonga): v_max3_f16

v_med3_u16 v1, v2, v3, v4
// ERROR: instruction not supported on this GPU (tonga): v_med3_u16

v_mfma_f32_16x16x4f16 a[0:15], a[0:1], a[1:2], -2.0
// ERROR: instruction not supported on this GPU (tonga): v_mfma_f32_16x16x4f16

v_mfma_f32_32x32x2f32 a[0:15], a0, a1, -2.0
// ERROR: instruction not supported on this GPU (tonga): v_mfma_f32_32x32x2f32

v_mfma_f32_4x4x2bf16 a[0:3], a0, a1, -2.0
// ERROR: instruction not supported on this GPU (tonga): v_mfma_f32_4x4x2bf16

v_mfma_i32_16x16x4i8 a[0:15], a0, a1, 2
// ERROR: instruction not supported on this GPU (tonga): v_mfma_i32_16x16x4i8

v_min3_u16 v1, v2, v3, v4
// ERROR: instruction not supported on this GPU (tonga): v_min3_u16

v_permlanex16_b32 v0, lds_direct, s0, s0
// ERROR: instruction not supported on this GPU (tonga): v_permlanex16_b32

v_pk_add_f16 v0, execz, v0
// ERROR: instruction not supported on this GPU (tonga): v_pk_add_f16

v_pk_mad_u16 v255, v1, v2, v3
// ERROR: instruction not supported on this GPU (tonga): v_pk_mad_u16

v_pk_min_u16 v0, v1, v2
// ERROR: instruction not supported on this GPU (tonga): v_pk_min_u16

v_pk_sub_u16 v255, v1, v2
// ERROR: instruction not supported on this GPU (tonga): v_pk_sub_u16

v_sub_i16 v255, v1, v2
// ERROR: instruction not supported on this GPU (tonga): v_sub_i16

v_subb_co_u32 v1, vcc, v2, v3, vcc row_shl:1 row_mask:0xa bank_mask:0x1 bound_ctrl:0
// ERROR: instruction not supported on this GPU (tonga): v_subb_co_u32

v_subrev_i32 v1, s[0:1], v2, v3
// ERROR: instruction not supported on this GPU (tonga): v_subrev_i32

v_xad_u32 v1, v2, v3, v4
// ERROR: instruction not supported on this GPU (tonga): v_xad_u32

// SOURCE: literal16-err.s

v_add_f16 v1, 0x1000ffff, v2
// ERROR: invalid operand for instruction

// SOURCE: literal16.s

v_add_f16_e32 v1, 3, v2
// ENCODING: [0x83, 0x04, 0x02, 0x3e] WAVE_SIZE: 64

// SOURCE: literals.s

buffer_atomic_add v0, off, s[0:3], src_shared_base offset:4095
// ERROR: src_shared_base register not available on this GPU

v_add_f64 v[0:1], v[0:1], lit(0x3ff000001)
// ERROR: literal value out of range

// SOURCE: mubuf-gfx9.s

buffer_load_format_d16_hi_x v5, off, s[8:11], s3 offset:4095
// ERROR: instruction not supported on this GPU (tonga): buffer_load_format_d16_hi_x

// SOURCE: out-of-range-registers.s

s_add_i32 s105, s0, s1
// ERROR: s105 register not available on this GPU

// SOURCE: reg-syntax-err.s

s_mov_b64 s[10:11], [s0
// ERROR: expected a comma or a closing square bracket

// SOURCE: smem-err.s

s_buffer_load_dword m0, s[0:3], s4
// ERROR: invalid operand for instruction

// SOURCE: smrd-err.s

s_load_dwordx4 s[100:103], s[2:3], s4
// ERROR: s[100:103] register not available on this GPU

// SOURCE: sop1-err.s

s_mov_b32 s103, 1
// ERROR: s103 register not available on this GPU

// SOURCE: sopc-err.s

s_cmp_eq_i32 0x12345678, 0x12345679
// ERROR: only one unique literal operand is allowed

// SOURCE: sopk-err.s

s_setreg_b32  typo(0x40), s2
// ERROR: expected a hwreg macro, structured immediate or an absolute expression

// SOURCE: vintrp-err.s

v_interp_mov_f32 v3, invalid_param_3, attr0.x
// ERROR: invalid interpolation slot

// SOURCE: vop3-modifiers-err.s

v_ceil_f32 v0, --1
// ERROR: invalid syntax, expected 'neg' modifier

// SOURCE: vop3-modifiers.s

v_add_f32_e32 v5, -16, v2
// ENCODING: [0xd0, 0x04, 0x0a, 0x02] WAVE_SIZE: 64

// SOURCE: vop_sdwa.s

v_add_f32_sdwa v0, -|v0|, -v0 dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:WORD_1 src1_sel:BYTE_2
// ENCODING: [0xf9, 0x00, 0x00, 0x02, 0x00, 0x06, 0x35, 0x12] WAVE_SIZE: 64

// SOURCE: xnack-mask.s

s_mov_b32 xnack_mask_hi, -1
// ERROR: xnack_mask_hi register not available on this GPU
