/*
 * Adapted from ROCr's rocrtst vector_copy_kernel.cl.
 * Copyright (c) 2025, Advanced Micro Devices, Inc. All rights reserved.
 * Distributed under the University of Illinois/NCSA Open Source License;
 * see ../LICENSE-ROCR-NCSA.
 */

__kernel void vector_copy(__global const int *a, __global int *b, __global int *c) {
  int i = get_global_id(0);
  b[i] = a[i];
}
