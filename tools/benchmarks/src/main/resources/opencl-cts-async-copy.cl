// Focused kernels derived from OpenCL CTS basic/test_async_copy.cpp and
// basic/test_async_strided_copy.cpp (Khronos OpenCL CTS, Apache-2.0).
// The CTS sweeps types, vector widths, strides and hundreds of workgroups.
// These fixed int kernels retain the barrier/copy/wait paths for quick probes.

__kernel void global_to_local(__global const int *src, __global int *dst,
                              __local int *scratch, int stride) {
  const int local_id = get_local_id(0);
  const int group = get_group_id(0);
  const int count = get_local_size(0) * 3;
  for (int i = 0; i < 3; ++i)
    scratch[local_id * 3 + i] = 0;
  barrier(CLK_LOCAL_MEM_FENCE);
  event_t event = async_work_group_strided_copy(
      scratch, src + group * count * stride, count, stride, 0);
  wait_group_events(1, &event);
  for (int i = 0; i < 3; ++i)
    dst[(group * count + local_id * 3 + i) * stride] = scratch[local_id * 3 + i];
}

__kernel void local_to_global(__global const int *src, __global int *dst,
                              __local int *scratch, int stride) {
  const int local_id = get_local_id(0);
  const int group = get_group_id(0);
  const int count = get_local_size(0) * 3;
  for (int i = 0; i < 3; ++i)
    scratch[local_id * 3 + i] = 0;
  barrier(CLK_LOCAL_MEM_FENCE);
  for (int i = 0; i < 3; ++i)
    scratch[local_id * 3 + i] = src[(group * count + local_id * 3 + i) * stride];
  barrier(CLK_LOCAL_MEM_FENCE);
  event_t event = async_work_group_strided_copy(
      dst + group * count * stride, scratch, count, stride, 0);
  wait_group_events(1, &event);
}

__kernel void global_to_local_contiguous(__global const int *src,
                                         __global int *dst,
                                         __local int *scratch, int stride) {
  const int local_id = get_local_id(0);
  const int group = get_group_id(0);
  const int count = get_local_size(0) * 3;
  for (int i = 0; i < 3; ++i)
    scratch[local_id * 3 + i] = 0;
  barrier(CLK_LOCAL_MEM_FENCE);
  event_t event = async_work_group_copy(scratch, src + group * count, count, 0);
  wait_group_events(1, &event);
  for (int i = 0; i < 3; ++i)
    dst[group * count + local_id * 3 + i] = scratch[local_id * 3 + i];
}

__kernel void local_to_global_contiguous(__global const int *src,
                                         __global int *dst,
                                         __local int *scratch, int stride) {
  const int local_id = get_local_id(0);
  const int group = get_group_id(0);
  const int count = get_local_size(0) * 3;
  for (int i = 0; i < 3; ++i)
    scratch[local_id * 3 + i] = 0;
  barrier(CLK_LOCAL_MEM_FENCE);
  for (int i = 0; i < 3; ++i)
    scratch[local_id * 3 + i] = src[group * count + local_id * 3 + i];
  barrier(CLK_LOCAL_MEM_FENCE);
  event_t event = async_work_group_copy(dst + group * count, scratch, count, 0);
  wait_group_events(1, &event);
}
