__attribute__((amdgpu_kernel)) void intmath(int *out, const float *in, int n) {
  int a = (int)in[0];
  int b = (int)in[1];
  int c = (a << 3) ^ (b >> 2);
  int d = c * 7 + n;
  int e = (d & 0xffff) | (a + b);
  int f = (e < 0) ? -e : e;
  int g = (c > d) ? c : d;
  out[0] = e + f;
  out[1] = g - c;
  out[2] = (f * g) >> 1;
}
