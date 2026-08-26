/*
 * Adapted from GPUOcelot's ocelot/src/cuda/test/memory/arrays.cu.
 * Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech.
 * SPDX-License-Identifier: BSD-3-Clause
 */
#pragma region case: runtime-array-copy
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
  constexpr std::size_t width = 127;
  constexpr std::size_t height = 65;
  constexpr std::size_t size = width * height;
  std::array<float, size> source{};
  std::array<float, size> result{};
  for (std::size_t i = 0; i < size; ++i)
    source[i] = static_cast<float>((i * 29 + 7) % 1024) / 17.0f;

  const cudaChannelFormatDesc channel = cudaCreateChannelDesc<float>();
  cudaArray_t array = nullptr;
  CUDA_REQUIRE(cudaMallocArray(&array, &channel, width, height));
  CUDA_REQUIRE(
      cudaMemcpy2DToArray(array, 0, 0, source.data(), width * sizeof(float), width * sizeof(float), height, cudaMemcpyHostToDevice));
  CUDA_REQUIRE(
      cudaMemcpy2DFromArray(result.data(), width * sizeof(float), array, 0, 0, width * sizeof(float), height, cudaMemcpyDeviceToHost));
  CUDA_REQUIRE(cudaFreeArray(array));

  if (result != source) {
    for (std::size_t i = 0; i < size; ++i) {
      if (result[i] != source[i]) {
        std::fprintf(stderr, "element %zu: expected %f, got %f\n", i, source[i], result[i]);
        break;
      }
    }
    return 1;
  }
  std::puts("pass");
  return 0;
}
