__attribute__((amdgpu_kernel)) void cmp(int *out, const float *in, int n) {
  float a = in[0];
  float b = in[1];
  int c = a < b ? 1 : 0;
  int d = a > b ? 2 : 3;
  int e = (a == b) ? n : -n;
  out[0] = c + d;
  out[1] = e ^ c;
  out[2] = c < d ? c : d;
}
