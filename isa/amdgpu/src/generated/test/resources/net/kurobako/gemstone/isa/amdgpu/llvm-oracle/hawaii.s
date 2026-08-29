// Generated from pinned LLVM MC/AMDGPU checks. Do not edit.
// TARGET: hawaii

// SOURCE: flat-scratch.s

s_mov_b32 flat_scratch_lo, -1
// ENCODING: [0xc1, 0x03, 0xe8, 0xbe]

s_mov_b64 flat_scratch, -1
// ENCODING: [0xc1, 0x04, 0xe8, 0xbe]

s_mov_b64 flat_scratch_lo, -1
// ERROR: invalid operand for instruction

// SOURCE: vop1-gfx9.s

v_cvt_norm_i16_f16 v5, 0xfe0b
// ERROR: instruction not supported on this GPU

// SOURCE: vop3-gfx9.s

v_add_i32_e64 v84, vcc, v13, v31
// ENCODING: [0x54, 0x6a, 0x4a, 0xd2, 0x0d, 0x3f, 0x02, 0x00]

v_sub_i32_e64 v84, vcc, v13, v31
// ENCODING: [0x54, 0x6a, 0x4c, 0xd2, 0x0d, 0x3f, 0x02, 0x00]

v_subrev_i32_e64 v84, vcc, v13, v31
// ENCODING: [0x54, 0x6a, 0x4e, 0xd2, 0x0d, 0x3f, 0x02, 0x00]

// SOURCE: vop3.s

v_mad_i64_i32 v[5:6], s[12:13], s1, 0, v[254:255]
// ENCODING: [0x05, 0x0c, 0xee, 0xd2, 0x01, 0x00, 0xf9, 0x07]

v_mad_u64_u32 v[5:6], s[12:13], s1, 0, 0
// ENCODING: [0x05, 0x0c, 0xec, 0xd2, 0x01, 0x00, 0x01, 0x02]

v_mqsad_u32_u8 v[5:8], s[2:3], v4, v[0:3]
// ENCODING: [0x05, 0x00, 0xea, 0xd2, 0x02, 0x08, 0x02, 0x04]

// SOURCE: vopc-vi.s

v_cmp_eq_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (hawaii): v_cmp_eq_f16

v_cmp_eq_i16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (hawaii): v_cmp_eq_i16

v_cmp_eq_u16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (hawaii): v_cmp_eq_u16

v_cmp_ge_i16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (hawaii): v_cmp_ge_i16

v_cmp_ge_u16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (hawaii): v_cmp_ge_u16

v_cmp_lg_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (hawaii): v_cmp_lg_f16

v_cmp_ne_i16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (hawaii): v_cmp_ne_i16

v_cmp_neq_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (hawaii): v_cmp_neq_f16

v_cmp_t_u16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (hawaii): v_cmp_t_u16

v_cmpx_f_u16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (hawaii): v_cmpx_f_u16

v_cmpx_ge_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (hawaii): v_cmpx_ge_f16

v_cmpx_lg_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (hawaii): v_cmpx_lg_f16

v_cmpx_lt_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (hawaii): v_cmpx_lt_f16

v_cmpx_nle_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (hawaii): v_cmpx_nle_f16

v_cmpx_nlg_f16 vcc, v2, v4
// ERROR: instruction not supported on this GPU (hawaii): v_cmpx_nlg_f16

// SOURCE: xnack-mask.s

s_mov_b32 xnack_mask_hi, -1
// ERROR: xnack_mask_hi register not available on this GPU
