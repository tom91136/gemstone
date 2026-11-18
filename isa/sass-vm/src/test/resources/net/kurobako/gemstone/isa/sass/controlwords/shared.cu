extern "C" __global__ void shared(float *out, const float *in) {
  __shared__ float buffer[64];
  unsigned t = threadIdx.x & 63u;
  buffer[t] = in[t];
  __syncthreads();
  float x = buffer[(t + 1u) & 63u];
  float y = buffer[(t + 2u) & 63u];
  out[t] = x * y + buffer[t];
}

extern "C" __global__ void local(int *out, const int *in, int k) {
  volatile int stack[8];
  for (int i = 0; i < 8; i++) stack[i] = in[i] + k;
  out[0] = stack[k & 7] + stack[(k + 3) & 7];
}

extern "C" __global__ void atomics(int *out, int *counter, int a) {
  int x = atomicAdd(counter, a);
  int y = atomicMax(counter + 1, x);
  out[0] = x + y;
}
