// Generated from pinned LLVM MC/AMDGPU checks. Regenerate with: sbt codegen
// TARGET: gfx800

// SOURCE: dl-insts-err.s

v_fmac_f32 v0, v1, v2
// ERROR: instruction not supported on this GPU
