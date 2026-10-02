// Generated from pinned LLVM MC/AMDGPU checks. Regenerate with: sbt codegen
// TARGET: gfx942

// SOURCE: flat-scratch-gfx942.s

scratch_load_dword a2, off, s6
// ENCODING: [0x00, 0x40, 0x50, 0xdc, 0x00, 0x00, 0x86, 0x02] WAVE_SIZE: 64

scratch_load_dwordx2 a[2:3], off, off
// ENCODING: [0x00, 0x40, 0x54, 0xdc, 0x00, 0x00, 0xff, 0x02] WAVE_SIZE: 64

scratch_load_dwordx3 a[2:4], off, off
// ENCODING: [0x00, 0x40, 0x58, 0xdc, 0x00, 0x00, 0xff, 0x02] WAVE_SIZE: 64

scratch_load_dwordx4 a[2:5], off, off
// ENCODING: [0x00, 0x40, 0x5c, 0xdc, 0x00, 0x00, 0xff, 0x02] WAVE_SIZE: 64

scratch_load_sbyte a2, off, off
// ENCODING: [0x00, 0x40, 0x44, 0xdc, 0x00, 0x00, 0xff, 0x02] WAVE_SIZE: 64

scratch_load_sbyte_d16 a2, off, off
// ENCODING: [0x00, 0x40, 0x88, 0xdc, 0x00, 0x00, 0xff, 0x02] WAVE_SIZE: 64

scratch_load_sbyte_d16_hi a2, off, off
// ENCODING: [0x00, 0x40, 0x8c, 0xdc, 0x00, 0x00, 0xff, 0x02] WAVE_SIZE: 64

scratch_load_short_d16 a2, off, off
// ENCODING: [0x00, 0x40, 0x90, 0xdc, 0x00, 0x00, 0xff, 0x02] WAVE_SIZE: 64

scratch_load_short_d16_hi a2, off, off
// ENCODING: [0x00, 0x40, 0x94, 0xdc, 0x00, 0x00, 0xff, 0x02] WAVE_SIZE: 64

scratch_load_sshort a2, off, off
// ENCODING: [0x00, 0x40, 0x4c, 0xdc, 0x00, 0x00, 0xff, 0x02] WAVE_SIZE: 64

scratch_load_ubyte a2, off, off
// ENCODING: [0x00, 0x40, 0x40, 0xdc, 0x00, 0x00, 0xff, 0x02] WAVE_SIZE: 64

scratch_load_ubyte_d16 a2, off, off
// ENCODING: [0x00, 0x40, 0x80, 0xdc, 0x00, 0x00, 0xff, 0x02] WAVE_SIZE: 64

scratch_load_ubyte_d16_hi a2, off, off
// ENCODING: [0x00, 0x40, 0x84, 0xdc, 0x00, 0x00, 0xff, 0x02] WAVE_SIZE: 64

scratch_load_ushort a2, off, off
// ENCODING: [0x00, 0x40, 0x48, 0xdc, 0x00, 0x00, 0xff, 0x02] WAVE_SIZE: 64

scratch_store_byte off, a2, off
// ENCODING: [0x00, 0x40, 0x60, 0xdc, 0x00, 0x02, 0xff, 0x00] WAVE_SIZE: 64

scratch_store_byte_d16_hi off, a2, off
// ENCODING: [0x00, 0x40, 0x64, 0xdc, 0x00, 0x02, 0xff, 0x00] WAVE_SIZE: 64

scratch_store_dword off, a2, off
// ENCODING: [0x00, 0x40, 0x70, 0xdc, 0x00, 0x02, 0xff, 0x00] WAVE_SIZE: 64

scratch_store_dwordx2 off, a[2:3], off
// ENCODING: [0x00, 0x40, 0x74, 0xdc, 0x00, 0x02, 0xff, 0x00] WAVE_SIZE: 64

scratch_store_dwordx3 off, a[2:4], off
// ENCODING: [0x00, 0x40, 0x78, 0xdc, 0x00, 0x02, 0xff, 0x00] WAVE_SIZE: 64

scratch_store_dwordx4 off, a[2:5], off
// ENCODING: [0x00, 0x40, 0x7c, 0xdc, 0x00, 0x02, 0xff, 0x00] WAVE_SIZE: 64

scratch_store_short v4, v2, off
// ENCODING: [0x00, 0x60, 0x68, 0xdc, 0x04, 0x02, 0x7f, 0x00] WAVE_SIZE: 64

scratch_store_short_d16_hi off, a2, off
// ENCODING: [0x00, 0x40, 0x6c, 0xdc, 0x00, 0x02, 0xff, 0x00] WAVE_SIZE: 64

// SOURCE: gfx942_asm_features.s

buffer_atomic_add_f32 v4, off, s[8:11], s3 sc1
// ENCODING: [0x00, 0x80, 0x34, 0xe1, 0x00, 0x04, 0x02, 0x03] WAVE_SIZE: 64

