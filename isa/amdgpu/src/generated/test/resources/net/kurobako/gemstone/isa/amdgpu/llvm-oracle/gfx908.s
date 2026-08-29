// Generated from pinned LLVM MC/AMDGPU checks. Do not edit.
// TARGET: gfx908

// SOURCE: accvgpr-altnames.s

v_accvgpr_read_b32 v2, a0
// ENCODING: [0x02, 0x40, 0xd8, 0xd3, 0x00, 0x01, 0x00, 0x18]

v_accvgpr_write_b32 a2, -2.0
// ENCODING: [0x02, 0x40, 0xd9, 0xd3, 0xf5, 0x00, 0x00, 0x18]

v_mfma_f32_32x32x1f32 a[0:31], a0, a1, a[32:63]
// ENCODING: [0x00, 0x00, 0xc0, 0xd3, 0x00, 0x03, 0x82, 0x1c]

// SOURCE: atomic-fadd-insts.s

buffer_atomic_add_f32 v5, off, s[8:11], s3 offset:4095 glc
// ERROR: operands are not valid for this GPU or mode

// SOURCE: dl-insts-err.s

v_dot2_f32_f16 v0, v1, v2, v3 neg_hi
// ERROR: invalid operand for instruction

v_dot2_f32_f16 v0, v1, v2, v3 neg_hi:[0,2]
// ERROR: invalid neg_hi value.

v_dot2_f32_f16 v0, v1, v2, v3 neg_lo:[
// ERROR: unknown token in expression

v_dot2_f32_f16 v0, v1, v2, v3 neg_lo:[0,0,0,0,0]
// ERROR: expected a closing square bracket

v_dot2_f32_f16 v0, v1, v2, v3 neg_lo:[0,2]
// ERROR: invalid neg_lo value.

v_dot2_f32_f16 v0, v1, v2, v3 neg_lo:]
// ERROR: expected a left square bracket

v_dot2_f32_f16 v0, |v1|, v2, v3
// ERROR: not a valid operand

v_dot2_f32_f16 v255, s1, s2, s3
// ERROR: invalid operand (violates constant bus restrictions)

v_dot2_i32_i16 v0, v1, v2, v3 op_sel:[0,2]
// ERROR: invalid op_sel value.

v_dot2_i32_i16 v0, v1, v2, v3 op_sel_hi:[0,2]
// ERROR: invalid op_sel_hi value.

v_dot2_u32_u16 v0, v1, v2, v3 op_sel_hi:[0,2]
// ERROR: invalid op_sel_hi value

// SOURCE: ds_swizzle.s

ds_swizzle_b32 v5, v1 offset:swizzle(FFT,0)
// ENCODING: [0x00, 0xe0, 0x7a, 0xd8, 0x01, 0x00, 0x00, 0x05]

ds_swizzle_b32 v5, v1 offset:swizzle(FFT,32)
// ERROR: FFT swizzle must be in the interval [0,31]

ds_swizzle_b32 v5, v1 offset:swizzle(ROTATE)
// ERROR: expected a comma

ds_swizzle_b32 v5, v1 offset:swizzle(ROTATE,0,0)
// ENCODING: [0x00, 0xc0, 0x7a, 0xd8, 0x01, 0x00, 0x00, 0x05]

ds_swizzle_b32 v5, v1 offset:swizzle(ROTATE,0,1,2)
// ERROR: expected a closing parentheses

ds_swizzle_b32 v5, v1 offset:swizzle(ROTATE,0,32)
// ERROR: number of threads to rotate must be in the interval [0,31]

ds_swizzle_b32 v5, v1 offset:swizzle(ROTATE,2,31)
// ERROR: direction must be 0 (left) or 1 (right)

// SOURCE: gfx908_err.s

v_dot4_i32_i8 v0, v1, v2, v3 op_sel:[0,0] op_sel_hi:[0,0]
// ERROR: not a valid operand.

// SOURCE: gfx908_err_pos.s

v_accvgpr_write a2, execz
// ERROR: source operand must be either a VGPR or an inline constant

