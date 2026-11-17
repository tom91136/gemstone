__attribute__((amdgpu_kernel)) void bitops(int *out, const int *in, int n) {
  int a = in[0];
  int b = in[1];
  int c = __builtin_popcount(a ^ b);
  int d = __builtin_clz(a | 1);
  int e = (a << b) | (b >> a);
  int f = (~a) ^ (b & n);
  out[0] = c + d;
  out[1] = e ^ f;
  out[2] = f + n;
}
