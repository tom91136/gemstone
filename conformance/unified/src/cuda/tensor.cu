#include <stdexcept>
#include <string>
#include <vector>

#include <mma.h>

#include <catch2/catch_test_macros.hpp>
#include <cuda_fp16.h>
#include <cuda_runtime.h>

#include "test_support.hpp"

namespace {

void checkTensor(cudaError_t result) {
  if (result != cudaSuccess) throw std::runtime_error(std::string("CUDA tensor test: ") + cudaGetErrorString(result));
}

__global__ void tensorGemm(const half *a, const half *b, float *c) {
#if __CUDA_ARCH__ >= 700
  using namespace nvcuda;
  const size_t tile = (blockIdx.x * 2) + (threadIdx.x / 32);
  wmma::fragment<wmma::matrix_a, 16, 16, 16, half, wmma::row_major> af;
  wmma::fragment<wmma::matrix_b, 16, 16, 16, half, wmma::col_major> bf;
  wmma::fragment<wmma::accumulator, 16, 16, 16, float> cf;
  wmma::load_matrix_sync(af, a + (tile * 512), 32);
  wmma::load_matrix_sync(bf, b + (tile * 512), 32);
  wmma::load_matrix_sync(cf, c + (tile * 512), 32, wmma::mem_row_major);
  wmma::mma_sync(cf, af, bf, cf);
  for (float &i : cf.x)
    i *= 2.0F;
  wmma::store_matrix_sync(c + (tile * 512), cf, 32, wmma::mem_row_major);
#endif
}

} // namespace

TEST_CASE("runtime/cuda/tensor-wmma-gemm", "[unified][runtime][tensor][compact]") {
  cudaDeviceProp properties{};
  checkTensor(cudaGetDeviceProperties(&properties, 0));
  if (properties.major < 7) SKIP("WMMA requires compute capability 7.0");

  constexpr int tiles = 4;
  constexpr int stride = 32;
  constexpr int tileSize = 16 * stride;
  std::vector<half> a(static_cast<size_t>(tiles) * tileSize, __float2half(0.0F));
  std::vector<half> b(static_cast<size_t>(tiles) * tileSize, __float2half(0.0F));
  std::vector<float> c(static_cast<size_t>(tiles) * tileSize, -1234.0F);
  for (int tile = 0; tile < tiles; ++tile)
    for (int row = 0; row < 16; ++row)
      for (int col = 0; col < 16; ++col) {
        a[(tile * tileSize) + (row * stride) + col] = __float2half(static_cast<float>(((row + (3 * col) + tile) % 7) - 3));
        b[(tile * tileSize) + (col * stride) + row] = __float2half(static_cast<float>((((2 * row) + col) % 5) - 2));
        c[(tile * tileSize) + (row * stride) + col] = static_cast<float>(row - col);
      }
  auto expected = c;
  for (int tile = 0; tile < tiles; ++tile)
    for (int row = 0; row < 16; ++row)
      for (int col = 0; col < 16; ++col) {
        auto &result = expected[(tile * tileSize) + (row * stride) + col];
        for (int k = 0; k < 16; ++k)
          result += __half2float(a[(tile * tileSize) + (row * stride) + k]) * __half2float(b[(tile * tileSize) + (col * stride) + k]);
        result *= 2.0F;
      }

  unified::test_runtime::Cleanup cleanup;
  half *da = nullptr;
  half *db = nullptr;
  float *dc = nullptr;
  checkTensor(cudaMalloc(reinterpret_cast<void **>(&da), a.size() * sizeof(half)));
  cleanup.add([=] { cudaFree(da); });
  checkTensor(cudaMalloc(reinterpret_cast<void **>(&db), b.size() * sizeof(half)));
  cleanup.add([=] { cudaFree(db); });
  checkTensor(cudaMalloc(reinterpret_cast<void **>(&dc), c.size() * sizeof(float)));
  cleanup.add([=] { cudaFree(dc); });
  checkTensor(cudaMemcpy(da, a.data(), a.size() * sizeof(half), cudaMemcpyHostToDevice));
  checkTensor(cudaMemcpy(db, b.data(), b.size() * sizeof(half), cudaMemcpyHostToDevice));
  checkTensor(cudaMemcpy(dc, c.data(), c.size() * sizeof(float), cudaMemcpyHostToDevice));
  tensorGemm<<<2, 64>>>(da, db, dc);
  checkTensor(cudaGetLastError());
  checkTensor(cudaDeviceSynchronize());
  checkTensor(cudaMemcpy(c.data(), dc, c.size() * sizeof(float), cudaMemcpyDeviceToHost));
  REQUIRE(c == expected);
}
