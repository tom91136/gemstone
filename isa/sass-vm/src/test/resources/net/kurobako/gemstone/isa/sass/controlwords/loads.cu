extern "C" __global__ void loads(float *out, const float *a, const float *b, const int *idx) {
  float x = a[0];
  float y = b[0];
  float z = a[1] * b[1];
  int i = idx[0];
  out[0] = x * y + z + a[i];
}

extern "C" __global__ void loadchain(unsigned *out, const unsigned *a) {
  unsigned i = a[0];
  unsigned j = a[i & 15u];
  unsigned k = a[j & 15u];
  out[0] = i + j + k;
}

extern "C" __global__ void loadwide(double *out, const double *a, const long long *b, const short *c, const unsigned char *d) {
  out[0] = a[0] + (double)b[0];
  out[1] = (double)(c[1] + d[2]);
}

extern "C" __global__ void loadmany(float *out, const float *a) {
  float s = 0.0f;
  s += a[0]; s += a[1]; s += a[2]; s += a[3];
  s += a[4]; s += a[5]; s += a[6]; s += a[7];
  out[0] = s;
}
