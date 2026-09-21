// Generated from pinned LLVM MC/AMDGPU checks. Regenerate with: sbt codegen
// TARGET: gfx904

// SOURCE: fma-mix.s

v_fma_mix_f32 v0, -|v1|, v2, v3
// ENCODING: [0x00, 0x01, 0xa0, 0xd3, 0x01, 0x05, 0x0e, 0x24]

v_fma_mix_f32 v0, v1, v2, v3 op_sel_hi:[1,0,0]
// ENCODING: [0x00, 0x00, 0xa0, 0xd3, 0x01, 0x05, 0x0e, 0x0c]

v_fma_mixhi_f16 v0, v1, v2, v3 op_sel_hi:[1,0,1] clamp
// ENCODING: [0x00, 0xc0, 0xa2, 0xd3, 0x01, 0x05, 0x0e, 0x0c]

v_fma_mixlo_f16 v0, v1, v2, v3
// ENCODING: [0x00, 0x00, 0xa1, 0xd3, 0x01, 0x05, 0x0e, 0x04]

// SOURCE: mad-mix.s

v_mad_mix_f32 v0, v1, v2, v3 op_sel_hi:[1,0,0]
// ERROR: instruction not supported on this GPU
