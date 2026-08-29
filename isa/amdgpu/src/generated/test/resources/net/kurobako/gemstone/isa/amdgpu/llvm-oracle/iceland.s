// Generated from pinned LLVM MC/AMDGPU checks. Do not edit.
// TARGET: iceland

// SOURCE: isa-version-hsa.s

.amdgcn_target "amdgcn-amd-amdhsa--gfx802"
// ERROR: .amdgcn_target amdgcn-amd-amdhsa-unknown-gfx802 is incompatible with amdgcn-amd-amdpal-unknown-gfx802

.amdgcn_target "amdgcn-amd-amdhsa--gfx802"
// ERROR: .amdgcn_target amdgcn-amd-amdhsa-unknown-gfx802 is incompatible with amdgcn-amd-unknown-unknown-gfx802
