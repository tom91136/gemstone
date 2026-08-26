/*
 * Adapted from GPUOcelot's ocelot/src/cuda/test/TestCudaGlobals.cu.
 * Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech.
 * SPDX-License-Identifier: BSD-3-Clause
 */
#pragma region case: runtime-globals
#pragma region targets: cuda
#pragma region do: {nvcc} --allow-unsupported-compiler -std=c++17 -O2 -o {output} {input}
#pragma region do: {output}
#pragma region requires: pass

#include <array>
#include <cmath>
#include <cstddef>
#include <cstdio>

#include <cuda_runtime.h>

__device__ float multiplier;

__global__ void read_global(float *result, std::size_t size) {
  const std::size_t i = threadIdx.x + static_cast<std::size_t>(blockIdx.x) * blockDim.x;
  if (i < size) result[i] = multiplier * static_cast<float>(i);
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
  constexpr std::size_t size = 129;
  constexpr float value = 3.25f;
  std::array<float, size> result{};
  float *device = nullptr;

  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device), result.size() * sizeof(float)));
  CUDA_REQUIRE(cudaMemcpyToSymbol(multiplier, &value, sizeof(value)));
  float round_trip = 0.0f;
  CUDA_REQUIRE(cudaMemcpyFromSymbol(&round_trip, multiplier, sizeof(round_trip)));
  if (round_trip != value) {
    std::fprintf(stderr, "symbol round-trip: expected %f, got %f\n", value, round_trip);
    return 1;
  }

  read_global<<<2, 128>>>(device, size);
  CUDA_REQUIRE(cudaGetLastError());
  CUDA_REQUIRE(cudaMemcpy(result.data(), device, result.size() * sizeof(float), cudaMemcpyDeviceToHost));

  void *symbol_address = nullptr;
  CUDA_REQUIRE(cudaGetSymbolAddress(&symbol_address, multiplier));
  constexpr float replacement = 1.5f;
  CUDA_REQUIRE(cudaMemcpy(symbol_address, &replacement, sizeof(replacement), cudaMemcpyHostToDevice));
  read_global<<<2, 128>>>(device, size);
  CUDA_REQUIRE(cudaGetLastError());
  CUDA_REQUIRE(cudaMemcpy(result.data(), device, result.size() * sizeof(float), cudaMemcpyDeviceToHost));
  CUDA_REQUIRE(cudaFree(device));

  for (std::size_t i = 0; i < size; ++i) {
    const float expected = replacement * static_cast<float>(i);
    if (std::fabs(result[i] - expected) > 0.0001f) {
      std::fprintf(stderr, "element %zu: expected %f, got %f\n", i, expected, result[i]);
      return 1;
    }
  }
  std::puts("pass");
  return 0;
}
