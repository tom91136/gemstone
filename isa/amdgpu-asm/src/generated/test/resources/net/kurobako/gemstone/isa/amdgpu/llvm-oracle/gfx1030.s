// Generated from pinned LLVM MC/AMDGPU checks. Regenerate with: sbt codegen
// TARGET: gfx1030

// SOURCE: flat-scratch-st-mode.s

scratch_load_dword v1, off, off offset:2047
// ENCODING: [0xff, 0x47, 0x30, 0xdc, 0x00, 0x00, 0x7f, 0x01] WAVE_SIZE: 32

scratch_load_dwordx2 v[1:2], off, off
// ENCODING: [0x00, 0x40, 0x34, 0xdc, 0x00, 0x00, 0x7f, 0x01] WAVE_SIZE: 32

scratch_load_dwordx3 v[1:3], off, off
// ENCODING: [0x00, 0x40, 0x3c, 0xdc, 0x00, 0x00, 0x7f, 0x01] WAVE_SIZE: 32

scratch_load_dwordx4 v[1:4], off, off
// ENCODING: [0x00, 0x40, 0x38, 0xdc, 0x00, 0x00, 0x7f, 0x01] WAVE_SIZE: 32

scratch_load_sbyte_d16 v1, off, off
// ENCODING: [0x00, 0x40, 0x88, 0xdc, 0x00, 0x00, 0x7f, 0x01] WAVE_SIZE: 32

scratch_load_sbyte_d16_hi v1, off, off
// ENCODING: [0x00, 0x40, 0x8c, 0xdc, 0x00, 0x00, 0x7f, 0x01] WAVE_SIZE: 32

scratch_load_short_d16 v1, off, off
// ENCODING: [0x00, 0x40, 0x90, 0xdc, 0x00, 0x00, 0x7f, 0x01] WAVE_SIZE: 32

scratch_load_short_d16_hi v1, off, off
// ENCODING: [0x00, 0x40, 0x94, 0xdc, 0x00, 0x00, 0x7f, 0x01] WAVE_SIZE: 32

scratch_load_ubyte v1, off, off
// ENCODING: [0x00, 0x40, 0x20, 0xdc, 0x00, 0x00, 0x7f, 0x01] WAVE_SIZE: 32

scratch_load_ubyte_d16 v1, off, off
// ENCODING: [0x00, 0x40, 0x80, 0xdc, 0x00, 0x00, 0x7f, 0x01] WAVE_SIZE: 32

scratch_load_ubyte_d16_hi v1, off, off
// ENCODING: [0x00, 0x40, 0x84, 0xdc, 0x00, 0x00, 0x7f, 0x01] WAVE_SIZE: 32

scratch_store_byte off, v2, off
// ENCODING: [0x00, 0x40, 0x60, 0xdc, 0x00, 0x02, 0x7f, 0x00] WAVE_SIZE: 32

scratch_store_byte_d16_hi off, v2, off
// ENCODING: [0x00, 0x40, 0x64, 0xdc, 0x00, 0x02, 0x7f, 0x00] WAVE_SIZE: 32

scratch_store_dword off, v2, off
// ENCODING: [0x00, 0x40, 0x70, 0xdc, 0x00, 0x02, 0x7f, 0x00] WAVE_SIZE: 32

scratch_store_dwordx2 off, v[2:3], off
// ENCODING: [0x00, 0x40, 0x74, 0xdc, 0x00, 0x02, 0x7f, 0x00] WAVE_SIZE: 32

scratch_store_dwordx3 off, v[2:4], off
// ENCODING: [0x00, 0x40, 0x7c, 0xdc, 0x00, 0x02, 0x7f, 0x00] WAVE_SIZE: 32

scratch_store_dwordx4 off, v[2:5], off
// ENCODING: [0x00, 0x40, 0x78, 0xdc, 0x00, 0x02, 0x7f, 0x00] WAVE_SIZE: 32

scratch_store_short off, v2, off
// ENCODING: [0x00, 0x40, 0x68, 0xdc, 0x00, 0x02, 0x7f, 0x00] WAVE_SIZE: 32

scratch_store_short_d16_hi off, v2, off
// ENCODING: [0x00, 0x40, 0x6c, 0xdc, 0x00, 0x02, 0x7f, 0x00] WAVE_SIZE: 32

