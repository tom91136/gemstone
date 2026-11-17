__attribute__((amdgpu_kernel)) void i64(long *out, const int *in, long n) {
  long a = in[0];
  long b = in[1];
  long c = a * 3 + n;
  long d = (a << 5) ^ (b >> 3);
  out[0] = c + d;
  out[1] = c * d;
  out[2] = c - d;
}
