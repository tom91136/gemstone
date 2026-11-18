extern "C" __global__ void predicated(int *out, int a, int b, int c) {
  int x = a > b ? a : b;
  int y = (a ^ c) < 0 ? b + c : b - c;
  if (c > 3) x += y;
  if (a == b) y = x * 3;
  out[0] = x;
  out[1] = y;
  out[2] = (a < c) && (b != 7) ? x : y;
}

extern "C" __global__ void predfloat(float *out, float a, float b, float c) {
  float x = a > b ? a * c : b + c;
  float y = (a != a) ? 0.0f : a;
  if (c < 0.0f) y = -y;
  out[0] = x;
  out[1] = y;
  out[2] = (a <= b) == (b <= c) ? 1.0f : 2.0f;
}

extern "C" __global__ void predstore(int *out, const int *in, int a) {
  int x = in[0];
  if (x > a) out[0] = x - a;
  if (x < 0) out[1] = in[1];
}
