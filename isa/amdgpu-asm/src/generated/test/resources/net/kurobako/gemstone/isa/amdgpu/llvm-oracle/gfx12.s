// Generated from pinned LLVM MC/AMDGPU checks. Regenerate with: sbt codegen
// TARGET: gfx12

// SOURCE: gfx12_5_generic_asm_vop3_err.s

v_mqsad_pk_u16_u8 v[0:1], v[2:3], v4, v[6:7]
// ERROR: instruction not supported on this GPU (gfx12-5-generic): v_mqsad_pk_u16_u8

v_mqsad_u32_u8 v[0:3], s[2:3], v4, v[0:3]
// ERROR: instruction not supported on this GPU (gfx12-5-generic): v_mqsad_u32_u8

v_msad_u8 v0, v1, v2, v3
// ERROR: instruction not supported on this GPU (gfx12-5-generic): v_msad_u8
