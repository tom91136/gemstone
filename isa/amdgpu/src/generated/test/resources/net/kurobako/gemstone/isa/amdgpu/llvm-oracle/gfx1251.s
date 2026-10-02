// Generated from pinned LLVM MC/AMDGPU checks. Regenerate with: sbt codegen
// TARGET: gfx1251

// SOURCE: gfx1250_asm_vop3_err.s

v_ashr_pk_i8_i32 v1, v2, v3, v4 clamp
// ERROR: invalid operand for instruction

v_cvt_scale_pk8_f32_fp8 v[10:17], v[20:21], v8 scale_sel:16
// ERROR: invalid scale_sel value.

v_cvt_sr_bf8_f16 v1, v2, v3 byte_sel:4
// ERROR: invalid byte_sel value.

v_fma_f64 v[4:5], v[2:3], v[6:7], v[8:9] dpp8:[7,6,5,4,3,2,1,0]
// ERROR: not a valid operand.

v_lshl_add_u64 v[2:3], v[4:5], v7, v[8:9] quad_perm:[3,2,1,0]
// ERROR: DP ALU dpp only supports row_share

// SOURCE: gfx1251_asm_vop1_dpp16.s

v_ceil_f64_dpp v[2:3], v[4:5] row_share:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x30, 0x04, 0x7e, 0x04, 0x51, 0x01, 0xff] WAVE_SIZE: 32

v_cvt_f64_i32_dpp v[4:5], v2 row_share:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x08, 0x08, 0x7e, 0x02, 0x51, 0x01, 0xff] WAVE_SIZE: 32

v_floor_f64_dpp v[2:3], v[4:5] row_share:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x34, 0x04, 0x7e, 0x04, 0x51, 0x01, 0xff] WAVE_SIZE: 32

v_frexp_exp_i32_f64_dpp v2, v[4:5] row_share:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x78, 0x04, 0x7e, 0x04, 0x51, 0x01, 0xff] WAVE_SIZE: 32

// SOURCE: gfx1251_asm_vop1_err.s

v_cvt_f64_u32 v[4:5], v2 quad_perm:[3,2,1,0]
// ERROR: DP ALU dpp only supports row_share

// SOURCE: gfx1251_asm_vop2_dpp16.s

v_add_f64_dpp v[4:5], v[2:3], v[4:5] row_share:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x08, 0x08, 0x04, 0x02, 0x51, 0x01, 0xff] WAVE_SIZE: 32

// SOURCE: gfx1251_asm_vop2_err.s

v_fmaak_f64 v[4:5], v[2:3], v[6:7], 123.0 row_share:1
// ERROR: not a valid operand.

// SOURCE: gfx1251_asm_vop3_dpp16.s

v_ashrrev_i64_e64_dpp v[4:5], v2, v[6:7] row_share:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0x04, 0x00, 0x3e, 0xd7, 0xfa, 0x0c, 0x02, 0x00, 0x02, 0x51, 0x01, 0xff] WAVE_SIZE: 32

v_div_fixup_f64_e64_dpp v[4:5], v[2:3], v[6:7], v[8:9] row_share:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0x04, 0x00, 0x28, 0xd6, 0xfa, 0x0c, 0x22, 0x04, 0x02, 0x51, 0x01, 0xff] WAVE_SIZE: 32

v_div_scale_f64_e64_dpp v[4:5], s2, v[2:3], v[6:7], v[8:9] row_share:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0x04, 0x02, 0xfd, 0xd6, 0xfa, 0x0c, 0x22, 0x04, 0x02, 0x51, 0x01, 0xff] WAVE_SIZE: 32

v_lshl_add_u64_e64_dpp v[2:3], v[4:5], v7, v[8:9] row_share:3 row_mask:0xf bank_mask:0xf
// ENCODING: [0x02, 0x00, 0x52, 0xd6, 0xfa, 0x0e, 0x22, 0x04, 0x04, 0x53, 0x01, 0xff] WAVE_SIZE: 32

v_mad_co_u64_u32_e64_dpp v[4:5], s2, v2, v6, v[8:9] row_share:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0x04, 0x02, 0xfe, 0xd6, 0xfa, 0x0c, 0x22, 0x04, 0x02, 0x51, 0x01, 0xff] WAVE_SIZE: 32

v_mad_nc_i64_i32_e64_dpp v[2:3], v4, v5, 1 row_share:0 row_mask:0xf bank_mask:0xf
// ENCODING: [0x02, 0x00, 0xfb, 0xd6, 0xfa, 0x0a, 0x06, 0x02, 0x04, 0x50, 0x01, 0xff] WAVE_SIZE: 32

v_mad_u32_e64_dpp v2, v4, v7, 1 row_share:0 row_mask:0xf bank_mask:0xf
// ENCODING: [0x02, 0x00, 0x35, 0xd6, 0xfa, 0x0e, 0x06, 0x02, 0x04, 0x50, 0x01, 0xff] WAVE_SIZE: 32

v_maximum_f64_e64_dpp v[4:5], v[2:3], v[6:7] row_share:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0x04, 0x00, 0x42, 0xd7, 0xfa, 0x0c, 0x02, 0x00, 0x02, 0x51, 0x01, 0xff] WAVE_SIZE: 32

v_min_u64_e64_dpp v[2:3], v[4:5], v[6:7] row_share:3 row_mask:0xf bank_mask:0xf fi:1
// ENCODING: [0x02, 0x00, 0x18, 0xd7, 0xfa, 0x0c, 0x02, 0x00, 0x04, 0x53, 0x05, 0xff] WAVE_SIZE: 32

