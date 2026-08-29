/*
 * Adapted from GPUOcelot's ocelot/src/cuda/test/TestCudaMalloc.cu.
 * Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech.
 * SPDX-License-Identifier: BSD-3-Clause
 */
#pragma region case: runtime-pitched-copy
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
  constexpr std::size_t width = 125;
  constexpr std::size_t height = 128;
  constexpr std::size_t size = width * height;
  std::array<float, size> source{};
  std::array<float, size> result{};
  for (std::size_t y = 0; y < height; ++y) {
    for (std::size_t x = 0; x < width; ++x)
      source[y * width + x] = static_cast<float>(x % 128) / 64.0f + 2.0f;
  }

  float *first = nullptr;
  float *second = nullptr;
  std::size_t first_pitch = 0;
  std::size_t second_pitch = 0;
  CUDA_REQUIRE(cudaMallocPitch(reinterpret_cast<void **>(&first), &first_pitch, width * sizeof(float), height));
  CUDA_REQUIRE(cudaMallocPitch(reinterpret_cast<void **>(&second), &second_pitch, (width + 1) * sizeof(float), height));
  if (first_pitch < width * sizeof(float) || second_pitch < (width + 1) * sizeof(float)) {
    std::fprintf(stderr, "cudaMallocPitch returned an undersized pitch\n");
    return 1;
  }

  CUDA_REQUIRE(
      cudaMemcpy2D(first, first_pitch, source.data(), width * sizeof(float), width * sizeof(float), height, cudaMemcpyHostToDevice));
  CUDA_REQUIRE(cudaMemcpy2D(second, second_pitch, first, first_pitch, width * sizeof(float), height, cudaMemcpyDeviceToDevice));
  CUDA_REQUIRE(
      cudaMemcpy2D(result.data(), width * sizeof(float), second, second_pitch, width * sizeof(float), height, cudaMemcpyDeviceToHost));
  CUDA_REQUIRE(cudaFree(second));
  CUDA_REQUIRE(cudaFree(first));

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
