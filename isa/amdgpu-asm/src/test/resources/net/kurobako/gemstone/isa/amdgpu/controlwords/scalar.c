__attribute__((amdgpu_kernel)) void scalar(int *out, const int *in, int n) {
  int a = in[0];
  int b = in[1];
  int c = a * 3 + n;
  int d = c < b ? c : b;
  int e = (a & 255) | (b << 3);
  out[0] = d + e;
  out[1] = d ^ e;
  out[2] = (a > 7 ? b : a) + (b >= c ? 1 : 2);
}
