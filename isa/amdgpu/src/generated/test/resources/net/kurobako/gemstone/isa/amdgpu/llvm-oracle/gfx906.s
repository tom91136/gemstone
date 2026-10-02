// Generated from pinned LLVM MC/AMDGPU checks. Regenerate with: sbt codegen
// TARGET: gfx906

// SOURCE: dl-insts-err.s

v_dot2_f32_f16 v0, v1, v2, v3 neg_hi
// ERROR: invalid operand for instruction

v_dot2_f32_f16 v0, v1, v2, v3 neg_lo:[
// ERROR: unknown token in expression

v_dot2_f32_f16 v0, v1, v2, v3 neg_lo:[0,2]
// ERROR: invalid neg_lo value.

v_dot2_f32_f16 v0, |v1|, v2, v3
// ERROR: not a valid operand

v_dot2_i32_i16 v0, v1, v2, v3 op_sel:[0,2]
// ERROR: invalid op_sel value.

// SOURCE: fma-mix.s

v_fma_mix_f32 v0, -|v1|, v2, v3
// ENCODING: [0x00, 0x01, 0xa0, 0xd3, 0x01, 0x05, 0x0e, 0x24] WAVE_SIZE: 64

v_fma_mix_f32 v0, v1, v2, v3 op_sel_hi:[1,0,0]
// ENCODING: [0x00, 0x00, 0xa0, 0xd3, 0x01, 0x05, 0x0e, 0x0c] WAVE_SIZE: 64

v_fma_mixhi_f16 v0, v1, v2, v3 op_sel_hi:[1,0,1] clamp
// ENCODING: [0x00, 0xc0, 0xa2, 0xd3, 0x01, 0x05, 0x0e, 0x0c] WAVE_SIZE: 64

v_fma_mixlo_f16 v0, v1, v2, v3
// ENCODING: [0x00, 0x00, 0xa1, 0xd3, 0x01, 0x05, 0x0e, 0x04] WAVE_SIZE: 64

// SOURCE: gfx950_asm_vop3.s

v_ashr_pk_i8_i32 v2, s4, 4, v2
// ERROR: instruction not supported on this GPU (gfx906): v_ashr_pk_i8_i32

v_bitop3_b16 v5, v1, v2, s3
// ERROR: instruction not supported on this GPU (gfx906): v_bitop3_b16

v_bitop3_b32 v5, v1, v2, s3
// ERROR: instruction not supported on this GPU (gfx906): v_bitop3_b32

// SOURCE: mad-mix.s

v_mad_mix_f32 v0, v1, v2, v3 op_sel_hi:[1,0,0]
// ERROR: instruction not supported on this GPU

// SOURCE: xdl-insts-err.s

v_dot2c_f32_f16_sdwa v0, v1, v2
// ERROR: instruction not supported on this GPU
