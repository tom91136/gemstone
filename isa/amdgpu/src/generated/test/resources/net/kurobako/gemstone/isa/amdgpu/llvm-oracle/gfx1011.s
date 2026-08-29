// Generated from pinned LLVM MC/AMDGPU checks. Do not edit.
// TARGET: gfx1011

// SOURCE: gfx1011_dlops.s

v_dot2_f32_f16 v0, v1, v2, v3
// ENCODING: [0x00, 0x40, 0x13, 0xcc, 0x01, 0x05, 0x0e, 0x1c]

v_dot2_i32_i16 v0, v1, v2, v3
// ENCODING: [0x00, 0x40, 0x14, 0xcc, 0x01, 0x05, 0x0e, 0x1c]

v_dot2_u32_u16 v0, v1, v2, v3
// ENCODING: [0x00, 0x40, 0x15, 0xcc, 0x01, 0x05, 0x0e, 0x1c]

v_dot2c_f32_f16 v5, v1, v2
// ENCODING: [0x01, 0x05, 0x0a, 0x04]

v_dot2c_f32_f16_dpp v5, v1, v2 dpp8:[7,6,5,4,3,2,1,0]
// ENCODING: [0xe9, 0x04, 0x0a, 0x04, 0x01, 0x77, 0x39, 0x05]

v_dot2c_f32_f16_dpp v5, v1, v2 quad_perm:[0,1,2,3] row_mask:0x0 bank_mask:0x0
// ENCODING: [0xfa, 0x04, 0x0a, 0x04, 0x01, 0xe4, 0x00, 0x00]

v_dot4_i32_i8 v0, v1, v2, v3
// ENCODING: [0x00, 0x40, 0x16, 0xcc, 0x01, 0x05, 0x0e, 0x1c]

v_dot4_u32_u8 v0, v1, v2, v3
// ENCODING: [0x00, 0x40, 0x17, 0xcc, 0x01, 0x05, 0x0e, 0x1c]

v_dot4c_i32_i8 v5, v1, v2
// ENCODING: [0x01, 0x05, 0x0a, 0x1a]

v_dot8_i32_i4 v0, v1, v2, v3
// ENCODING: [0x00, 0x40, 0x18, 0xcc, 0x01, 0x05, 0x0e, 0x1c]

v_dot8_u32_u4 v0, v1, v2, v3
// ENCODING: [0x00, 0x40, 0x19, 0xcc, 0x01, 0x05, 0x0e, 0x1c]

// SOURCE: gfx1011_err.s

image_bvh64_intersect_ray v[4:7], v[9:24], s[4:7] a16
// ERROR: instruction not supported on this GPU (gfx1011): image_bvh64_intersect_ray

image_bvh_intersect_ray v[4:7], v[9:16], s[4:7] a16
// ERROR: instruction not supported on this GPU (gfx1011): image_bvh_intersect_ray

image_msaa_load v14, [v204,v11,v14,v19], s[40:47] dmask:0x1 dim:SQ_RSRC_IMG_2D_MSAA_ARRAY
// ERROR: instruction not supported on this GPU (gfx1011): image_msaa_load

s_getreg_b32 s2, hwreg(HW_REG_SHADER_CYCLES)
// ERROR: invalid hardware register: not supported on this GPU

v_dot8c_i32_i4 v5, v1, v2
// ERROR: instruction not supported on this GPU (gfx1011): v_dot8c_i32_i4

v_fma_legacy_f32 v0, v1, v2, v3
// ERROR: instruction not supported on this GPU (gfx1011): v_fma_legacy_f32

v_fmac_legacy_f32 v0, v1, v2
// ERROR: instruction not supported on this GPU (gfx1011): v_fmac_legacy_f32

// SOURCE: xdl-insts-gfx1011-gfx1012.s

v_dot2c_f32_f16 v255, v1, v2
// ENCODING: [0x01, 0x05, 0xfe, 0x05]
