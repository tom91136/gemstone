/*
 * Adapted from GPUOcelot's ocelot/src/cuda/test/TestFunctionCall.cu.
 * Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech.
 * SPDX-License-Identifier: BSD-3-Clause
 */
#pragma region case: runtime-device-function
#pragma region targets: cuda
#pragma region do: {nvcc} --allow-unsupported-compiler -std=c++17 -O2 -o {output} {input}
#pragma region do: {output}
#pragma region requires: pass

#include <array>
#include <cmath>
#include <cstddef>
#include <cstdio>

#include <cuda_runtime.h>

__device__ __noinline__ float square(float value) { return value * value; }

__global__ void apply_square(float *values, std::size_t size) {
  const std::size_t i = threadIdx.x + static_cast<std::size_t>(blockIdx.x) * blockDim.x;
  if (i < size) values[i] = square(values[i]);
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
  std::array<float, size> values{};
  for (std::size_t i = 0; i < size; ++i)
    values[i] = static_cast<float>(i) * 0.25f;

  float *device = nullptr;
  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device), values.size() * sizeof(float)));
  CUDA_REQUIRE(cudaMemcpy(device, values.data(), values.size() * sizeof(float), cudaMemcpyHostToDevice));
  apply_square<<<3, 128>>>(device, size);
  CUDA_REQUIRE(cudaGetLastError());
  CUDA_REQUIRE(cudaMemcpy(values.data(), device, values.size() * sizeof(float), cudaMemcpyDeviceToHost));
  CUDA_REQUIRE(cudaFree(device));

  for (std::size_t i = 0; i < size; ++i) {
    const float input = static_cast<float>(i) * 0.25f;
    const float expected = input * input;
    if (std::fabs(values[i] - expected) > 0.0001f) {
      std::fprintf(stderr, "element %zu: expected %f, got %f\n", i, expected, values[i]);
      return 1;
    }
  }
  std::puts("pass");
  return 0;
}
