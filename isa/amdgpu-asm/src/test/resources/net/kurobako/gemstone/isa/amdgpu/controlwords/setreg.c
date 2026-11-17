__attribute__((amdgpu_kernel)) void setreg(int *out, const int *in) {
  int v = in[0];
  __builtin_amdgcn_s_setreg(3, 0);
  int w = in[1];
  __builtin_amdgcn_s_setreg(3, 1);
  out[0] = v + w + (int)__builtin_amdgcn_s_getreg(3);
  __builtin_amdgcn_s_setreg(4, 0);
  out[1] = w;
}
