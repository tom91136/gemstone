__attribute__((amdgpu_kernel)) void salu(int *out, const int *in, int n) {
  int a = in[0];
  int b = in[1];
  int c = a * 3 + 1;
  int d = c < b ? c : b;
  int e = (a & 255) | (b << 3);
  int f = (d >= e) ? d : e;
  int g = (a ^ b) + (c & d);
  out[0] = f + g;
  out[1] = f - g;
}