// SOURCE: gfx1011_dlops.s

v_dot2_i32_i16 v0, v1, v2, v3
// ENCODING: [0x00, 0x40, 0x14, 0xcc, 0x01, 0x05, 0x0e, 0x1c] WAVE_SIZE: 32

v_dot2c_f32_f16_dpp v5, v1, v2 dpp8:[7,6,5,4,3,2,1,0]
// ENCODING: [0xe9, 0x04, 0x0a, 0x04, 0x01, 0x77, 0x39, 0x05] WAVE_SIZE: 32

v_dot2c_f32_f16_dpp v5, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0x0a, 0x04, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 32

v_dot4_u32_u8 v0, v1, v2, v3
// ENCODING: [0x00, 0x40, 0x17, 0xcc, 0x01, 0x05, 0x0e, 0x1c] WAVE_SIZE: 32

// SOURCE: gfx1030_err.s

ds_add_src2_u32 v1 offset:65535
// ERROR: instruction not supported on this GPU (gfx1030): ds_add_src2_u32

ds_and_src2_b32 v1 offset:65535
// ERROR: instruction not supported on this GPU (gfx1030): ds_and_src2_b32

ds_dec_src2_u64 v1 offset:65535
// ERROR: instruction not supported on this GPU (gfx1030): ds_dec_src2_u64

ds_inc_src2_u64 v1 offset:65535
// ERROR: instruction not supported on this GPU (gfx1030): ds_inc_src2_u64

ds_max_src2_f32 v1 offset:65535
// ERROR: instruction not supported on this GPU (gfx1030): ds_max_src2_f32

ds_min_src2_i32 v1 offset:65535
// ERROR: instruction not supported on this GPU (gfx1030): ds_min_src2_i32

ds_min_src2_u64 v1 offset:65535
// ERROR: instruction not supported on this GPU (gfx1030): ds_min_src2_u64

ds_sub_src2_u64 v1 offset:65535
// ERROR: instruction not supported on this GPU (gfx1030): ds_sub_src2_u64

s_get_waveid_in_workgroup s0
// ERROR: instruction not supported on this GPU (gfx1030): s_get_waveid_in_workgroup

s_waitcnt_depctr depctr_va_vdst(-1)
// ERROR: invalid value for depctr_va_vdst

// SOURCE: gfx1030_new.s

buffer_atomic_csub v5, off, s[8:11], -1 offset:4095 glc
// ENCODING: [0xff, 0x4f, 0xd0, 0xe0, 0x00, 0x05, 0x02, 0xc1] WAVE_SIZE: 32

buffer_atomic_csub v5, off, s[8:11], s3
// ENCODING: [0x00, 0x00, 0xd0, 0xe0, 0x00, 0x05, 0x02, 0x03] WAVE_SIZE: 32

global_atomic_csub v0, v2, s[2:3]
// ENCODING: [0x00, 0x80, 0xd0, 0xdc, 0x00, 0x02, 0x02, 0x00] WAVE_SIZE: 32

global_atomic_csub v2, v[0:1], v2, off glc
// ENCODING: [0x00, 0x80, 0xd1, 0xdc, 0x00, 0x02, 0x7d, 0x02] WAVE_SIZE: 32

global_load_dword_addtid v1, off offset:16
// ENCODING: [0x10, 0x80, 0x58, 0xdc, 0x00, 0x00, 0x7d, 0x01] WAVE_SIZE: 32

global_store_dword v254, v1, s[2:3] offset:16
// ENCODING: [0x10, 0x80, 0x70, 0xdc, 0xfe, 0x01, 0x02, 0x00] WAVE_SIZE: 32

global_store_dword_addtid v1, off offset:16 glc slc dlc
// ENCODING: [0x10, 0x90, 0x5f, 0xdc, 0x00, 0x01, 0x7d, 0x00] WAVE_SIZE: 32

image_bvh64_intersect_ray v[39:42], [v50, v46, v23, v17, v16, v15, v21, v20, v19, v37, v40, v42], s[12:15]
// ENCODING: [0x07, 0x9f, 0x9c, 0xf1, 0x32, 0x27, 0x03, 0x00, 0x2e, 0x17, 0x11, 0x10, 0x0f, 0x15, 0x14, 0x13, 0x25, 0x28, 0x2a, 0x00] WAVE_SIZE: 32

