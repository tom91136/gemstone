__kernel void vector_copy(__global const int *source, __global int *destination, __global int *unused) {
  const int index = get_global_id(0);
  destination[index] = source[index];
}