buffer_atomic_add_f64 v[4:5], off, s[8:11], s3 sc1
// ENCODING: [0x00, 0x80, 0x3c, 0xe1, 0x00, 0x04, 0x02, 0x03] WAVE_SIZE: 64

buffer_atomic_max_f64 v[4:5], off, s[8:11], s3 sc1
// ENCODING: [0x00, 0x80, 0x44, 0xe1, 0x00, 0x04, 0x02, 0x03] WAVE_SIZE: 64

buffer_atomic_min_f64 v[4:5], off, s[8:11], s3 sc1
// ENCODING: [0x00, 0x80, 0x40, 0xe1, 0x00, 0x04, 0x02, 0x03] WAVE_SIZE: 64

buffer_atomic_pk_add_f16 v4, off, s[8:11], s3 sc1
// ENCODING: [0x00, 0x80, 0x38, 0xe1, 0x00, 0x04, 0x02, 0x03] WAVE_SIZE: 64

buffer_atomic_swap v5, off, s[8:11], s3 sc0
// ENCODING: [0x00, 0x40, 0x00, 0xe1, 0x00, 0x05, 0x02, 0x03] WAVE_SIZE: 64

buffer_inv sc0
// ENCODING: [0x00, 0x40, 0xa4, 0xe0, 0x00, 0x00, 0x00, 0x00] WAVE_SIZE: 64

buffer_load_dword v5, off, s[8:11], s3 sc0 nt sc1
// ENCODING: [0x00, 0xc0, 0x52, 0xe0, 0x00, 0x05, 0x02, 0x03] WAVE_SIZE: 64

buffer_wbl2 sc1
// ENCODING: [0x00, 0x80, 0xa0, 0xe0, 0x00, 0x00, 0x00, 0x00] WAVE_SIZE: 64

ds_pk_add_bf16 v2, v1
// ENCODING: [0x00, 0x00, 0x30, 0xd8, 0x02, 0x01, 0x00, 0x00] WAVE_SIZE: 64

ds_pk_add_f16 v2, v1
// ENCODING: [0x00, 0x00, 0x2e, 0xd8, 0x02, 0x01, 0x00, 0x00] WAVE_SIZE: 64

ds_pk_add_rtn_bf16 v3, v2, v1
// ENCODING: [0x00, 0x00, 0x70, 0xd9, 0x02, 0x01, 0x00, 0x03] WAVE_SIZE: 64

ds_pk_add_rtn_f16 v3, v2, v1
// ENCODING: [0x00, 0x00, 0x6e, 0xd9, 0x02, 0x01, 0x00, 0x03] WAVE_SIZE: 64

flat_atomic_add_f32 v[2:3], v1
// ENCODING: [0x00, 0x00, 0x34, 0xdd, 0x02, 0x01, 0x00, 0x00] WAVE_SIZE: 64

flat_atomic_add_f64 v[0:1], v[0:1], v[2:3] sc0 sc1
// ENCODING: [0x00, 0x00, 0x3d, 0xdf, 0x00, 0x02, 0x00, 0x00] WAVE_SIZE: 64

flat_atomic_max_f64 v[0:1], v[2:3] sc1
// ENCODING: [0x00, 0x00, 0x44, 0xdf, 0x00, 0x02, 0x00, 0x00] WAVE_SIZE: 64

flat_atomic_min_f64 v[0:1], v[2:3] sc1
// ENCODING: [0x00, 0x00, 0x40, 0xdf, 0x00, 0x02, 0x00, 0x00] WAVE_SIZE: 64

flat_atomic_pk_add_bf16 v4, v[2:3], v1 sc0
// ENCODING: [0x00, 0x00, 0x49, 0xdd, 0x02, 0x01, 0x00, 0x04] WAVE_SIZE: 64

flat_atomic_pk_add_f16 v4, v[2:3], v1 sc0
// ENCODING: [0x00, 0x00, 0x39, 0xdd, 0x02, 0x01, 0x00, 0x04] WAVE_SIZE: 64

global_atomic_add_f32 v0, v[0:1], v2, off sc0 sc1
// ENCODING: [0x00, 0x80, 0x35, 0xdf, 0x00, 0x02, 0x7f, 0x00] WAVE_SIZE: 64

global_atomic_add_f64 v[0:1], v[2:3], off sc1
// ENCODING: [0x00, 0x80, 0x3c, 0xdf, 0x00, 0x02, 0x7f, 0x00] WAVE_SIZE: 64

global_atomic_max_f64 v[0:1], v[2:3], off sc1
// ENCODING: [0x00, 0x80, 0x44, 0xdf, 0x00, 0x02, 0x7f, 0x00] WAVE_SIZE: 64

global_atomic_min_f64 v[0:1], v[2:3], off sc1
// ENCODING: [0x00, 0x80, 0x40, 0xdf, 0x00, 0x02, 0x7f, 0x00] WAVE_SIZE: 64

