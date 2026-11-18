extern "C" __global__ void sumloop(float *out, const float *a, int n) {
  float s = 0.0f;
  for (int i = 0; i < n; i++) s += a[i] * a[i];
  out[0] = s;
}

extern "C" __global__ void collatz(unsigned *out, unsigned x) {
  unsigned steps = 0;
  while (x != 1u) {
    x = (x & 1u) ? 3u * x + 1u : x >> 1;
    steps++;
  }
  out[0] = steps;
}

extern "C" __global__ void diamond(float *out, const float *a, int k) {
  float x = a[0];
  if (k > 0) {
    x = x * a[1] + a[2];
    out[1] = x;
  } else {
    x = rsqrtf(x) - a[3];
    out[2] = x;
  }
  out[0] = x * 2.0f;
}

extern "C" __global__ void switchy(int *out, int k, int a, int b) {
  int r;
  switch (k) {
  case 0: r = a + b; break;
  case 1: r = a - b; break;
  case 2: r = a * b; break;
  case 3: r = a ^ b; break;
  case 4: r = a / (b | 1); break;
  default: r = 0; break;
  }
  out[0] = r;
}
