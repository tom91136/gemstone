extern "C" __global__ void convert(float *out, int *iout, int a, unsigned b, float c, double d) {
  out[0] = (float)a;
  out[1] = (float)b;
  out[2] = (float)d;
  iout[0] = (int)c;
  iout[1] = (int)d;
  iout[2] = __float_as_int(c) + a;
}

extern "C" __global__ void convwide(double *out, long long *lout, long long a, float c, double d) {
  out[0] = (double)a;
  out[1] = (double)c;
  lout[0] = (long long)d;
  lout[1] = (long long)c;
}

extern "C" __global__ void constants(float *out, int k, float a) {
  const float table[8] = {1.0f, 2.0f, 4.0f, 8.0f, 16.0f, 32.0f, 64.0f, 128.0f};
  out[0] = table[k & 7] * a + table[(k + 1) & 7];
}