global_atomic_pk_add_bf16 v4, v[2:3], v1, off sc0
// ENCODING: [0x00, 0x80, 0x49, 0xdd, 0x02, 0x01, 0x7f, 0x04] WAVE_SIZE: 64

global_atomic_pk_add_f16 v0, v[0:1], v2, off sc0 sc1
// ENCODING: [0x00, 0x80, 0x39, 0xdf, 0x00, 0x02, 0x7f, 0x00] WAVE_SIZE: 64

global_load_dword v2, v[2:3], off
// ENCODING: [0x00, 0x80, 0x50, 0xdc, 0x02, 0x00, 0x7f, 0x02] WAVE_SIZE: 64

global_load_lds_dword v[2:3], off
// ENCODING: [0x00, 0x80, 0xa8, 0xdc, 0x02, 0x00, 0x7f, 0x00] WAVE_SIZE: 64

global_load_lds_dword v[2:3], off offset:4
// ENCODING: [0x04, 0x80, 0xa8, 0xdc, 0x02, 0x00, 0x7f, 0x00] WAVE_SIZE: 64

global_load_lds_sbyte v[2:3], off
// ENCODING: [0x00, 0x80, 0x9c, 0xdc, 0x02, 0x00, 0x7f, 0x00] WAVE_SIZE: 64

global_load_lds_sshort v[2:3], off
// ENCODING: [0x00, 0x80, 0xa4, 0xdc, 0x02, 0x00, 0x7f, 0x00] WAVE_SIZE: 64

global_load_lds_ubyte v[2:3], off
// ENCODING: [0x00, 0x80, 0x98, 0xdc, 0x02, 0x00, 0x7f, 0x00] WAVE_SIZE: 64

global_load_lds_ushort v[2:3], off
// ENCODING: [0x00, 0x80, 0xa0, 0xdc, 0x02, 0x00, 0x7f, 0x00] WAVE_SIZE: 64

s_getreg_b32 s1, hwreg(HW_REG_XCC_ID)
// ENCODING: [0x14, 0xf8, 0x81, 0xb8] WAVE_SIZE: 64

s_load_dword s2, s[2:3], 0x0 glc
// ENCODING: [0x81, 0x00, 0x03, 0xc0, 0x00, 0x00, 0x00, 0x00] WAVE_SIZE: 64

scratch_load_lds_dword v2, off
// ENCODING: [0x00, 0x60, 0xa8, 0xdc, 0x02, 0x00, 0x7f, 0x00] WAVE_SIZE: 64

scratch_load_lds_sbyte v2, off
// ENCODING: [0x00, 0x60, 0x9c, 0xdc, 0x02, 0x00, 0x7f, 0x00] WAVE_SIZE: 64

scratch_load_lds_sshort v2, off
// ENCODING: [0x00, 0x60, 0xa4, 0xdc, 0x02, 0x00, 0x7f, 0x00] WAVE_SIZE: 64

scratch_load_lds_ubyte v2, off
// ENCODING: [0x00, 0x60, 0x98, 0xdc, 0x02, 0x00, 0x7f, 0x00] WAVE_SIZE: 64

scratch_load_lds_ushort v2, off
// ENCODING: [0x00, 0x60, 0xa0, 0xdc, 0x02, 0x00, 0x7f, 0x00] WAVE_SIZE: 64

v_cvt_f32_bf8_dpp v1, v3 quad_perm:[0,2,1,1] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0xaa, 0x02, 0x7e, 0x03, 0x58, 0x00, 0xff] WAVE_SIZE: 64

v_cvt_f32_bf8_e32 v1, s3
// ENCODING: [0x03, 0xaa, 0x02, 0x7e] WAVE_SIZE: 64

v_cvt_f32_bf8_e64 v1, s3 clamp
// ENCODING: [0x01, 0x80, 0x95, 0xd1, 0x03, 0x00, 0x00, 0x00] WAVE_SIZE: 64

v_cvt_f32_bf8_e64 v1, s3 mul:2
// ENCODING: [0x01, 0x00, 0x95, 0xd1, 0x03, 0x00, 0x00, 0x08] WAVE_SIZE: 64

v_cvt_f32_bf8_sdwa v1, s3 src0_sel:BYTE_1
// ENCODING: [0xf9, 0xaa, 0x02, 0x7e, 0x03, 0x06, 0x81, 0x00] WAVE_SIZE: 64

v_cvt_f32_fp8_dpp v1, v3 quad_perm:[0,2,1,1] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0xa8, 0x02, 0x7e, 0x03, 0x58, 0x00, 0xff] WAVE_SIZE: 64

v_cvt_pk_bf8_f32 v1, v2, v3
// ENCODING: [0x01, 0x00, 0xa3, 0xd2, 0x02, 0x07, 0x02, 0x00] WAVE_SIZE: 64

