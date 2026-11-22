__attribute__((nvptx_kernel)) void scale(float *out, const float *in, const int *count, double factor) {
  for (int i = 0; i < count[0]; i++) out[i] = (float)(in[i] * factor);
}
