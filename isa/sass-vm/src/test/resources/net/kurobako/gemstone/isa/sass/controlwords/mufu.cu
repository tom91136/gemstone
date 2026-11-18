extern "C" __global__ void mufu(float *out, float a, float b) {
  out[0] = 1.0f / a;
  out[1] = rsqrtf(b);
  out[2] = sqrtf(a + b);
  out[3] = __sinf(a);
  out[4] = __cosf(b);
  out[5] = __log2f(a);
  out[6] = exp2f(b);
}

extern "C" __global__ void mufuchain(float *out, float a) {
  float x = rsqrtf(a);
  x = x * a;
  x = 1.0f / x;
  x = x + a;
  out[0] = rsqrtf(x) * x;
}

extern "C" __global__ void divide(float *out, float a, float b, int c, int d) {
  out[0] = a / b;
  out[1] = (float)(c / d);
}