v_cvt_pk_bf8_f32 v1, v2, v3 op_sel:[0,0,1]
// ENCODING: [0x01, 0x40, 0xa3, 0xd2, 0x02, 0x07, 0x02, 0x00] WAVE_SIZE: 64

v_cvt_pk_f32_bf8_dpp v[0:1], v3 row_newbcast:3 row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0xae, 0x00, 0x7e, 0x03, 0x53, 0x01, 0xff] WAVE_SIZE: 64

v_cvt_pk_f32_fp8_dpp v[0:1], v3 row_newbcast:3 row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0xac, 0x00, 0x7e, 0x03, 0x53, 0x01, 0xff] WAVE_SIZE: 64

v_cvt_pk_fp8_f32 v1, v2, v3
// ENCODING: [0x01, 0x00, 0xa2, 0xd2, 0x02, 0x07, 0x02, 0x00] WAVE_SIZE: 64

v_cvt_sr_bf8_f32 v1, -|s2|, v3
// ENCODING: [0x01, 0x01, 0xa5, 0xd2, 0x02, 0x06, 0x02, 0x20] WAVE_SIZE: 64

v_cvt_sr_bf8_f32 v1, v2, v3
// ENCODING: [0x01, 0x00, 0xa5, 0xd2, 0x02, 0x07, 0x02, 0x00] WAVE_SIZE: 64

v_cvt_sr_fp8_f32 v1, v2, v3
// ENCODING: [0x01, 0x00, 0xa4, 0xd2, 0x02, 0x07, 0x02, 0x00] WAVE_SIZE: 64

v_fmaak_f32 v0, v2, v3, 0x42c80000
// ENCODING: [0x02, 0x07, 0x00, 0x30, 0x00, 0x00, 0xc8, 0x42] WAVE_SIZE: 64

v_fmac_f64_e32 v[4:5], v[2:3], v[4:5]
// ENCODING: [0x02, 0x09, 0x08, 0x08] WAVE_SIZE: 64

v_fmac_f64_e32 v[4:5], vcc, v[4:5]
// ENCODING: [0x6a, 0x08, 0x08, 0x08] WAVE_SIZE: 64

v_fmamk_f32 v0, v2, 0x42c80000, v3
// ENCODING: [0x02, 0x07, 0x00, 0x2e, 0x00, 0x00, 0xc8, 0x42] WAVE_SIZE: 64

v_lshl_add_u64 v[2:3], s[4:5], v7, v[8:9]
// ENCODING: [0x02, 0x00, 0x08, 0xd2, 0x04, 0x0e, 0x22, 0x04] WAVE_SIZE: 64

v_mov_b64_dpp v[2:3], v[4:5] row_newbcast:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x70, 0x04, 0x7e, 0x04, 0x51, 0x01, 0xff] WAVE_SIZE: 64

// SOURCE: gfx942_err.s

buffer_invl2
// ERROR: instruction not supported on this GPU (gfx942): buffer_invl2

exp pos0 v3, v2, v1, v0
// ERROR: instruction not supported on this GPU (gfx942): exp

v_mac_f32_sdwa v5, v1, v2 dst_sel:DWORD dst_unused:UNUSED_PRESERVE src0_sel:DWORD src1_sel:DWORD
// ERROR: instruction not supported on this GPU (gfx942): v_mac_f32

v_mov_b64 v[2:3], v[4:5] row_shl:1
// ERROR: DP ALU dpp only supports row_newbcast

// SOURCE: gfx942_err_pos.s

global_atomic_or v[0:1], v2, off sc0 sc1 nt
// ERROR: instruction must not use sc0

// SOURCE: gfx942_unsupported.s

buffer_wbinvl1
// ERROR: instruction not supported on this GPU (gfx942): buffer_wbinvl1

buffer_wbinvl1_vol
// ERROR: instruction not supported on this GPU (gfx942): buffer_wbinvl1_vol

// SOURCE: gfx950_asm_features.s

buffer_atomic_pk_add_bf16 v5, off, s[96:99], s3 offset:4095
// ERROR: instruction not supported on this GPU (gfx942): buffer_atomic_pk_add_bf16

v_cvt_scalef32_2xpk16_fp6_f32 v[20:25], v[10:25], v[10:25], 22
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_scalef32_2xpk16_fp6_f32

v_cvt_scalef32_f32_fp8 v1, 11, v3
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_scalef32_f32_fp8

v_cvt_scalef32_pk32_bf6_bf16 v[20:25], v[10:25], v8
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_scalef32_pk32_bf6_bf16

v_cvt_scalef32_pk32_bf6_f16 v[20:25], v[10:25], v8
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_scalef32_pk32_bf6_f16

v_cvt_scalef32_pk32_f32_fp6 v[2:33], v[2:7], v6
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_scalef32_pk32_f32_fp6

v_cvt_scalef32_pk_bf16_fp4 v1, s2, 3
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_scalef32_pk_bf16_fp4

