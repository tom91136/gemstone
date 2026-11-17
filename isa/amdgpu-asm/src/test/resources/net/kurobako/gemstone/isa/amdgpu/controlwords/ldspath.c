__attribute__((amdgpu_kernel)) void ldspath(float *out, const float *in) {
  float a = in[0];
  float b = in[1];
  float c = in[2];
  float d = in[3];
  float e = a * b;
  float f = c * d;
  float g = e + f;
  float h = e - f;
  out[0] = g * h;
  out[1] = g + h;
}
