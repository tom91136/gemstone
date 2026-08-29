/*
 * Adapted from GPUOcelot's ocelot/src/cuda/test/TestIndirectFunctionCall.cu.
 * Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech.
 * SPDX-License-Identifier: BSD-3-Clause
 */
#pragma region case: runtime-indirect-function
#pragma region do: {nvcc} --allow-unsupported-compiler -std=c++17 -O2 -o {output} {input}
#pragma region do: {output}
#pragma region requires: pass

#include <array>
#include <cstddef>
#include <cstdio>

#include <cuda_runtime.h>

__device__ __noinline__ int twice(int value) { return value * 2; }
__device__ __noinline__ int thrice(int value) { return value * 3; }
__device__ __noinline__ int four_times(int value) { return value * 4; }
__device__ __noinline__ int five_times(int value) { return value * 5; }

__global__ void apply_indirect(int *values, std::size_t size) {
  const std::size_t i = threadIdx.x + static_cast<std::size_t>(blockIdx.x) * blockDim.x;
  if (i >= size) return;

  int (*function)(int) = nullptr;
  switch (i & 3) {
    case 0: function = &twice; break;
    case 1: function = &thrice; break;
    case 2: function = &four_times; break;
    default: function = &five_times; break;
  }
  values[i] = function(static_cast<int>(i));
}

#define CUDA_REQUIRE(expression)                                                                                                           \
  do {                                                                                                                                     \
    const cudaError_t status = (expression);                                                                                               \
    if (status != cudaSuccess) {                                                                                                           \
      std::fprintf(stderr, "%s failed: %s at line %d\n", #expression, cudaGetErrorString(status), __LINE__);                               \
      return 1;                                                                                                                            \
    }                                                                                                                                      \
  } while (false)

int main() {
  constexpr std::size_t size = 257;
  std::array<int, size> result{};
  int *device = nullptr;
  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device), result.size() * sizeof(int)));
  apply_indirect<<<3, 128>>>(device, size);
  CUDA_REQUIRE(cudaGetLastError());
  CUDA_REQUIRE(cudaDeviceSynchronize());
  CUDA_REQUIRE(cudaMemcpy(result.data(), device, result.size() * sizeof(int), cudaMemcpyDeviceToHost));
  CUDA_REQUIRE(cudaFree(device));

  for (std::size_t i = 0; i < size; ++i) {
    const int expected = static_cast<int>(i) * static_cast<int>((i & 3) + 2);
    if (result[i] != expected) {
      std::fprintf(stderr, "element %zu: expected %d, got %d\n", i, expected, result[i]);
      return 1;
    }
  }
  std::puts("pass");
  return 0;
}
