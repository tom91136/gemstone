__attribute__((amdgpu_kernel)) void stores(float *out, const float *in, float s) {
  float a = in[0];
  float b = in[1];
  float c = a * s;
  float d = b * s;
  out[0] = a;
  out[1] = b;
  out[2] = c;
  out[3] = d;
  out[4] = c + d;
  out[5] = c * d;
}
