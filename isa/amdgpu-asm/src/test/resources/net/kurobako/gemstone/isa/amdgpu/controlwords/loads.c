__attribute__((amdgpu_kernel)) void loads(float *out, const float *in) {
  float a = in[0];
  float b = in[1];
  float c = in[2];
  float d = in[3];
  float e = in[4];
  out[0] = a + b + c;
  out[1] = a * d - e;
  out[2] = __builtin_fmaf(b, c, d);
  out[3] = a * b * c * d;
}
