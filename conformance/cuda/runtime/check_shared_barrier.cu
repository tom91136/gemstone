/*
 * Adapted from GPUOcelot's ocelot/src/cuda/test/TestCudaSequence.cu.
 * Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech.
 * SPDX-License-Identifier: BSD-3-Clause
 */
#pragma region case: runtime-shared-barrier
#pragma region do: {nvcc} --allow-unsupported-compiler -std=c++17 -O2 -o {output} {input}
#pragma region do: {output}
#pragma region requires: pass

#include <array>
#include <cstddef>
#include <cstdio>

#include <cuda_runtime.h>

__global__ void exchange_neighbours(int *values, std::size_t size) {
  __shared__ int shared[128];
  const std::size_t i = threadIdx.x + static_cast<std::size_t>(blockIdx.x) * blockDim.x;
  if (i < size) shared[threadIdx.x] = values[i];
  __syncthreads();
  if (i < size) values[i] = shared[threadIdx.x ^ 1];
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
  constexpr std::size_t size = 256;
  std::array<int, size> values{};
  for (std::size_t i = 0; i < size; ++i)
    values[i] = static_cast<int>(i * 3 + 1);

  int *device = nullptr;
  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device), values.size() * sizeof(int)));
  CUDA_REQUIRE(cudaMemcpy(device, values.data(), values.size() * sizeof(int), cudaMemcpyHostToDevice));
  exchange_neighbours<<<2, 128>>>(device, size);
  CUDA_REQUIRE(cudaGetLastError());
  CUDA_REQUIRE(cudaDeviceSynchronize());
  CUDA_REQUIRE(cudaMemcpy(values.data(), device, values.size() * sizeof(int), cudaMemcpyDeviceToHost));
  CUDA_REQUIRE(cudaFree(device));

  for (std::size_t i = 0; i < size; ++i) {
    const std::size_t neighbour = (i / 128) * 128 + ((i % 128) ^ 1);
    const int expected = static_cast<int>(neighbour * 3 + 1);
    if (values[i] != expected) {
      std::fprintf(stderr, "element %zu: expected %d, got %d\n", i, expected, values[i]);
      return 1;
    }
  }
  std::puts("pass");
  return 0;
}
