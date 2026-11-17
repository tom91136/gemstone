__attribute__((amdgpu_kernel)) void fminmax(float *out, const float *in, float s) {
  float a = in[0];
  float b = in[1];
  float c = in[2];
  out[0] = __builtin_fminf(a, b);
  out[1] = __builtin_fmaxf(b, c);
  out[2] = __builtin_fabsf(a - b) * s;
  out[3] = -a * b + c;
}
