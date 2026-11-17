float sqrtf(float);
__attribute__((amdgpu_kernel)) void trans(float *out, const float *in) {
  float a = in[0];
  float b = in[1];
  out[0] = 1.0f / a;
  out[1] = sqrtf(b);
  out[2] = __builtin_amdgcn_rsq(b * a);
  out[3] = __builtin_amdgcn_rcp(a + b);
}