v_cvt_scalef32_pk_f16_bf8 v1, s2, 3
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_scalef32_pk_f16_bf8

v_cvt_scalef32_pk_f32_bf8 v[2:3], s2, 3
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_scalef32_pk_f32_bf8

v_cvt_scalef32_pk_fp4_bf16 v1, -|s2|, v3
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_scalef32_pk_fp4_bf16

v_cvt_scalef32_pk_fp8_bf16 v1, -v2, |v3|
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_scalef32_pk_fp8_bf16

v_cvt_scalef32_sr_bf8_f16 v0, -v1, v2, v3
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_scalef32_sr_bf8_f16

v_cvt_scalef32_sr_fp8_bf16 v0, -v1, v2, v3
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_scalef32_sr_fp8_bf16

v_cvt_scalef32_sr_pk32_bf6_f16 v[0:5], v[6:21], v22, v23
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_scalef32_sr_pk32_bf6_f16

v_cvt_scalef32_sr_pk32_fp6_f32 v[0:5], v[6:37], v38, v39
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_scalef32_sr_pk32_fp6_f32

v_cvt_scalef32_sr_pk_fp4_bf16 v0, -v2, v4, v5
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_scalef32_sr_pk_fp4_bf16

v_cvt_sr_bf16_f32 v0, -v1, v2
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_sr_bf16_f32

v_minimum3_f32 v0, v1, v2, v3
// ERROR: instruction not supported on this GPU (gfx942): v_minimum3_f32

// SOURCE: gfx950_asm_read_tr.s

ds_read_b64_tr_b16 v[2:3], v3 offset:64
// ERROR: instruction not supported on this GPU (gfx942): ds_read_b64_tr_b16

ds_read_b64_tr_b4 v[0:1], v1
// ERROR: instruction not supported on this GPU (gfx942): ds_read_b64_tr_b4

ds_read_b64_tr_b8 v[0:1], v1
// ERROR: instruction not supported on this GPU (gfx942): ds_read_b64_tr_b8

// SOURCE: gfx950_asm_vop1.s

v_cvt_f32_bf16 v5, -1
// ERROR: instruction not supported on this GPU (gfx942): v_cvt_f32_bf16

// SOURCE: gfx950_asm_vop3.s

v_ashr_pk_i8_i32 v1, v2, v3, v4 op_sel:[0,0,0,1]
// ERROR: instruction not supported on this GPU (gfx942): v_ashr_pk_i8_i32

v_bitop3_b16 v5, v1, v2, s3
// ERROR: instruction not supported on this GPU (gfx942): v_bitop3_b16

// SOURCE: gfx9_asm_vop2_features.s

v_pk_fmac_f16_dpp v5, v1, v2 quad_perm:[0,1,2,3] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x04, 0x0a, 0x78, 0x01, 0xe4, 0x00, 0xff] WAVE_SIZE: 64

// SOURCE: mai-err-gfx942.s

v_mfma_f32_32x32x1_2b_f32 a[0:31], v0, v1, a[0:31] neg:[1,0,0]
// ERROR: invalid modifier: neg is not supported

v_mfma_f32_32x32x4bf16 a[0:15], v0, v1, 0
// ERROR: operands are not valid for this GPU or mode

// SOURCE: mai-gfx942.s

v_accvgpr_write_b32 a10, s20
// ENCODING: [0x0a, 0x40, 0xd9, 0xd3, 0x14, 0x00, 0x00, 0x18] WAVE_SIZE: 64

v_mfma_f32_16x16x16_bf16 v[0:3], v[2:3], v[4:5], v[2:5]
// ENCODING: [0x00, 0x00, 0xe1, 0xd3, 0x02, 0x09, 0x0a, 0x04] WAVE_SIZE: 64

v_mfma_f32_16x16x16_f16 a[0:3], v[0:1], v[2:3], a[2:5]
// ENCODING: [0x00, 0x80, 0xcd, 0xd3, 0x00, 0x05, 0x0a, 0x04] WAVE_SIZE: 64

v_mfma_f32_16x16x1_4b_f32 a[0:15], v0, v1, a[18:33]
// ENCODING: [0x00, 0x80, 0xc1, 0xd3, 0x00, 0x03, 0x4a, 0x04] WAVE_SIZE: 64

v_mfma_f32_16x16x32_bf8_bf8 v[0:3], v[2:3], v[4:5], v[0:3]
// ENCODING: [0x00, 0x00, 0xf0, 0xd3, 0x02, 0x09, 0x02, 0x04] WAVE_SIZE: 64

v_mfma_f32_16x16x32_bf8_fp8 v[0:3], v[2:3], v[4:5], v[0:3]
// ENCODING: [0x00, 0x00, 0xf1, 0xd3, 0x02, 0x09, 0x02, 0x04] WAVE_SIZE: 64

