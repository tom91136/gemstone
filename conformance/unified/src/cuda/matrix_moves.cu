#include <cstdint>
#include <random>
#include <vector>

#include "cuda/mma_support.cuh"
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <cuda_bf16.h>
#include <cuda_runtime_api.h>

#include "test_support.hpp"

using namespace aspartame;

// ldmatrix, stmatrix and movmatrix in their documented lane layouts, and a CuTe-style tile that feeds mma from
// ldmatrix.
namespace {

// Rows of 8 16-bit elements, each matrix's rows placed out of order 16 bytes apart in a shared tile.
constexpr int Rows = 32;
__host__ __device__ inline int slot(int row) { return ((row * 5) + 3) % Rows; }

template <int Count, bool Transpose> __global__ void load(const uint16_t *source, uint32_t *destination) {
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 750
  __shared__ alignas(16) uint16_t tile[Rows * 8];
  const unsigned lane = threadIdx.x % 32;
  for (unsigned i = lane; i < Rows * 8; i += 32)
    tile[i] = source[i];
  __syncwarp();
  const unsigned row = lane % (8 * Count);
  const auto address = static_cast<unsigned>(__cvta_generic_to_shared(&tile[static_cast<ptrdiff_t>(slot(row)) * 8]));
  uint32_t r[4] = {};
  if constexpr (Count == 1 && !Transpose) asm volatile("ldmatrix.sync.aligned.m8n8.x1.shared.b16 {%0}, [%1];" : "=r"(r[0]) : "r"(address));
  else if constexpr (Count == 1) asm volatile("ldmatrix.sync.aligned.m8n8.x1.trans.shared.b16 {%0}, [%1];" : "=r"(r[0]) : "r"(address));
  else if constexpr (Count == 2 && !Transpose)
    asm volatile("ldmatrix.sync.aligned.m8n8.x2.shared::cta.b16 {%0,%1}, [%2];" : "=r"(r[0]), "=r"(r[1]) : "r"(address));
  else if constexpr (Count == 2)
    asm volatile("ldmatrix.sync.aligned.m8n8.x2.trans.shared::cta.b16 {%0,%1}, [%2];" : "=r"(r[0]), "=r"(r[1]) : "r"(address));
  else if constexpr (!Transpose)
    asm volatile("ldmatrix.sync.aligned.m8n8.x4.shared.b16 {%0,%1,%2,%3}, [%4];"
                 : "=r"(r[0]), "=r"(r[1]), "=r"(r[2]), "=r"(r[3])
                 : "r"(address));
  else
    asm volatile("ldmatrix.sync.aligned.m8n8.x4.trans.shared.b16 {%0,%1,%2,%3}, [%4];"
                 : "=r"(r[0]), "=r"(r[1]), "=r"(r[2]), "=r"(r[3])
                 : "r"(address));
  for (int i = 0; i < Count; ++i)
    destination[(lane * Count) + i] = r[i];
#else
  __trap();
#endif
}

// The bits a lane's register holds: the element at row lane / 4 and columns lane % 4 * 2 and the next of each
// matrix, read across the stored rows when transposed.
uint32_t expectedLoad(const std::vector<uint16_t> &tile, int lane, int matrix, bool transpose) {
  uint32_t bits = 0;
  for (int part = 0; part < 2; ++part) {
    const int row = lane / 4;
    const int column = (lane % 4 * 2) + part;
    const int stored = transpose ? column : row;
    const int element = transpose ? row : column;
    bits |= static_cast<uint32_t>(tile[(slot((matrix * 8) + stored) * 8) + element]) << (16 * part);
  }
  return bits;
}

template <int Count, bool Transpose> void checkLoad(const std::vector<uint16_t> &tile) {
  DeviceBuffer<uint16_t> source(tile);
  DeviceBuffer<uint32_t> result(std::vector<uint32_t>(size_t{32} * Count));
  load<Count, Transpose><<<1, 32>>>(source.data, result.data);
  finish("ldmatrix");
  const auto expected =
      iota(0, 32 * Count) | map([&](int index) { return expectedLoad(tile, index / Count, index % Count, Transpose); }) | to_vector();
  expectEqual(result.download(), expected, fmt::format("ldmatrix x{}{}", Count, Transpose ? " trans" : ""));
}

} // namespace

TEST_CASE("runtime/cuda/matrix-moves/ldmatrix", "[unified][runtime][tensor][compact]") {
  if (capability() < 75) SKIP("ldmatrix requires compute capability 7.5");
  const auto tile =
      iota(size_t{0}, size_t{Rows} * 8) | map([](size_t i) { return static_cast<uint16_t>(0x1000 + (i * 37)); }) | to_vector();
  checkLoad<1, false>(tile);
  checkLoad<1, true>(tile);
  checkLoad<2, false>(tile);
  checkLoad<2, true>(tile);
  checkLoad<4, false>(tile);
  checkLoad<4, true>(tile);
}

