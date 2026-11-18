__attribute__((amdgpu_kernel)) void vmemsgpr(int *out, const int *in) {
  int offset = __builtin_amdgcn_readfirstlane(*(volatile const int *)in);
  __amdgpu_buffer_rsrc_t rsrc = __builtin_amdgcn_make_buffer_rsrc((void *)in, 0, 1024, 0);
  out[0] = __builtin_amdgcn_raw_buffer_load_b32(rsrc, 0, offset, 0);
}