v_mfma_f32_16x16x32_fp8_bf8 v[0:3], v[2:3], v[4:5], v[0:3]
// ENCODING: [0x00, 0x00, 0xf2, 0xd3, 0x02, 0x09, 0x02, 0x04] WAVE_SIZE: 64

v_mfma_f32_16x16x32_fp8_fp8 v[0:3], v[2:3], v[4:5], v[0:3]
// ENCODING: [0x00, 0x00, 0xf3, 0xd3, 0x02, 0x09, 0x02, 0x04] WAVE_SIZE: 64

v_mfma_f32_16x16x4_4b_bf16 v[0:15], v[2:3], v[4:5], v[18:33]
// ENCODING: [0x00, 0x00, 0xde, 0xd3, 0x02, 0x09, 0x4a, 0x04] WAVE_SIZE: 64

v_mfma_f32_16x16x4_4b_f16 a[0:15], v[0:1], v[2:3], a[18:33]
// ENCODING: [0x00, 0x80, 0xc9, 0xd3, 0x00, 0x05, 0x4a, 0x04] WAVE_SIZE: 64

v_mfma_f32_16x16x4_f32 a[0:3], v0, v1, a[2:5]
// ENCODING: [0x00, 0x80, 0xc5, 0xd3, 0x00, 0x03, 0x0a, 0x04] WAVE_SIZE: 64

v_mfma_f32_16x16x8_xf32 a[0:3], v[2:3], v[4:5], a[2:5]
// ENCODING: [0x00, 0x80, 0xbe, 0xd3, 0x02, 0x09, 0x0a, 0x04] WAVE_SIZE: 64

v_mfma_f32_32x32x16_bf8_bf8 v[0:15], v[2:3], v[4:5], v[0:15]
// ENCODING: [0x00, 0x00, 0xf4, 0xd3, 0x02, 0x09, 0x02, 0x04] WAVE_SIZE: 64

v_mfma_f32_32x32x16_bf8_fp8 v[0:15], v[2:3], v[4:5], v[0:15]
// ENCODING: [0x00, 0x00, 0xf5, 0xd3, 0x02, 0x09, 0x02, 0x04] WAVE_SIZE: 64

v_mfma_f32_32x32x16_fp8_bf8 v[0:15], v[2:3], v[4:5], v[0:15]
// ENCODING: [0x00, 0x00, 0xf6, 0xd3, 0x02, 0x09, 0x02, 0x04] WAVE_SIZE: 64

v_mfma_f32_32x32x16_fp8_fp8 v[0:15], v[2:3], v[4:5], v[0:15]
// ENCODING: [0x00, 0x00, 0xf7, 0xd3, 0x02, 0x09, 0x02, 0x04] WAVE_SIZE: 64

v_mfma_f32_32x32x1_2b_f32 a[0:31], v0, v1, a[34:65] blgp:7
// ENCODING: [0x00, 0x80, 0xc0, 0xd3, 0x00, 0x03, 0x8a, 0xe4] WAVE_SIZE: 64

v_mfma_f32_32x32x2_f32 a[0:15], v0, v1, a[18:33]
// ENCODING: [0x00, 0x80, 0xc4, 0xd3, 0x00, 0x03, 0x4a, 0x04] WAVE_SIZE: 64

v_mfma_f32_32x32x4_2b_bf16 v[0:31], v[2:3], v[4:5], v[34:65]
// ENCODING: [0x00, 0x00, 0xdd, 0xd3, 0x02, 0x09, 0x8a, 0x04] WAVE_SIZE: 64

v_mfma_f32_32x32x4_2b_f16 a[0:31], v[0:1], v[2:3], a[34:65]
// ENCODING: [0x00, 0x80, 0xc8, 0xd3, 0x00, 0x05, 0x8a, 0x04] WAVE_SIZE: 64

v_mfma_f32_32x32x4_xf32 v[0:15], v[2:3], v[4:5], v[18:33]
// ENCODING: [0x00, 0x00, 0xbf, 0xd3, 0x02, 0x09, 0x4a, 0x04] WAVE_SIZE: 64

v_mfma_f32_32x32x8_bf16 v[0:15], v[2:3], v[4:5], v[18:33]
// ENCODING: [0x00, 0x00, 0xe0, 0xd3, 0x02, 0x09, 0x4a, 0x04] WAVE_SIZE: 64

v_mfma_f32_32x32x8_f16 a[0:15], v[0:1], v[2:3], a[18:33]
// ENCODING: [0x00, 0x80, 0xcc, 0xd3, 0x00, 0x05, 0x4a, 0x04] WAVE_SIZE: 64

v_mfma_f32_4x4x1_16b_f32 v[0:3], v0, v1, v[2:5]
// ENCODING: [0x00, 0x00, 0xc2, 0xd3, 0x00, 0x03, 0x0a, 0x04] WAVE_SIZE: 64

v_mfma_f32_4x4x4_16b_bf16 v[0:3], v[2:3], v[4:5], v[2:5]
// ENCODING: [0x00, 0x00, 0xdf, 0xd3, 0x02, 0x09, 0x0a, 0x04] WAVE_SIZE: 64

