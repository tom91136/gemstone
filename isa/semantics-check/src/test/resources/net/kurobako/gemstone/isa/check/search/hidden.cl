__kernel void hidden_args(__global const float *in, __global float *out, unsigned n) {
  size_t i = __builtin_amdgcn_workgroup_id_x() * __builtin_amdgcn_workgroup_size_x() + __builtin_amdgcn_workitem_id_x();
  if (i < n) out[i] = in[i] * in[i] + 1.0f;
}
