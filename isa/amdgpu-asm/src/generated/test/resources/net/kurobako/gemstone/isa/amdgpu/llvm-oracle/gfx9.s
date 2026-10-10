// Generated from pinned LLVM MC/AMDGPU checks. Regenerate with: sbt codegen
// TARGET: gfx9

// SOURCE: gfx9_4_generic_unsupported.s

v_cvt_f32_fp8 v1, 3
// ERROR: instruction not supported on this GPU (gfx9-4-generic): v_cvt_f32_fp8

v_mfma_f32_16x16x32_bf8_bf8 a[0:3], v[2:3], v[4:5], a[0:3]
// ERROR: instruction not supported on this GPU (gfx9-4-generic): v_mfma_f32_16x16x32_bf8_bf8

v_mfma_f32_16x16x32_fp8_fp8 a[0:3], v[2:3], v[4:5], a[0:3]
// ERROR: instruction not supported on this GPU (gfx9-4-generic): v_mfma_f32_16x16x32_fp8_fp8

v_mfma_f32_16x16x8xf32 a[0:3], v[2:3], v[4:5], a[2:5]
// ERROR: instruction not supported on this GPU (gfx9-4-generic): v_mfma_f32_16x16x8xf32

v_mfma_f32_32x32x16_bf8_fp8 a[0:15], v[2:3], v[4:5], a[0:15]
// ERROR: instruction not supported on this GPU (gfx9-4-generic): v_mfma_f32_32x32x16_bf8_fp8

v_mfma_f32_32x32x4_xf32 a[0:15], v[2:3], v[4:5], a[18:33]
// ERROR: instruction not supported on this GPU (gfx9-4-generic): v_mfma_f32_32x32x4_xf32

v_mfma_f32_32x32x4xf32 a[0:15], v[2:3], v[4:5], a[18:33]
// ERROR: instruction not supported on this GPU (gfx9-4-generic): v_mfma_f32_32x32x4xf32

v_smfmac_f32_16x16x64_bf8_fp8 a[0:3], v[2:3], a[4:7], v1
// ERROR: instruction not supported on this GPU (gfx9-4-generic): v_smfmac_f32_16x16x64_bf8_fp8

v_smfmac_f32_32x32x32_bf8_bf8 a[0:15], v[2:3], a[4:7], v1
// ERROR: instruction not supported on this GPU (gfx9-4-generic): v_smfmac_f32_32x32x32_bf8_bf8

v_smfmac_f32_32x32x32_bf8_fp8 a[0:15], v[2:3], a[4:7], v1
// ERROR: instruction not supported on this GPU (gfx9-4-generic): v_smfmac_f32_32x32x32_bf8_fp8

v_smfmac_f32_32x32x32_fp8_bf8 a[0:15], v[2:3], a[4:7], v1
// ERROR: instruction not supported on this GPU (gfx9-4-generic): v_smfmac_f32_32x32x32_fp8_bf8
