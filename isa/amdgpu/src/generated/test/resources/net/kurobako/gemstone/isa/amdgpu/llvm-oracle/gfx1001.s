// Generated from pinned LLVM MC/AMDGPU checks. Do not edit.
// TARGET: gfx1001

// SOURCE: xnack-mask.s

s_mov_b32 xnack_mask_hi, -1
// ERROR: xnack_mask_hi register not available on this GPU

s_mov_b32 xnack_mask_lo, -1
// ERROR: xnack_mask_lo register not available on this GPU

s_mov_b64 xnack_mask, -1
// ERROR: xnack_mask register not available on this GPU
