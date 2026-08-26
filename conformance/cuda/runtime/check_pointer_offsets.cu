/*
 * Adapted from GPUOcelot's ocelot/src/cuda/test/TestCudaMalloc.cu.
 * Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech.
 * SPDX-License-Identifier: BSD-3-Clause
 */
#pragma region case: runtime-pointer-offsets
#pragma region targets: cuda
#pragma region do: {nvcc} --allow-unsupported-compiler -std=c++17 -O2 -o {output} {input}
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
  constexpr std::size_t size = 513;
  std::array<int, size> source{};
  for (std::size_t i = 0; i < size; ++i)
    source[i] = static_cast<int>(i * 17 + 3);

  int *device = nullptr;
  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device), source.size() * sizeof(int)));
  CUDA_REQUIRE(cudaMemcpy(device, source.data(), source.size() * sizeof(int), cudaMemcpyHostToDevice));

  for (std::size_t i = 0; i < size; i += 7) {
    int value = -1;
    CUDA_REQUIRE(cudaMemcpy(&value, device + i, sizeof(value), cudaMemcpyDeviceToHost));
    if (value != source[i]) {
      std::fprintf(stderr, "element %zu: expected %d, got %d\n", i, source[i], value);
      return 1;
    }
  }

  constexpr std::size_t offset = 129;
  constexpr std::size_t count = 211;
  std::array<int, count> patch{};
  std::array<int, count> result{};
  for (std::size_t i = 0; i < count; ++i)
    patch[i] = -static_cast<int>(i) - 100;
  CUDA_REQUIRE(cudaMemcpy(device + offset, patch.data(), patch.size() * sizeof(int), cudaMemcpyHostToDevice));
  CUDA_REQUIRE(cudaMemcpy(result.data(), device + offset, result.size() * sizeof(int), cudaMemcpyDeviceToHost));
  CUDA_REQUIRE(cudaFree(device));

  if (result != patch) {
    std::fprintf(stderr, "interior-pointer range round-trip did not match\n");
    return 1;
  }
  std::puts("pass");
  return 0;
}
