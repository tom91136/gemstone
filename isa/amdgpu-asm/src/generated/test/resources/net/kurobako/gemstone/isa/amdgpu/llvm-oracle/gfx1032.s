// Generated from pinned LLVM MC/AMDGPU checks. Regenerate with: sbt codegen
// TARGET: gfx1032

// SOURCE: gfx1011_dlops.s

v_dot2_f32_f16 v0, v1, v2, v3
// ENCODING: [0x00, 0x40, 0x13, 0xcc, 0x01, 0x05, 0x0e, 0x1c] WAVE_SIZE: 32

v_dot2_i32_i16 v0, v1, v2, v3
// ENCODING: [0x00, 0x40, 0x14, 0xcc, 0x01, 0x05, 0x0e, 0x1c] WAVE_SIZE: 32

v_dot2c_f32_f16 v5, v1, v2
// ENCODING: [0x01, 0x05, 0x0a, 0x04] WAVE_SIZE: 32

v_dot2c_f32_f16_dpp v5, v1, v2 dpp8:[7,6,5,4,3,2,1,0]
// ENCODING: [0xe9, 0x04, 0x0a, 0x04, 0x01, 0x77, 0x39, 0x05] WAVE_SIZE: 32

v_dot2c_f32_f16_dpp v5, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0x0a, 0x04, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 32

v_dot4_u32_u8 v0, v1, v2, v3
// ENCODING: [0x00, 0x40, 0x17, 0xcc, 0x01, 0x05, 0x0e, 0x1c] WAVE_SIZE: 32

v_dot4c_i32_i8 v5, v1, v2
// ENCODING: [0x01, 0x05, 0x0a, 0x1a] WAVE_SIZE: 32

v_dot8_u32_u4 v0, v1, v2, v3
// ENCODING: [0x00, 0x40, 0x19, 0xcc, 0x01, 0x05, 0x0e, 0x1c] WAVE_SIZE: 32

// SOURCE: gfx1030_err.s

ds_inc_src2_u32 v1 offset:65535
// ERROR: instruction not supported on this GPU (gfx1032): ds_inc_src2_u32

ds_inc_src2_u64 v1 offset:65535
// ERROR: instruction not supported on this GPU (gfx1032): ds_inc_src2_u64

ds_min_src2_f32 v1 offset:65535
// ERROR: instruction not supported on this GPU (gfx1032): ds_min_src2_f32

ds_or_src2_b64 v1 offset:65535
// ERROR: instruction not supported on this GPU (gfx1032): ds_or_src2_b64

ds_xor_src2_b32 v1 offset:65535
// ERROR: instruction not supported on this GPU (gfx1032): ds_xor_src2_b32

s_getreg_b32 s2, hwreg(HW_REG_XNACK_MASK)
// ERROR: invalid hardware register: not supported on this GPU

s_waitcnt_depctr depctr_va_vdst(-1)
// ERROR: invalid value for depctr_va_vdst

v_dot8c_i32_i4 v5, v1, v2
// ERROR: instruction not supported on this GPU (gfx1032): v_dot8c_i32_i4

// SOURCE: gfx1030_new.s

buffer_atomic_csub v5, off, s[8:11], -1 offset:4095 glc
// ENCODING: [0xff, 0x4f, 0xd0, 0xe0, 0x00, 0x05, 0x02, 0xc1] WAVE_SIZE: 32

buffer_atomic_csub v5, off, s[8:11], s3
// ENCODING: [0x00, 0x00, 0xd0, 0xe0, 0x00, 0x05, 0x02, 0x03] WAVE_SIZE: 32

global_atomic_csub v0, v2, s[2:3]
// ENCODING: [0x00, 0x80, 0xd0, 0xdc, 0x00, 0x02, 0x02, 0x00] WAVE_SIZE: 32

global_atomic_csub v2, v[0:1], v2, off glc
// ENCODING: [0x00, 0x80, 0xd1, 0xdc, 0x00, 0x02, 0x7d, 0x02] WAVE_SIZE: 32

global_store_dword v254, v1, s[2:3] offset:16
// ENCODING: [0x10, 0x80, 0x70, 0xdc, 0xfe, 0x01, 0x02, 0x00] WAVE_SIZE: 32

image_bvh64_intersect_ray v[39:42], [v50, v46, v23, v17, v16, v15, v21, v20, v19, v37, v40, v42], s[12:15]
// ENCODING: [0x07, 0x9f, 0x9c, 0xf1, 0x32, 0x27, 0x03, 0x00, 0x2e, 0x17, 0x11, 0x10, 0x0f, 0x15, 0x14, 0x13, 0x25, 0x28, 0x2a, 0x00] WAVE_SIZE: 32

image_bvh64_intersect_ray v[39:42], [v50, v46, v23, v17, v16, v15, v21, v20, v19], s[12:15] a16
// ENCODING: [0x05, 0x9f, 0x9c, 0xf1, 0x32, 0x27, 0x03, 0x40, 0x2e, 0x17, 0x11, 0x10, 0x0f, 0x15, 0x14, 0x13] WAVE_SIZE: 32

image_bvh_intersect_ray v[39:42], [v50, v46, v23, v17, v16, v15, v21, v20], s[12:15] a16
// ENCODING: [0x05, 0x9f, 0x98, 0xf1, 0x32, 0x27, 0x03, 0x40, 0x2e, 0x17, 0x11, 0x10, 0x0f, 0x15, 0x14, 0x00] WAVE_SIZE: 32

image_msaa_load v14, [v204, v11, v14, v19], s[40:47] dmask:0x1 dim:SQ_RSRC_IMG_2D_MSAA_ARRAY
// ENCODING: [0x3b, 0x01, 0x00, 0xf0, 0xcc, 0x0e, 0x0a, 0x00, 0x0b, 0x0e, 0x13, 0x00] WAVE_SIZE: 32

s_getreg_b32 s2, hwreg(HW_REG_SHADER_CYCLES, 0, 1)
// ENCODING: [0x1d, 0x00, 0x02, 0xb9] WAVE_SIZE: 32

s_waitcnt_depctr depctr_hold_cnt(0) depctr_sa_sdst(0) depctr_va_vdst(8) depctr_va_sdst(0) depctr_va_ssrc(0) depctr_va_vcc(0) depctr_vm_vsrc(0)
// ENCODING: [0x00, 0x80, 0xa3, 0xbf] WAVE_SIZE: 32

scratch_load_dword off, off offset:1024 lds
// ENCODING: [0x00, 0x64, 0x30, 0xdc, 0x00, 0x00, 0x7f, 0x00] WAVE_SIZE: 32

scratch_load_sbyte off, off offset:1024 lds
// ENCODING: [0x00, 0x64, 0x24, 0xdc, 0x00, 0x00, 0x7f, 0x00] WAVE_SIZE: 32

scratch_load_ubyte off, off offset:1024 lds
// ENCODING: [0x00, 0x64, 0x20, 0xdc, 0x00, 0x00, 0x7f, 0x00] WAVE_SIZE: 32

v_fma_legacy_f32 v0, s1, 2.0, -v3
// ENCODING: [0x00, 0x00, 0x40, 0xd5, 0x01, 0xe8, 0x0d, 0x84] WAVE_SIZE: 32

v_fmac_legacy_f32_e32 v0, v1, v2
// ENCODING: [0x01, 0x05, 0x00, 0x0c] WAVE_SIZE: 32

v_fmac_legacy_f32_e64 v0, s1, 2.0
// ENCODING: [0x00, 0x00, 0x06, 0xd5, 0x01, 0xe8, 0x01, 0x02] WAVE_SIZE: 32
