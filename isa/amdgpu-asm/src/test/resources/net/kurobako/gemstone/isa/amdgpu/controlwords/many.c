__attribute__((amdgpu_kernel)) void many(float *out, const float *in, float s) {
  float a0 = in[0];
  float a1 = in[1];
  float a2 = in[2];
  float a3 = in[3];
  float b0 = a0 * s + a1;
  float b1 = a1 * s + a2;
  float b2 = a2 * s + a3;
  float b3 = a3 * s + a0;
  float c0 = b0 * b1 - b2;
  float c1 = b1 * b2 - b3;
  float c2 = b2 * b3 - b0;
  float c3 = b3 * b0 - b1;
  out[0] = c0 + c1;
  out[1] = c1 + c2;
  out[2] = c2 + c3;
  out[3] = c3 + c0;
  out[4] = c0 * c3;
}
