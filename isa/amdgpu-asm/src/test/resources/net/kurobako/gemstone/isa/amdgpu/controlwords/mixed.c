__attribute__((amdgpu_kernel)) void mixed(float *out, const float *in, const int *flags, int n) {
  float a = in[0];
  float b = in[1];
  int f = flags[0];
  float c = a * b + (float)f;
  int g = (int)c ^ f;
  float d = (g > n) ? c : b;
  out[0] = d + c;
  out[1] = d - b;
  out[2] = (float)g;
}
