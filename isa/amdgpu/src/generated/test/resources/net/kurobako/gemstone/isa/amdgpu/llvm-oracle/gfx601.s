// Generated from pinned LLVM MC/AMDGPU checks. Do not edit.
// TARGET: gfx601

// SOURCE: gfx10_asm_err.s

s_andn2_wrexec_b32 s0, s1
// ERROR: instruction not supported on this GPU

v_mov_b32_dpp v5, v1 dpp8:[0,1,2,3,4,5,6,7]
// ERROR: dpp variant of this instruction is not supported