v_mfma_f32_4x4x4_16b_f16 a[0:3], v[0:1], v[2:3], a[2:5]
// ENCODING: [0x00, 0x80, 0xca, 0xd3, 0x00, 0x05, 0x0a, 0x04] WAVE_SIZE: 64

v_mfma_f64_16x16x4_f64 a[0:7], v[0:1], v[2:3], a[0:7]
// ENCODING: [0x00, 0x80, 0xee, 0xd3, 0x00, 0x05, 0x02, 0x04] WAVE_SIZE: 64

v_mfma_f64_4x4x4_4b_f64 a[0:1], v[0:1], a[2:3], a[2:3]
// ENCODING: [0x00, 0x80, 0xef, 0xd3, 0x00, 0x05, 0x0a, 0x14] WAVE_SIZE: 64

v_mfma_i32_16x16x32_i8 v[0:3], v[2:3], v[4:5], v[0:3]
// ENCODING: [0x00, 0x00, 0xd7, 0xd3, 0x02, 0x09, 0x02, 0x04] WAVE_SIZE: 64

v_mfma_i32_16x16x4_4b_i8 a[0:15], v0, v1, a[18:33]
// ENCODING: [0x00, 0x80, 0xd1, 0xd3, 0x00, 0x03, 0x4a, 0x04] WAVE_SIZE: 64

v_mfma_i32_32x32x16_i8 v[0:15], v[2:3], v[4:5], v[0:15]
// ENCODING: [0x00, 0x00, 0xd6, 0xd3, 0x02, 0x09, 0x02, 0x04] WAVE_SIZE: 64

v_mfma_i32_32x32x4_2b_i8 a[0:31], v0, v1, a[34:65]
// ENCODING: [0x00, 0x80, 0xd0, 0xd3, 0x00, 0x03, 0x8a, 0x04] WAVE_SIZE: 64

v_mfma_i32_4x4x4_16b_i8 a[0:3], v0, v1, a[2:5]
// ENCODING: [0x00, 0x80, 0xd2, 0xd3, 0x00, 0x03, 0x0a, 0x04] WAVE_SIZE: 64

v_smfmac_f32_16x16x32_bf16 v[10:13], a[2:3], v[4:7], v4 cbsz:3 abid:1
// ENCODING: [0x0a, 0x0b, 0xe6, 0xd3, 0x02, 0x09, 0x12, 0x0c] WAVE_SIZE: 64

v_smfmac_f32_16x16x32_f16 v[10:13], a[2:3], v[4:7], v0 cbsz:3 abid:1
// ENCODING: [0x0a, 0x0b, 0xe2, 0xd3, 0x02, 0x09, 0x02, 0x0c] WAVE_SIZE: 64

v_smfmac_f32_16x16x64_bf8_bf8 v[0:3], a[2:3], v[4:7], v1 cbsz:3 abid:1
// ENCODING: [0x00, 0x0b, 0xf8, 0xd3, 0x02, 0x09, 0x06, 0x0c] WAVE_SIZE: 64

v_smfmac_f32_16x16x64_bf8_fp8 v[0:3], a[2:3], v[4:7], v1 cbsz:3 abid:1
// ENCODING: [0x00, 0x0b, 0xf9, 0xd3, 0x02, 0x09, 0x06, 0x0c] WAVE_SIZE: 64

v_smfmac_f32_16x16x64_fp8_bf8 v[0:3], a[2:3], v[4:7], v1 cbsz:3 abid:1
// ENCODING: [0x00, 0x0b, 0xfa, 0xd3, 0x02, 0x09, 0x06, 0x0c] WAVE_SIZE: 64

v_smfmac_f32_16x16x64_fp8_fp8 v[0:3], a[2:3], v[4:7], v1 cbsz:3 abid:1
// ENCODING: [0x00, 0x0b, 0xfb, 0xd3, 0x02, 0x09, 0x06, 0x0c] WAVE_SIZE: 64

v_smfmac_f32_32x32x16_bf16 v[10:25], a[2:3], v[4:7], v6 cbsz:3 abid:1
// ENCODING: [0x0a, 0x0b, 0xe8, 0xd3, 0x02, 0x09, 0x1a, 0x0c] WAVE_SIZE: 64

v_smfmac_f32_32x32x16_f16 v[10:25], a[2:3], v[4:7], v2 cbsz:3 abid:1
// ENCODING: [0x0a, 0x0b, 0xe4, 0xd3, 0x02, 0x09, 0x0a, 0x0c] WAVE_SIZE: 64

v_smfmac_f32_32x32x32_bf8_bf8 v[0:15], a[2:3], v[4:7], v1 cbsz:3 abid:1
// ENCODING: [0x00, 0x0b, 0xfc, 0xd3, 0x02, 0x09, 0x06, 0x0c] WAVE_SIZE: 64