namespace {
__global__ void transpose(const uint32_t *source, uint32_t *destination) {
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 750
  const unsigned lane = threadIdx.x % 32;
  uint32_t result = 0;
  asm volatile("movmatrix.sync.aligned.m8n8.trans.b16 %0, %1;" : "=r"(result) : "r"(source[lane]));
  destination[lane] = result;
#else
  __trap();
#endif
}

} // namespace

TEST_CASE("runtime/cuda/matrix-moves/movmatrix", "[unified][runtime][tensor][compact]") {
  if (capability() < 75) SKIP("movmatrix requires compute capability 7.5");
  // lane l holds row l / 4, columns l % 4 * 2 and the next; the transpose holds column l / 4, rows l % 4 * 2 and the next
  auto element = [](int row, int column) { return static_cast<uint32_t>((0x100 * row) + column + 0x4000); };
  const auto source = iota(0, 32)
                      | map([&](int lane) { return element(lane / 4, lane % 4 * 2) | element(lane / 4, (lane % 4 * 2) + 1) << 16; })
                      | to_vector();
  const auto expected = iota(0, 32)
                        | map([&](int lane) { return element(lane % 4 * 2, lane / 4) | element((lane % 4 * 2) + 1, lane / 4) << 16; })
                        | to_vector();
  DeviceBuffer<uint32_t> input(source);
  DeviceBuffer<uint32_t> output(std::vector<uint32_t>(32));
  transpose<<<1, 32>>>(input.data, output.data);
  finish("movmatrix");
  expectEqual(output.download(), expected, "movmatrix");
}

namespace {
template <int Count, bool Transpose> __global__ void store(const uint32_t *source, uint16_t *destination) {
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 900
  __shared__ alignas(16) uint16_t tile[Rows * 8];
  const unsigned lane = threadIdx.x % 32;
  for (unsigned i = lane; i < Rows * 8; i += 32)
    tile[i] = 0;
  __syncwarp();
  const unsigned row = lane % (8 * Count);
  const auto address = static_cast<unsigned>(__cvta_generic_to_shared(&tile[static_cast<ptrdiff_t>(slot(row)) * 8]));
  const uint32_t *r = source + (static_cast<size_t>(lane) * Count);
  if constexpr (Count == 1 && !Transpose) asm volatile("stmatrix.sync.aligned.m8n8.x1.shared.b16 [%0], {%1};" ::"r"(address), "r"(r[0]));
  else if constexpr (Count == 2 && Transpose)
    asm volatile("stmatrix.sync.aligned.m8n8.x2.trans.shared::cta.b16 [%0], {%1,%2};" ::"r"(address), "r"(r[0]), "r"(r[1]));
  else
    asm volatile("stmatrix.sync.aligned.m8n8.x4.shared.b16 [%0], {%1,%2,%3,%4};" ::"r"(address), "r"(r[0]), "r"(r[1]), "r"(r[2]),
                 "r"(r[3]));
  __syncwarp();
  for (unsigned i = lane; i < Rows * 8; i += 32)
    destination[i] = tile[i];
#else
  __trap();
#endif
}

template <int Count, bool Transpose> void checkStore() {
  const auto registers = iota(size_t{0}, size_t{32} * Count)
                         | map([](size_t i) { return static_cast<uint32_t>((0x10001 * (i + 1)) + (i << 9)); }) | to_vector();
  std::vector<uint16_t> expected(size_t{Rows} * 8, 0);
  for (int lane = 0; lane < 32; ++lane)
    for (int matrix = 0; matrix < Count; ++matrix)
      for (int part = 0; part < 2; ++part) {
        const int row = lane / 4;
        const int column = (lane % 4 * 2) + part;
        const int stored = Transpose ? column : row;
        const int element = Transpose ? row : column;
        expected[(slot((matrix * 8) + stored) * 8) + element] = static_cast<uint16_t>(registers[(lane * Count) + matrix] >> (16 * part));
      }
  DeviceBuffer<uint32_t> source(registers);
  DeviceBuffer<uint16_t> result(std::vector<uint16_t>(size_t{Rows} * 8));
  store<Count, Transpose><<<1, 32>>>(source.data, result.data);
  finish("stmatrix");
  expectEqual(result.download(), expected, fmt::format("stmatrix x{}{}", Count, Transpose ? " trans" : ""));
}

} // namespace

TEST_CASE("runtime/cuda/matrix-moves/stmatrix", "[unified][runtime][tensor][compact]") {
  if (capability() < 90) SKIP("stmatrix requires compute capability 9.0");
  checkStore<1, false>();
  checkStore<2, true>();
  checkStore<4, false>();
}

