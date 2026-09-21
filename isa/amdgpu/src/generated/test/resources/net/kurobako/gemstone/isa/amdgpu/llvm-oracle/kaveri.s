// Generated from pinned LLVM MC/AMDGPU checks. Regenerate with: sbt codegen
// TARGET: kaveri

// SOURCE: smem.s

s_buffer_load_dword s10, s[92:95], m0
// ENCODING: [0x7c, 0x5c, 0x05, 0xc2]

s_buffer_load_dword ttmp0, s[92:95], m0
// ENCODING: [0x7c, 0x5c, 0x38, 0xc2]

s_buffer_load_dwordx2 s[10:11], s[92:95], m0
// ENCODING: [0x7c, 0x5c, 0x45, 0xc2]

s_load_dword s1, s[2:3], -1
// ERROR: operands are not valid for this GPU or mode

s_load_dword s1, s[2:3], 0xfc glc
// ERROR: cache policy is not supported for SMRD instructions

s_load_dword s1, s[2:3], s0 offset:0x1FFFFF
// ERROR: not a valid operand.

s_store_dword tma_lo, s[2:3], s4
// ERROR: instruction not supported on this GPU