v_dot2_f32_f16 v0, v1, v2, v3 neg_hi:[0,2]
// ERROR: invalid neg_hi value

v_dot2_f32_f16 v0, v1, v2, v3 neg_lo:[2,0]
// ERROR: invalid neg_lo value

v_mfma_f32_32x32x1f32 a[0:31], v0, v1, 0
// ERROR: inline constants are not allowed for this operand

// SOURCE: gfx90a_asm_features.s

flat_atomic_min_f64 v[0:1], v[0:1], v[2:3] glc
// ERROR: instruction not supported on this GPU

global_atomic_add_f32 v0, v[0:1], v2, off glc scc
// ERROR: scc modifier is not supported on this GPU

// SOURCE: gfx90a_ldst_acc.s

buffer_atomic_sub_x2 a[6:7], off, s[96:99], s3 offset:4095
// ERROR: invalid operand for instruction

ds_gws_barrier a0 gds
// ERROR: invalid register class: agpr loads and stores not supported on this GPU

// SOURCE: gfx9_asm_vop2_features.s

v_pk_fmac_f16_dpp v5, v1, v2 quad_perm:[0,1,2,3] row_mask:0xf bank_mask:0xf
// ENCODING: [0xfa, 0x04, 0x0a, 0x78, 0x01, 0xe4, 0x00, 0xff]

v_pk_fmac_f16_sdwa v5, v1, v2 dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:DWORD src1_sel:DWORD
// ENCODING: [0xf9, 0x04, 0x0a, 0x78, 0x01, 0x06, 0x06, 0x16]

// SOURCE: mai-err.s

v_mfma_f32_32x32x1f32 a[0:31], v0, v1, a[2:33]
// ERROR: source 2 operand must not partially overlap with dst

// SOURCE: mai.s

v_accvgpr_read_b32 v2, a255
// ENCODING: [0x02, 0x40, 0xd8, 0xd3, 0xff, 0x01, 0x00, 0x18]

v_accvgpr_write a2, 100
// ERROR: invalid operand for instruction

v_mfma_f32_16x16x16f16 a[0:3], v[0:1], v[1:2], a[1:4]
// ENCODING: [0x00, 0x00, 0xcd, 0xd3, 0x00, 0x03, 0x06, 0x04]

v_mfma_f32_16x16x1f32 a[0:15], v0, v1, a[17:32]
// ENCODING: [0x00, 0x00, 0xc1, 0xd3, 0x00, 0x03, 0x46, 0x04]

v_mfma_f32_16x16x2bf16 a[0:15], v0, v1, a[17:32]
// ENCODING: [0x00, 0x00, 0xe9, 0xd3, 0x00, 0x03, 0x46, 0x04]

v_mfma_f32_16x16x4f16 a[0:15], v[0:1], v[1:2], a[17:32]
// ENCODING: [0x00, 0x00, 0xc9, 0xd3, 0x00, 0x03, 0x46, 0x04]

v_mfma_f32_16x16x4f32 a[0:3], v0, v1, a[1:4]
// ENCODING: [0x00, 0x00, 0xc5, 0xd3, 0x00, 0x03, 0x06, 0x04]

v_mfma_f32_16x16x8bf16 a[0:3], v0, v1, a[1:4]
// ENCODING: [0x00, 0x00, 0xed, 0xd3, 0x00, 0x03, 0x06, 0x04]

v_mfma_f32_32x32x2bf16 a[0:31], v0, v1, a[33:64]
// ENCODING: [0x00, 0x00, 0xe8, 0xd3, 0x00, 0x03, 0x86, 0x04]

v_mfma_f32_32x32x2f32 a[0:15], v0, v1, a[17:32]
// ENCODING: [0x00, 0x00, 0xc4, 0xd3, 0x00, 0x03, 0x46, 0x04]

v_mfma_f32_32x32x4bf16 a[0:15], v0, v1, a[17:32]
// ENCODING: [0x00, 0x00, 0xec, 0xd3, 0x00, 0x03, 0x46, 0x04]

