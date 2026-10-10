// Generated from pinned LLVM MC/AMDGPU checks. Regenerate with: sbt codegen
// TARGET: gfx1013

// SOURCE: dl-insts-err.s

v_dot2_f32_f16 v0, v1, v2, v3
// ERROR: instruction not supported on this GPU

// SOURCE: gfx1013.s

image_bvh64_intersect_ray v[39:42], [v50, v46, v23, v17, v16, v15, v21, v20, v19, v37, v40, v42], s[12:15]
// ENCODING: [0x07, 0x9f, 0x9c, 0xf1, 0x32, 0x27, 0x03, 0x00, 0x2e, 0x17, 0x11, 0x10, 0x0f, 0x15, 0x14, 0x13, 0x25, 0x28, 0x2a, 0x00] WAVE_SIZE: 32

image_bvh64_intersect_ray v[5:8], v[1:12], ttmp[12:15]
// ENCODING: [0x01, 0x9f, 0x9c, 0xf1, 0x01, 0x05, 0x1e, 0x00] WAVE_SIZE: 32

image_bvh64_intersect_ray v[5:8], v[247:255], s[8:11] a16
// ENCODING: [0x01, 0x9f, 0x9c, 0xf1, 0xf7, 0x05, 0x02, 0x40] WAVE_SIZE: 32

image_bvh_intersect_ray v[252:255], v[1:11], s[8:11]
// ENCODING: [0x01, 0x9f, 0x98, 0xf1, 0x01, 0xfc, 0x02, 0x00] WAVE_SIZE: 32

image_msaa_load v5, v[1:4], s[8:15] dmask:0x8 dim:SQ_RSRC_IMG_2D_MSAA_ARRAY d16
// ENCODING: [0x39, 0x08, 0x00, 0xf0, 0x01, 0x05, 0x02, 0x80] WAVE_SIZE: 32

image_msaa_load v[5:6], v[1:4], s[8:15] dmask:0x3 dim:SQ_RSRC_IMG_2D_MSAA_ARRAY dlc
// ENCODING: [0xb9, 0x03, 0x00, 0xf0, 0x01, 0x05, 0x02, 0x00] WAVE_SIZE: 32

// SOURCE: gfx10_unsupported.s

buffer_load_d16_hi_format_x v255, off, s[8:11], s3 offset:4095
// ERROR: instruction not supported on this GPU (gfx1013): buffer_load_d16_hi_format_x

buffer_wbinvl1
// ERROR: instruction not supported on this GPU (gfx1013): buffer_wbinvl1

flat_load_b96 v[1:3], v[5:6]
// ERROR: instruction not supported on this GPU (gfx1013): flat_load_b96

scratch_store_d16_hi_b8 off, v2, null
// ERROR: instruction not supported on this GPU (gfx1013): scratch_store_d16_hi_b8

v_mfma_f32_32x32x2_f32 a[0:15], v0, v1, a[18:33]
// ERROR: instruction not supported on this GPU (gfx1013): v_mfma_f32_32x32x2_f32

v_smfmac_f32_32x32x32fp8bf8 v[0:15], a[2:3], v[4:7], v1 cbsz:3 abid:1
// ERROR: instruction not supported on this GPU (gfx1013): v_smfmac_f32_32x32x32fp8bf8

// SOURCE: gfx10_unsupported_sdwa.s

v_ashrrev_i16_sdwa v255, v1, v2 dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:DWORD src1_sel:DWORD
// ERROR: sdwa variant of this instruction is not supported
