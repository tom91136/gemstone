/*
 * Adapted from GPUOcelot's ocelot/src/cuda/test/TestCudaMalloc.cu.
 * Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech.
 * SPDX-License-Identifier: BSD-3-Clause
 */
#pragma region case: runtime-malloc-copy
#pragma region targets: cuda,gemstone
#pragma region do: {nvcc} --allow-unsupported-compiler -std=c++17 -O2 -o {output} {input} {cuda_runtime_link}
#pragma region do: {output}
#pragma region requires: pass

#include <array>
#include <cstddef>
#include <cstdio>

#include <cuda_runtime.h>

#define CUDA_REQUIRE(expression)                                                                                                           \
  do {                                                                                                                                     \
    const cudaError_t status = (expression);                                                                                               \
    if (status != cudaSuccess) {                                                                                                           \
      std::fprintf(stderr, "%s failed: %s at line %d\n", #expression, cudaGetErrorString(status), __LINE__);                               \
      return 1;                                                                                                                            \
    }                                                                                                                                      \
  } while (false)

int main() {
  constexpr std::size_t size = 32768;
  std::array<float, size> source{};
  std::array<float, size> result{};
  for (std::size_t i = 0; i < size; ++i)
    source[i] = static_cast<float>(i % 128) / 128.0f;

  float *device = nullptr;
  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device), source.size() * sizeof(float)));
  CUDA_REQUIRE(cudaMemcpy(device, source.data(), source.size() * sizeof(float), cudaMemcpyHostToDevice));
  CUDA_REQUIRE(cudaMemcpy(result.data(), device, result.size() * sizeof(float), cudaMemcpyDeviceToHost));
  CUDA_REQUIRE(cudaFree(device));

  for (std::size_t i = 0; i < size; ++i) {
    if (result[i] != source[i]) {
      std::fprintf(stderr, "element %zu: expected %f, got %f\n", i, source[i], result[i]);
      return 1;
    }
  }
  std::puts("pass");
  return 0;
}