v_smfmac_f32_32x32x32_bf8_fp8 v[0:15], a[2:3], v[4:7], v1 cbsz:3 abid:1
// ENCODING: [0x00, 0x0b, 0xfd, 0xd3, 0x02, 0x09, 0x06, 0x0c] WAVE_SIZE: 64

v_smfmac_f32_32x32x32_fp8_bf8 v[0:15], a[2:3], v[4:7], v1 cbsz:3 abid:1
// ENCODING: [0x00, 0x0b, 0xfe, 0xd3, 0x02, 0x09, 0x06, 0x0c] WAVE_SIZE: 64

v_smfmac_f32_32x32x32_fp8_fp8 v[0:15], a[2:3], v[4:7], v1 cbsz:3 abid:1
// ENCODING: [0x00, 0x0b, 0xff, 0xd3, 0x02, 0x09, 0x06, 0x0c] WAVE_SIZE: 64

v_smfmac_i32_16x16x64_i8 v[10:13], a[2:3], v[4:7], v8 cbsz:3 abid:1
// ENCODING: [0x0a, 0x0b, 0xea, 0xd3, 0x02, 0x09, 0x22, 0x0c] WAVE_SIZE: 64

v_smfmac_i32_32x32x32_i8 v[10:25], a[2:3], v[4:7], v10 cbsz:3 abid:1
// ENCODING: [0x0a, 0x0b, 0xec, 0xd3, 0x02, 0x09, 0x2a, 0x0c] WAVE_SIZE: 64

// SOURCE: mimg-err-gfx942.s

image_atomic_add v4, v192, s[28:35] dmask:0x1 unorm glc
// ERROR: instruction not supported on this GPU (gfx942): image_atomic_add

image_atomic_cmpswap v[4:5], v[192:195], s[28:35] dmask:0x3 unorm glc
// ERROR: instruction not supported on this GPU (gfx942): image_atomic_cmpswap

image_atomic_dec v4, v192, s[28:35] dmask:0x1 unorm
// ERROR: instruction not supported on this GPU (gfx942): image_atomic_dec

image_atomic_smin v4, v192, s[28:35] dmask:0x1 unorm
// ERROR: instruction not supported on this GPU (gfx942): image_atomic_smin

image_atomic_sub v4, v192, s[28:35] dmask:0x1 unorm
// ERROR: instruction not supported on this GPU (gfx942): image_atomic_sub

image_atomic_swap v4, v[192:195], s[28:35] dmask:0x1 unorm glc
// ERROR: instruction not supported on this GPU (gfx942): image_atomic_swap

image_atomic_umin v4, v192, s[28:35] dmask:0x1 unorm
// ERROR: instruction not supported on this GPU (gfx942): image_atomic_umin

image_load_mip_pck_sgn v[4:5], v[0:3], s[8:15] dmask:0x5
// ERROR: instruction not supported on this GPU (gfx942): image_load_mip_pck_sgn

image_load_pck v5, v[0:3], s[8:15] dmask:0x1 glc
// ERROR: instruction not supported on this GPU (gfx942): image_load_pck

image_store v[192:194], v[238:241], s[28:35] dmask:0x7 unorm
// ERROR: instruction not supported on this GPU (gfx942): image_store

image_store_pck v1, v[2:5], s[12:19] dmask:0x1 unorm da
// ERROR: instruction not supported on this GPU (gfx942): image_store_pck

// SOURCE: xdl-insts-gfx908.s

v_dot2c_f32_f16_dpp v5, v1, |v2| quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0x0a, 0x6e, 0x01, 0xe4, 0x80, 0x00] WAVE_SIZE: 64

v_dot2c_f32_f16_e64 v255, -|src_scc|, -1 clamp div:2
// ENCODING: [0xff, 0x81, 0x37, 0xd1, 0xfd, 0x82, 0x01, 0x38] WAVE_SIZE: 64

v_dot2c_f32_f16_e64 v5, 0.5, -|flat_scratch_hi|
// ENCODING: [0x05, 0x02, 0x37, 0xd1, 0xf0, 0xce, 0x00, 0x40] WAVE_SIZE: 64

v_dot2c_f32_f16_e64 v5, src_execz, 0.5 mul:4
// ENCODING: [0x05, 0x00, 0x37, 0xd1, 0xfc, 0xe0, 0x01, 0x10] WAVE_SIZE: 64

v_dot2c_i32_i16_dpp v255, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0xfe, 0x71, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

v_dot4c_i32_i8_dpp v255, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0xfe, 0x73, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

v_dot8c_i32_i4_dpp v255, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0xfe, 0x75, 0x01, 0xe4, 0x00, 0x00] WAVE_SIZE: 64

v_pk_fmac_f16_e32 v5, ttmp11, v2
// ENCODING: [0x77, 0x04, 0x0a, 0x78] WAVE_SIZE: 64
