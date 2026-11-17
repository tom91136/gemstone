float sqrtf(float);
__attribute__((amdgpu_kernel)) void mixf(float *out, const float *in, float s, int n) {
  float a = in[0];
  float b = in[1];
  float c = in[2];
  float d = in[3];
  float r0 = a * s + b;
  float r1 = sqrtf(c) * d;
  int k = (int)(r0 * r1);
  out[0] = r1 + r0;
  out[1] = __builtin_fmaf(r0, r1, s);
  out[2] = (float)(k * 3 + n);
}
