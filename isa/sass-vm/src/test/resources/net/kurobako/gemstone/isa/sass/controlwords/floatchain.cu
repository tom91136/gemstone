extern "C" __global__ void floatchain(float *out, float a, float b, float c) {
  float x = a + b;
  x = x * c;
  x = __fmaf_rn(x, a, b);
  x = x - c;
  x = __fmaf_rn(x, x, a);
  x = x * b + c;
  out[0] = x;
}

extern "C" __global__ void floatfan(float *out, float a, float b, float c, float d) {
  float p = a * b;
  float q = c * d;
  float r = a + d;
  float s = b - c;
  out[0] = p + q;
  out[1] = r * s;
  out[2] = __fmaf_rn(p, r, q);
  out[3] = fminf(p, s) + fmaxf(q, r) + fabsf(s);
}

extern "C" __global__ void doublechain(double *out, double a, double b, double c) {
  double x = a + b;
  x = x * c;
  x = __fma_rn(x, a, b);
  x = x - c;
  out[0] = __fma_rn(x, x, a);
}
