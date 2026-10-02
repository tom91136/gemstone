// Generated from pinned LLVM MC/AMDGPU checks. Regenerate with: sbt codegen
// TARGET: stoney

// SOURCE: xnack-mask.s

s_mov_b32 xnack_mask, -1
// ERROR: invalid operand for instruction

s_mov_b32 xnack_mask_lo, -1
// ENCODING: [0xc1, 0x00, 0xe8, 0xbe] WAVE_SIZE: 64

s_mov_b64 xnack_mask, -1
// ENCODING: [0xc1, 0x01, 0xe8, 0xbe] WAVE_SIZE: 64
