#include <stdexcept>
#include <string>
#include <vector>

#include <mma.h>

#include <catch2/catch_test_macros.hpp>
#include <cuda_fp16.h>
#include <cuda_runtime.h>

#include "test_support.hpp"

namespace cuda_tensor {

void check_tensor(cudaError_t result) {
  if (result != cudaSuccess) throw std::runtime_error(std::string("CUDA tensor test: ") + cudaGetErrorString(result));
}

__global__ void tensor_gemm(const half *a, const half *b, float *c) {
#if __CUDA_ARCH__ >= 700
  using namespace nvcuda;
  const unsigned tile = blockIdx.x * 2 + threadIdx.x / 32;
  wmma::fragment<wmma::matrix_a, 16, 16, 16, half, wmma::row_major> af;
  wmma::fragment<wmma::matrix_b, 16, 16, 16, half, wmma::col_major> bf;
  wmma::fragment<wmma::accumulator, 16, 16, 16, float> cf;
  wmma::load_matrix_sync(af, a + tile * 512, 32);
  wmma::load_matrix_sync(bf, b + tile * 512, 32);
  wmma::load_matrix_sync(cf, c + tile * 512, 32, wmma::mem_row_major);
  wmma::mma_sync(cf, af, bf, cf);
  for (int i = 0; i < cf.num_elements; ++i)
    cf.x[i] *= 2.0f;
  wmma::store_matrix_sync(c + tile * 512, cf, 32, wmma::mem_row_major);
#endif
}

TEST_CASE("runtime/cuda/tensor-wmma-gemm", "[unified][runtime][tensor][compact]") {
  cudaDeviceProp properties{};
  check_tensor(cudaGetDeviceProperties(&properties, 0));
  if (properties.major < 7) SKIP("WMMA requires compute capability 7.0");

  constexpr int tiles = 4;
  constexpr int stride = 32;
  constexpr int tile_size = 16 * stride;
  std::vector<half> a(tiles * tile_size, __float2half(0.0f));
  std::vector<half> b(tiles * tile_size, __float2half(0.0f));
  std::vector<float> c(tiles * tile_size, -1234.0f);
  for (int tile = 0; tile < tiles; ++tile)
    for (int row = 0; row < 16; ++row)
      for (int col = 0; col < 16; ++col) {
        a[tile * tile_size + row * stride + col] = __float2half(static_cast<float>((row + 3 * col + tile) % 7 - 3));
        b[tile * tile_size + col * stride + row] = __float2half(static_cast<float>((2 * row + col) % 5 - 2));
        c[tile * tile_size + row * stride + col] = static_cast<float>(row - col);
      }
  auto expected = c;
  for (int tile = 0; tile < tiles; ++tile)
    for (int row = 0; row < 16; ++row)
      for (int col = 0; col < 16; ++col) {
        auto &result = expected[tile * tile_size + row * stride + col];
        for (int k = 0; k < 16; ++k)
          result += __half2float(a[tile * tile_size + row * stride + k]) * __half2float(b[tile * tile_size + col * stride + k]);
        result *= 2.0f;
      }

  unified::test_runtime::Cleanup cleanup;
  half *da = nullptr;
  half *db = nullptr;
  float *dc = nullptr;
  check_tensor(cudaMalloc(reinterpret_cast<void **>(&da), a.size() * sizeof(half)));
  cleanup.add([=] { cudaFree(da); });
  check_tensor(cudaMalloc(reinterpret_cast<void **>(&db), b.size() * sizeof(half)));
  cleanup.add([=] { cudaFree(db); });
  check_tensor(cudaMalloc(reinterpret_cast<void **>(&dc), c.size() * sizeof(float)));
  cleanup.add([=] { cudaFree(dc); });
  check_tensor(cudaMemcpy(da, a.data(), a.size() * sizeof(half), cudaMemcpyHostToDevice));
  check_tensor(cudaMemcpy(db, b.data(), b.size() * sizeof(half), cudaMemcpyHostToDevice));
  check_tensor(cudaMemcpy(dc, c.data(), c.size() * sizeof(float), cudaMemcpyHostToDevice));
  tensor_gemm<<<2, 64>>>(da, db, dc);
  check_tensor(cudaGetLastError());
  check_tensor(cudaDeviceSynchronize());
  check_tensor(cudaMemcpy(c.data(), dc, c.size() * sizeof(float), cudaMemcpyDeviceToHost));
  REQUIRE(c == expected);
}

} // namespace cuda_tensor
