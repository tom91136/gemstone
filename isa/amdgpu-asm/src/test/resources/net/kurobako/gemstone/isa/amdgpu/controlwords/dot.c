__attribute__((amdgpu_kernel)) void dot(float *out, const float *in) {
  float a = in[0];
  float b = in[1];
  float c = in[2];
  float d = in[3];
  out[0] = __builtin_fmaf(a, b, __builtin_fmaf(c, d, 1.0f));
  out[1] = a * c - b * d;
  out[2] = a * d + b * c;
}
