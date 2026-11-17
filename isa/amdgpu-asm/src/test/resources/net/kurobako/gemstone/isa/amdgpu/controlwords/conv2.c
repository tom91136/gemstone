__attribute__((amdgpu_kernel)) void conv2(float *out, const float *in) {
  float a = in[0];
  float b = in[1];
  int i = (int)a;
  unsigned u = (unsigned)(a * 1000.0f);
  short s = (short)i;
  float c = (float)u;
  float d = (float)s;
  out[0] = c + d;
  out[1] = (float)i;
  out[2] = __builtin_fmaf(c, d, b);
}