v_mul_hi_i32_e64_dpp v4, v2, v6 row_share:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0x04, 0x00, 0x2e, 0xd7, 0xfa, 0x0c, 0x02, 0x00, 0x02, 0x51, 0x01, 0xff] WAVE_SIZE: 32

// SOURCE: gfx1251_asm_vop3_from_vop1_dpp16.s

v_ceil_f64_e64_dpp v[2:3], v[4:5] row_share:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0x02, 0x00, 0x98, 0xd5, 0xfa, 0x00, 0x00, 0x00, 0x04, 0x51, 0x01, 0xff] WAVE_SIZE: 32

// SOURCE: gfx1251_asm_vop3_from_vop1_err.s

v_cvt_f64_u32_e64_dpp v[4:5], v2 quad_perm:[3,2,1,0]
// ERROR: DP ALU dpp only supports row_share

v_rcp_f64_e64_dpp v[4:5], v[2:3] row_share:1
// ERROR: e64_dpp variant of this instruction is not supported

// SOURCE: gfx1251_asm_vop3_from_vop2_dpp16.s

v_add_f64_e64_dpp v[4:5], v[2:3], v[4:5] row_share:1 row_mask:0xf bank_mask:0xf
// ENCODING: [0x04, 0x00, 0x02, 0xd5, 0xfa, 0x08, 0x02, 0x00, 0x02, 0x51, 0x01, 0xff] WAVE_SIZE: 32

// SOURCE: gfx1251_asm_vop3_from_vop2_err.s

v_fmac_f64_e64_dpp v[4:5], v[2:3], v[4:5] dpp8:[7,6,5,4,3,2,1,0]
// ERROR: not a valid operand.

// SOURCE: gfx1251_asm_vop3p.s

v_pk_add_f64 v[4:7], s[8:11], s[12:15]
// ENCODING: [0x04, 0x40, 0x4b, 0xcc, 0x08, 0x18, 0x00, 0x1a] WAVE_SIZE: 32

v_pk_add_f64 v[4:7], v[8:11], 1.0
// ENCODING: [0x04, 0x40, 0x4b, 0xcc, 0x08, 0xe5, 0x01, 0x1a] WAVE_SIZE: 32

v_pk_add_f64 v[4:7], v[8:11], v[12:15] clamp
// ENCODING: [0x04, 0xc0, 0x4b, 0xcc, 0x08, 0x19, 0x02, 0x1a] WAVE_SIZE: 32

v_pk_add_f64 v[4:7], v[8:11], v[12:15] neg_lo:[1,0] neg_hi:[1,0]
// ENCODING: [0x04, 0x41, 0x4b, 0xcc, 0x08, 0x19, 0x02, 0x3a] WAVE_SIZE: 32

v_pk_add_nc_u64 v[4:7], v[8:11], v[12:15]
// ENCODING: [0x04, 0x40, 0x4c, 0xcc, 0x08, 0x19, 0x02, 0x1a] WAVE_SIZE: 32

v_pk_fma_f64 v[4:7], v[8:11], v[12:15], v[16:19]
// ENCODING: [0x04, 0x40, 0x3b, 0xcc, 0x08, 0x19, 0x42, 0x1c] WAVE_SIZE: 32

v_pk_lshl_add_u64 v[4:7], v[8:11], v[12:13], v[16:19]
// ENCODING: [0x04, 0x40, 0x7e, 0xcc, 0x08, 0x19, 0x42, 0x1c] WAVE_SIZE: 32

v_pk_max_num_f64 v[4:7], v[8:11], v[12:15]
// ENCODING: [0x04, 0x40, 0x4e, 0xcc, 0x08, 0x19, 0x02, 0x1a] WAVE_SIZE: 32

v_pk_min_num_f64 v[4:7], v[8:11], v[12:15]
// ENCODING: [0x04, 0x40, 0x4f, 0xcc, 0x08, 0x19, 0x02, 0x1a] WAVE_SIZE: 32

v_pk_mul_f64 v[4:7], v[8:11], v[12:15]
// ENCODING: [0x04, 0x40, 0x3c, 0xcc, 0x08, 0x19, 0x02, 0x1a] WAVE_SIZE: 32

v_pk_sub_nc_u64 v[4:7], v[8:11], v[12:15]
// ENCODING: [0x04, 0x40, 0x4d, 0xcc, 0x08, 0x19, 0x02, 0x1a] WAVE_SIZE: 32

// SOURCE: gfx1251_asm_wmma_w32.s

v_wmma_f64_16x16x4_f64 v[8:23], v[0:3], v[4:7], 1.0
// ENCODING: [0x08, 0x00, 0x5b, 0xcc, 0x00, 0x09, 0xca, 0x1b] WAVE_SIZE: 32

v_wmma_f64_16x16x4_f64 v[8:23], v[0:3], v[4:7], 1.0
// ERROR: instruction requires wavesize=32

// SOURCE: gfx1251_asm_wmma_w32_err.s

v_wmma_f64_16x16x4_f64 v[8:23], v[0:3], v[4:7], s[8:23]
// ERROR: invalid operand for instruction

// SOURCE: gfx1251_err.s

v_pk_fma_f64 v[4:7], v[16:19], v[5:8], null
// ERROR: invalid register class: vgpr tuples must be 64 bit aligned

v_pk_fma_f64 v[4:7], v[8:11], v[16:19], lit64(0x12345678a)
// ERROR: invalid operand for instruction
