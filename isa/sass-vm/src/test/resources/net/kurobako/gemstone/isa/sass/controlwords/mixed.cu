extern "C" __global__ void saxpy(float *y, const float *x, float a) {
  unsigned i = blockIdx.x * blockDim.x + threadIdx.x;
  y[i] = a * x[i] + y[i];
}

extern "C" __global__ void dot4(float *out, const float *a, const float *b) {
  float s = a[0] * b[0];
  s = __fmaf_rn(a[1], b[1], s);
  s = __fmaf_rn(a[2], b[2], s);
  s = __fmaf_rn(a[3], b[3], s);
  out[0] = s;
}

extern "C" __global__ void normalise(float *out, const float *v) {
  float x = v[0], y = v[1], z = v[2];
  float r = rsqrtf(x * x + y * y + z * z);
  out[0] = x * r;
  out[1] = y * r;
  out[2] = z * r;
}

extern "C" __global__ void hash(unsigned *out, const unsigned *in) {
  unsigned i = threadIdx.x;
  unsigned h = in[i];
  h ^= h >> 16; h *= 0x85ebca6bu;
  h ^= h >> 13; h *= 0xc2b2ae35u;
  h ^= h >> 16;
  out[i] = h;
}

extern "C" __global__ void horner(double *out, const double *c, double x) {
  double r = c[0];
  r = r * x + c[1];
  r = r * x + c[2];
  r = r * x + c[3];
  out[0] = r;
}
