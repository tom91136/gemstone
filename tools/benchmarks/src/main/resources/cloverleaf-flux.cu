// Bounded flux-calculation fixture inspired by the CUDA and HIP CloverLeaf
// flux_calc kernels. The full miniapp invokes many kernels over many steps;
// this isolates its double-precision, neighbouring-cell memory pattern.
#if defined(__HIPCC__)
#include <hip/hip_runtime.h>
#else
#include <cuda_runtime.h>
#endif

extern "C" __global__ void clover_flux(
    const double *xarea, const double *yarea,
    const double *xvel0, const double *yvel0,
    const double *xvel1, const double *yvel1,
    double *fluxx, double *fluxy, int cells, int row_stride, double dt) {
  const int index = blockIdx.x * blockDim.x + threadIdx.x;
  if (index >= cells) return;
  const double vx = xvel0[index] + xvel0[index + row_stride] +
                    xvel1[index] + xvel1[index + row_stride];
  const double vy = yvel0[index] + yvel0[index + 1] +
                    yvel1[index] + yvel1[index + 1];
  fluxx[index] = 0.25 * dt * xarea[index] * vx;
  fluxy[index] = 0.25 * dt * yarea[index] * vy;
}
