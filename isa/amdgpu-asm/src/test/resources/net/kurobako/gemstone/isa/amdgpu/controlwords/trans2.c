float sqrtf(float);
__attribute__((amdgpu_kernel)) void trans2(float *out, const float *in) {
  float a = in[0];
  float b = in[1];
  float c = sqrtf(a);
  float d = __builtin_amdgcn_rsq(b);
  float e = __builtin_amdgcn_rcp(a * b);
  float f = sqrtf(c * d);
  out[0] = c + d;
  out[1] = e * f;
  out[2] = c * c - d;
}
