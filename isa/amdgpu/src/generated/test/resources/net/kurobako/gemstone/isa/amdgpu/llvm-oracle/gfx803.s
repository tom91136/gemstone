// Generated from pinned LLVM MC/AMDGPU checks. Do not edit.
// TARGET: gfx803

// SOURCE: isa-version-hsa.s

.amdgcn_target "amdgcn-amd-amdhsa--gfx802"
// ERROR: .amdgcn_target directive processor gfx802 does not match the specified processor gfx803

// SOURCE: mubuf-gfx950.s

buffer_load_dwordx3 off, s[8:11], s3 lds
// ERROR: invalid operand for instruction

buffer_load_dwordx3 off, s[8:11], s3 offset:4095 lds
// ERROR: not a valid operand.
