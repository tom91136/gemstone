extern "C" __global__ void stores(float *out, float a, float b) {
  out[0] = a;
  out[1] = b;
  out[2] = a + b;
  out[3] = a * b;
  out[4] = a - b;
}

extern "C" __global__ void storeload(unsigned *p, unsigned *q, unsigned v) {
  p[0] = v;
  unsigned x = q[0];
  p[1] = x + v;
  unsigned y = q[1];
  q[2] = y * x;
}

extern "C" __global__ void storewide(double *out, long long *o2, short *o3, double a, long long b) {
  out[0] = a;
  o2[0] = b;
  o3[0] = (short)b;
  out[1] = a * 2.0;
}
