__device__ __noinline__ float helper(float a, float b) { return a * b + 1.0f; }

__device__ __noinline__ int ihelper(const int *p, int a) { return p[a & 3] + a; }

extern "C" __global__ void calls(float *out, const int *p, float a, float b) {
  float x = helper(a, b);
  float y = helper(x, a);
  int z = ihelper(p, (int)y);
  out[0] = x + y + (float)z;
}

extern "C" __global__ void threadid(unsigned *out, const unsigned *in) {
  unsigned i = blockIdx.x * blockDim.x + threadIdx.x;
  out[i] = in[i] + threadIdx.y * gridDim.x;
}