image_bvh64_intersect_ray v[39:42], [v50, v46, v23, v17, v16, v15, v21, v20, v19], s[12:15] a16
// ENCODING: [0x05, 0x9f, 0x9c, 0xf1, 0x32, 0x27, 0x03, 0x40, 0x2e, 0x17, 0x11, 0x10, 0x0f, 0x15, 0x14, 0x13] WAVE_SIZE: 32

image_msaa_load v14, [v204, v11, v14, v19], s[40:47] dmask:0x1 dim:SQ_RSRC_IMG_2D_MSAA_ARRAY
// ENCODING: [0x3b, 0x01, 0x00, 0xf0, 0xcc, 0x0e, 0x0a, 0x00, 0x0b, 0x0e, 0x13, 0x00] WAVE_SIZE: 32

s_waitcnt_depctr depctr_hold_cnt(0) depctr_sa_sdst(0) depctr_va_vdst(8) depctr_va_sdst(0) depctr_va_ssrc(0) depctr_va_vcc(0) depctr_vm_vsrc(0)
// ENCODING: [0x00, 0x80, 0xa3, 0xbf] WAVE_SIZE: 32

v_fma_legacy_f32 v0, s1, 2.0, -v3
// ENCODING: [0x00, 0x00, 0x40, 0xd5, 0x01, 0xe8, 0x0d, 0x84] WAVE_SIZE: 32

v_fmac_legacy_f32_e32 v0, v1, v2
// ENCODING: [0x01, 0x05, 0x00, 0x0c] WAVE_SIZE: 32

v_fmac_legacy_f32_e64 v0, s1, 2.0
// ENCODING: [0x00, 0x00, 0x06, 0xd5, 0x01, 0xe8, 0x01, 0x02] WAVE_SIZE: 32

// SOURCE: gfx1030_unsupported.s

v_fmac_legacy_f32_dpp v255, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ERROR: dpp variant of this instruction is not supported

v_fmac_legacy_f32_sdwa v255, v1, v2 dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:DWORD src1_sel:DWORD
// ERROR: sdwa variant of this instruction is not supported

v_mul_lo_i32 v0, v1, v2
// ERROR: instruction not supported on this GPU (gfx1030): v_mul_lo_i32

// SOURCE: gfx11_asm_operands.s

s_add_i32 s0, src_execz, s2
// ENCODING: [0xfc, 0x02, 0x00, 0x81] WAVE_SIZE: 32

s_add_i32 s0, src_pops_exiting_wave_id, s1
// ENCODING: [0xef, 0x01, 0x00, 0x81] WAVE_SIZE: 32

s_add_i32 s0, src_vccz, s2
// ENCODING: [0xfb, 0x02, 0x00, 0x81] WAVE_SIZE: 32

s_cbranch_vccz 256
// ENCODING: [0x00, 0x01, 0x86, 0xbf] WAVE_SIZE: 32

v_readfirstlane_b32 s0, src_lds_direct
// ENCODING: [0xfe, 0x04, 0x00, 0x7e] WAVE_SIZE: 32

// SOURCE: mubuf-gfx950.s

buffer_load_dwordx3 off, s[8:11], s3 lds
// ERROR: invalid operand for instruction

// SOURCE: smem.s

s_atc_probe_buffer 0x1, s[8:11], 0x1FFFFF
// ERROR: expected a 20-bit unsigned offset

s_buffer_load_dword s10, s[92:95], m0
// ENCODING: [0xae, 0x02, 0x20, 0xf4, 0x00, 0x00, 0x00, 0xf8] WAVE_SIZE: 32

s_buffer_load_dword ttmp0, s[92:95], m0
// ENCODING: [0x2e, 0x1b, 0x20, 0xf4, 0x00, 0x00, 0x00, 0xf8] WAVE_SIZE: 32

// SOURCE: v_illegal-atomics.s

v_illegal
// ENCODING: [0x00, 0x00, 0x00, 0x00] WAVE_SIZE: 32
