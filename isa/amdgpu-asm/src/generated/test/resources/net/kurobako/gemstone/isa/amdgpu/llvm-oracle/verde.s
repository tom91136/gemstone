// Generated from pinned LLVM MC/AMDGPU checks. Regenerate with: sbt codegen
// TARGET: verde

// SOURCE: exp-gfx10.s

exp pos4 v4, v3, v2, v1
// ERROR: exp target is not supported on this GPU
