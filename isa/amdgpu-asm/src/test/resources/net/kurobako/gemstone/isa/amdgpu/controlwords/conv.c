__attribute__((amdgpu_kernel)) void conv(float *out, const float *in, long *big) {
  float a = in[0];
  float b = in[1];
  int i = (int)a;
  long l = (long)i * 3 + (long)b;
  double d = (double)a + (double)b;
  out[0] = (float)i;
  out[1] = (float)l;
  out[2] = (float)d;
  big[0] = l;
}
