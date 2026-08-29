/* Copyright (c) 2022, NVIDIA CORPORATION. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of NVIDIA CORPORATION nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Adapted by the Gemstone project from NVIDIA CUDA Samples' matrixMul.cu
 * at revision b7c5481c556c3fe98db060207ecaa41a4b9a9abc. The helper-library
 * dependency and benchmark-oriented host code were replaced with deterministic
 * self-checks, and bounds handling was added for partial tiles.
 */
#pragma region case: sample-tiled-matrix-multiply
#pragma region do: {nvcc} --allow-unsupported-compiler -std=c++17 -O2 -MD -MF {dependencies} -o {output} {input}
#pragma region do: {output}
#pragma region requires: pass

#include <cuda_runtime.h>

#include <cmath>
#include <cstddef>
#include <cstdio>
#include <vector>

#define CUDA_REQUIRE(expression)                                                                                                           \
  do {                                                                                                                                     \
    const cudaError_t status = (expression);                                                                                               \
    if (status != cudaSuccess) {                                                                                                           \
      std::fprintf(stderr, "%s failed: %s at line %d\n", #expression, cudaGetErrorString(status), __LINE__);                             \
      return 1;                                                                                                                            \
    }                                                                                                                                      \
  } while (false)

template <int tile>
__global__ void matrix_multiply(float *output, const float *left, const float *right, int rows, int inner, int columns) {
  __shared__ float left_tile[tile][tile];
  __shared__ float right_tile[tile][tile];

  const int row = static_cast<int>(blockIdx.y) * tile + static_cast<int>(threadIdx.y);
  const int column = static_cast<int>(blockIdx.x) * tile + static_cast<int>(threadIdx.x);
  float sum = 0.0F;

  for (int base = 0; base < inner; base += tile) {
    const int left_column = base + static_cast<int>(threadIdx.x);
    const int right_row = base + static_cast<int>(threadIdx.y);
    left_tile[threadIdx.y][threadIdx.x] = row < rows && left_column < inner ? left[row * inner + left_column] : 0.0F;
    right_tile[threadIdx.y][threadIdx.x] = right_row < inner && column < columns ? right[right_row * columns + column] : 0.0F;
    __syncthreads();

#pragma unroll
    for (int k = 0; k < tile; ++k) sum += left_tile[threadIdx.y][k] * right_tile[k][threadIdx.x];
    __syncthreads();
  }

  if (row < rows && column < columns) output[row * columns + column] = sum;
}

int main() {
  constexpr int rows = 19;
  constexpr int inner = 23;
  constexpr int columns = 17;
  constexpr int tile = 8;
  std::vector<float> left(rows * inner);
  std::vector<float> right(inner * columns);
  std::vector<float> actual(rows * columns, -1.0F);
  std::vector<float> expected(rows * columns, 0.0F);

  for (std::size_t i = 0; i < left.size(); ++i) left[i] = static_cast<float>(static_cast<int>(i % 13) - 6) / 4.0F;
  for (std::size_t i = 0; i < right.size(); ++i) right[i] = static_cast<float>(static_cast<int>(i % 11) - 5) / 8.0F;
  for (int row = 0; row < rows; ++row)
    for (int column = 0; column < columns; ++column)
      for (int k = 0; k < inner; ++k)
        expected[row * columns + column] += left[row * inner + k] * right[k * columns + column];

  float *device_left = nullptr;
  float *device_right = nullptr;
  float *device_output = nullptr;
  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device_left), left.size() * sizeof(float)));
  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device_right), right.size() * sizeof(float)));
  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device_output), actual.size() * sizeof(float)));
  CUDA_REQUIRE(cudaMemcpy(device_left, left.data(), left.size() * sizeof(float), cudaMemcpyHostToDevice));
  CUDA_REQUIRE(cudaMemcpy(device_right, right.data(), right.size() * sizeof(float), cudaMemcpyHostToDevice));

  const dim3 block(tile, tile);
  const dim3 grid((columns + tile - 1) / tile, (rows + tile - 1) / tile);
  matrix_multiply<tile><<<grid, block>>>(device_output, device_left, device_right, rows, inner, columns);
  CUDA_REQUIRE(cudaGetLastError());
  CUDA_REQUIRE(cudaDeviceSynchronize());
  CUDA_REQUIRE(cudaMemcpy(actual.data(), device_output, actual.size() * sizeof(float), cudaMemcpyDeviceToHost));
  CUDA_REQUIRE(cudaFree(device_output));
  CUDA_REQUIRE(cudaFree(device_right));
  CUDA_REQUIRE(cudaFree(device_left));

  for (std::size_t i = 0; i < actual.size(); ++i) {
    if (std::fabs(actual[i] - expected[i]) > 1.0e-5F) {
      std::fprintf(stderr, "element %zu: expected %.8f, got %.8f\n", i, expected[i], actual[i]);
      return 1;
    }
  }

  std::puts("pass");
  return 0;
}