v_mfma_f32_32x32x4f16 a[0:31], v[0:1], v[1:2], a[33:64]
// ENCODING: [0x00, 0x00, 0xc8, 0xd3, 0x00, 0x03, 0x86, 0x04]

v_mfma_f32_32x32x8f16 a[0:15], v[0:1], v[1:2], a[17:32]
// ENCODING: [0x00, 0x00, 0xcc, 0xd3, 0x00, 0x03, 0x46, 0x04]

v_mfma_f32_4x4x1f32 a[0:3], v0, v1, a[1:4] cbsz:3 abid:2 blgp:7
// ENCODING: [0x00, 0x13, 0xc2, 0xd3, 0x00, 0x03, 0x06, 0xe4]

v_mfma_f32_4x4x2bf16 a[0:3], v0, v1, a[1:4]
// ENCODING: [0x00, 0x00, 0xeb, 0xd3, 0x00, 0x03, 0x06, 0x04]

v_mfma_f32_4x4x4f16 a[0:3], v[0:1], v[1:2], a[1:4]
// ENCODING: [0x00, 0x00, 0xca, 0xd3, 0x00, 0x03, 0x06, 0x04]

v_mfma_i32_16x16x16i8 a[0:3], v0, v1, a[1:4]
// ENCODING: [0x00, 0x00, 0xd5, 0xd3, 0x00, 0x03, 0x06, 0x04]

v_mfma_i32_16x16x4i8 a[0:15], v0, v1, a[17:32]
// ENCODING: [0x00, 0x00, 0xd1, 0xd3, 0x00, 0x03, 0x46, 0x04]

v_mfma_i32_32x32x4i8 a[0:31], v0, v1, a[33:64]
// ENCODING: [0x00, 0x00, 0xd0, 0xd3, 0x00, 0x03, 0x86, 0x04]

v_mfma_i32_32x32x8i8 a[0:15], v0, v1, a[17:32]
// ENCODING: [0x00, 0x00, 0xd4, 0xd3, 0x00, 0x03, 0x46, 0x04]

v_mfma_i32_4x4x4i8 a[0:3], v0, v1, a[1:4]
// ENCODING: [0x00, 0x00, 0xd2, 0xd3, 0x00, 0x03, 0x06, 0x04]

// SOURCE: xdl-insts-err.s

v_dot2c_f32_f16_sdwa v0, v1, v2
// ERROR: sdwa variant of this instruction is not supported

// SOURCE: xdl-insts-gfx908.s

v_dot2c_f32_f16_dpp v5, v1, |v2| quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0x0a, 0x6e, 0x01, 0xe4, 0x80, 0x00]

v_dot2c_f32_f16_e32 v5, v1, v2
// ENCODING: [0x01, 0x05, 0x0a, 0x6e]

v_dot2c_f32_f16_e64 v255, -|src_scc|, -1 clamp div:2
// ENCODING: [0xff, 0x81, 0x37, 0xd1, 0xfd, 0x82, 0x01, 0x38]

v_dot2c_f32_f16_e64 v5, s101, s101
// ENCODING: [0x05, 0x00, 0x37, 0xd1, 0x65, 0xca, 0x00, 0x00]

v_dot2c_f32_f16_e64 v5, src_execz, 0.5 mul:4
// ENCODING: [0x05, 0x00, 0x37, 0xd1, 0xfc, 0xe0, 0x01, 0x10]

v_dot2c_i32_i16_dpp v255, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0xfe, 0x71, 0x01, 0xe4, 0x00, 0x00]

v_dot4c_i32_i8_dpp v255, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0xfe, 0x73, 0x01, 0xe4, 0x00, 0x00]

v_dot8c_i32_i4_dpp v255, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0xfe, 0x75, 0x01, 0xe4, 0x00, 0x00]

v_pk_fmac_f16_e32 v5, ttmp11, v2
// ENCODING: [0x77, 0x04, 0x0a, 0x78]

v_pk_fmac_f16_e32 v5, vcc_hi, v2
// ENCODING: [0x6b, 0x04, 0x0a, 0x78]