namespace {
// C (64 x 32, f32) += A (64 x 64, bf16, row-major) * B (64 x 32, bf16, row-major) with four warps, each staging its
// operands through shared memory and taking every fragment from ldmatrix.
__global__ void gemm(const __nv_bfloat16 *a, const __nv_bfloat16 *b, float *c) {
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 800
  constexpr int m = 64;
  constexpr int n = 32;
  constexpr int k = 64;
  __shared__ alignas(16) __nv_bfloat16 sa[m * k];
  __shared__ alignas(16) __nv_bfloat16 sb[k * n];
  for (unsigned i = threadIdx.x; i < m * k; i += blockDim.x)
    sa[i] = a[i];
  for (unsigned i = threadIdx.x; i < k * n; i += blockDim.x)
    sb[i] = b[i];
  __syncthreads();
  const unsigned warp = threadIdx.x / 32;
  const unsigned lane = threadIdx.x % 32;
  for (int tile = 0; tile < n / 8; ++tile) {
    float accumulator[4];
    for (int i = 0; i < 4; ++i)
      accumulator[i] = c[(((warp * 16) + (lane / 4) + (8 * (i / 2))) * n) + (tile * 8) + (lane % 4 * 2) + (i % 2)];
    for (int step = 0; step < k / 16; ++step) {
      uint32_t fa[4];
      uint32_t fb[2];
      const auto rowA =
          static_cast<unsigned>(__cvta_generic_to_shared(&sa[(((warp * 16) + (lane % 16)) * k) + (step * 16) + (lane / 16 * 8)]));
      asm volatile("ldmatrix.sync.aligned.m8n8.x4.shared.b16 {%0,%1,%2,%3}, [%4];"
                   : "=r"(fa[0]), "=r"(fa[1]), "=r"(fa[2]), "=r"(fa[3])
                   : "r"(rowA));
      const auto rowB = static_cast<unsigned>(__cvta_generic_to_shared(&sb[(((step * 16) + (lane % 16)) * n) + (tile * 8)]));
      asm volatile("ldmatrix.sync.aligned.m8n8.x2.trans.shared.b16 {%0,%1}, [%2];" : "=r"(fb[0]), "=r"(fb[1]) : "r"(rowB));
      asm volatile("mma.sync.aligned.m16n8k16.row.col.f32.bf16.bf16.f32 {%0,%1,%2,%3}, {%4,%5,%6,%7}, {%8,%9}, {%0,%1,%2,%3};"
                   : "+f"(accumulator[0]), "+f"(accumulator[1]), "+f"(accumulator[2]), "+f"(accumulator[3])
                   : "r"(fa[0]), "r"(fa[1]), "r"(fa[2]), "r"(fa[3]), "r"(fb[0]), "r"(fb[1]));
    }
    for (int i = 0; i < 4; ++i)
      c[(((warp * 16) + (lane / 4) + (8 * (i / 2))) * n) + (tile * 8) + (lane % 4 * 2) + (i % 2)] = accumulator[i];
  }
#else
  __trap();
#endif
}

} // namespace

TEST_CASE("runtime/cuda/matrix-moves/ldmatrix-mma-gemm", "[unified][runtime][tensor][compact]") {
  if (capability() < 80) SKIP("bf16 mma requires compute capability 8.0");
  constexpr int m = 64;
  constexpr int n = 32;
  constexpr int k = 64;
  std::mt19937 random(0x6c646dU);
  std::uniform_int_distribution<int> pick(-8, 8);
  std::vector<float> a(static_cast<size_t>(m) * k);
  std::vector<float> b(static_cast<size_t>(k) * n);
  std::vector<float> c(static_cast<size_t>(m) * n);
  for (auto &v : a)
    v = static_cast<float>(pick(random)) * 0.5F;
  for (auto &v : b)
    v = static_cast<float>(pick(random)) * 0.5F;
  for (auto &v : c)
    v = static_cast<float>(pick(random) * 4);
  const auto ha = a ^ map([](float v) { return __float2bfloat16(v); });
  const auto hb = b ^ map([](float v) { return __float2bfloat16(v); });
  std::vector<float> expected(c);
  for (int row = 0; row < m; ++row)
    for (int column = 0; column < n; ++column)
      for (int inner = 0; inner < k; ++inner)
        expected[(row * n) + column] += a[(row * k) + inner] * b[(inner * n) + column];
  DeviceBuffer<__nv_bfloat16> da(ha);
  DeviceBuffer<__nv_bfloat16> db(hb);
  DeviceBuffer<float> dc(c);
  gemm<<<1, 128>>>(da.data, db.data, dc.data);
  finish("ldmatrix gemm");
  expectEqual(dc.download() ^ map(floatBits), expected ^ map(floatBits), "ldmatrix gemm");
}
