/*
 * Adapted from GPUOcelot's ocelot/src/cuda/test/TestCudaSequence.cu.
 * Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech.
 * SPDX-License-Identifier: BSD-3-Clause
 */
#pragma region case: runtime-sequence
#pragma region targets: cuda
#pragma region do: {nvcc} --allow-unsupported-compiler -std=c++17 -O2 -o {output} {input}
#pragma region do: {output}
#pragma region requires: pass

#include <array>
#include <cstddef>
#include <cstdio>

#include <cuda_runtime.h>

__global__ void sequence(int *values, std::size_t size) {
  const std::size_t i = threadIdx.x + static_cast<std::size_t>(blockIdx.x) * blockDim.x;
  if (i < size) values[i] = static_cast<int>(i * 2);
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
  result.fill(-1);

  int *device = nullptr;
  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device), result.size() * sizeof(int)));
  CUDA_REQUIRE(cudaMemcpy(device, result.data(), result.size() * sizeof(int), cudaMemcpyHostToDevice));
  sequence<<<3, 128>>>(device, size);
  CUDA_REQUIRE(cudaGetLastError());
  CUDA_REQUIRE(cudaDeviceSynchronize());
  CUDA_REQUIRE(cudaMemcpy(result.data(), device, result.size() * sizeof(int), cudaMemcpyDeviceToHost));
  CUDA_REQUIRE(cudaFree(device));

  for (std::size_t i = 0; i < size; ++i) {
    const int expected = static_cast<int>(i * 2);
    if (result[i] != expected) {
      std::fprintf(stderr, "element %zu: expected %d, got %d\n", i, expected, result[i]);
      return 1;
    }
  }
  std::puts("pass");
  return 0;
}
