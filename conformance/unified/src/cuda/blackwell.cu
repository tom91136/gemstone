#include <cstdint>
#include <cstring>
#include <stdexcept>
#include <vector>

#include "cuda/support.cuh"
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <cuda.h>
#include <cuda_runtime.h>

#include "conformance/device.hpp"

#include "test_support.hpp"

using namespace aspartame;

// tcgen05 tensor memory and the sm_100 tensor copy modes, which only sm_100a code can use: each case skips unless the
// selected device is compute capability 10.0 and runs this binary's sm_100a code.
namespace {

__global__ void probe(int *available) {
#ifdef __CUDA_ARCH_FEAT_SM100_ALL
  *available = 1;
#else
  *available = 0;
#endif
}

// Whether the selected device runs this binary's sm_100a code, as the case's message says when it does not.
bool available() {
  const auto selected = static_cast<int>(conformance::deviceSelection().nativeHandle);
  check(cudaSetDevice(selected), "cudaSetDevice");
  int major = 0;
  int minor = 0;
  check(cudaDeviceGetAttribute(&major, cudaDevAttrComputeCapabilityMajor, selected), "compute capability");
  check(cudaDeviceGetAttribute(&minor, cudaDevAttrComputeCapabilityMinor, selected), "compute capability");
  if (major != 10 || minor != 0) return false;
  Buffer<int> flag(std::vector<int>{0});
  probe<<<1, 1>>>(flag.device);
  check(cudaGetLastError(), "probe launch");
  check(cudaDeviceSynchronize(), "probe");
  return flag.download(1)[0] == 1;
}

#ifdef __CUDA_ARCH_FEAT_SM100_ALL
__device__ uint32_t sharedAddress(const void *pointer) { return static_cast<uint32_t>(__cvta_generic_to_shared(pointer)); }

__device__ void threadSync() {
  asm volatile("tcgen05.fence::before_thread_sync;" ::: "memory");
  __syncthreads();
  asm volatile("tcgen05.fence::after_thread_sync;" ::: "memory");
}
#endif

// Each warp stores eight columns of its 32 lanes with the 32x32b shape, then reads them back with the 16x256b and
// 16x64b shapes, whose registers map to lanes and columns as the PTX ISA data movement figures show.
__global__ void movement([[maybe_unused]] uint32_t *out) {
#ifdef __CUDA_ARCH_FEAT_SM100_ALL
  __shared__ uint32_t slot;
  const uint32_t thread = threadIdx.x;
  const uint32_t warp = thread / 32;
  if (warp == 0) {
    asm volatile("tcgen05.alloc.cta_group::1.sync.aligned.shared::cta.b32 [%0], 32;" ::"r"(sharedAddress(&slot)) : "memory");
    asm volatile("tcgen05.relinquish_alloc_permit.cta_group::1.sync.aligned;" ::: "memory");
  }
  threadSync();
  const uint32_t base = slot + (warp << 21);
  uint32_t v[8];
  for (uint32_t column = 0; column < 8; ++column)
    v[column] = thread * 256 + column;
  asm volatile("tcgen05.st.sync.aligned.32x32b.x8.b32 [%0], {%1, %2, %3, %4, %5, %6, %7, %8};" ::"r"(base), "r"(v[0]), "r"(v[1]), "r"(v[2]),
               "r"(v[3]), "r"(v[4]), "r"(v[5]), "r"(v[6]), "r"(v[7])
               : "memory");
  asm volatile("tcgen05.wait::st.sync.aligned;" ::: "memory");
  uint32_t q[4];
  uint32_t h = 0;
  asm volatile("tcgen05.ld.sync.aligned.16x256b.x1.b32 {%0, %1, %2, %3}, [%4];"
               : "=r"(q[0]), "=r"(q[1]), "=r"(q[2]), "=r"(q[3])
               : "r"(base)
               : "memory");
  asm volatile("tcgen05.ld.sync.aligned.16x64b.x1.b32 {%0}, [%1];" : "=r"(h) : "r"(base + 4) : "memory");
  asm volatile("tcgen05.wait::ld.sync.aligned;" ::: "memory");
  for (int index = 0; index < 4; ++index)
    out[thread * 5 + index] = q[index];
  out[thread * 5 + 4] = h;
  threadSync();
  if (warp == 0) asm volatile("tcgen05.dealloc.cta_group::1.sync.aligned.b32 %0, 32;" ::"r"(slot) : "memory");
#endif
}

} // namespace

TEST_CASE("runtime/cuda/blackwell/tcgen05-load-store-shapes", "[unified][runtime][blackwell][compact]") {
  if (!available()) SKIP("tcgen05 requires sm_100a code on a compute capability 10.0 device");
  Buffer<uint32_t> out(std::vector<uint32_t>(size_t{128} * 5, 0xffffffffU));
  movement<<<1, 128>>>(out.device);
  check(cudaGetLastError(), "movement launch");
  check(cudaDeviceSynchronize(), "movement");
  const auto actual = out.download(size_t{128} * 5);
  for (uint32_t thread = 0; thread < 128; ++thread) {
    const uint32_t quarter = thread / 32 * 32;
    const uint32_t lane = thread % 32;
    CAPTURE(thread);
    for (uint32_t index = 0; index < 4; ++index) {
      const uint32_t row = quarter + (lane / 4) + (8 * (index / 2));
      const uint32_t column = (2 * (lane % 4)) + (index % 2);
      REQUIRE(actual[(thread * 5) + index] == (row * 256) + column);
    }
    const uint32_t row = quarter + (lane / 4) + (8 * (lane % 2));
    REQUIRE(actual[(thread * 5) + 4] == (row * 256) + 4 + (lane / 2 % 2));
  }
}

namespace {
constexpr int M = 128;
constexpr int N = 64;
constexpr int K = 16;
constexpr int TileBytes = 16384;

// A K-major operand row's byte under the 128-byte swizzle: eight 128-byte rows to each 1024-byte atom, their 16-byte
// chunks XORed with the row within the atom.
size_t swizzled(int row, int byte) {
  const size_t offset = (static_cast<size_t>(row % 8) * 128) + (static_cast<size_t>(row / 8) * 1024) + byte;
  return offset ^ (((offset >> 7) & 7) << 4);
}

// One thread issues a 128x64x16 BF16 product into tensor memory from 128-byte swizzled K-major A and B in shared
// memory, commits it to an mbarrier, and every thread reads back its row of D.
__global__ void product([[maybe_unused]] const uint4 *image, [[maybe_unused]] uint64_t adesc, [[maybe_unused]] uint64_t bdesc,
                        [[maybe_unused]] uint32_t idesc, [[maybe_unused]] float *out) {
#ifdef __CUDA_ARCH_FEAT_SM100_ALL
  __shared__ alignas(1024) uint4 tiles[2 * TileBytes / 16];
  __shared__ alignas(8) uint64_t barrier;
  __shared__ uint32_t slot;
  const uint32_t thread = threadIdx.x;
  const uint32_t warp = thread / 32;
  for (uint32_t index = thread; index < 2 * TileBytes / 16; index += blockDim.x)
    tiles[index] = image[index];
  if (warp == 0) {
    asm volatile("tcgen05.alloc.cta_group::1.sync.aligned.shared::cta.b32 [%0], 64;" ::"r"(sharedAddress(&slot)) : "memory");
    asm volatile("tcgen05.relinquish_alloc_permit.cta_group::1.sync.aligned;" ::: "memory");
  }
  if (thread == 0) asm volatile("mbarrier.init.shared::cta.b64 [%0], 1;" ::"r"(sharedAddress(&barrier)) : "memory");
  asm volatile("fence.proxy.async.shared::cta;" ::: "memory");
  asm volatile("fence.mbarrier_init.release.cluster;" ::: "memory");
  threadSync();
  if (thread == 0) {
    const uint64_t start = (sharedAddress(tiles) >> 4) & 0x3fff;
    asm volatile("{\n"
                 " .reg .pred enable;\n"
                 " setp.ne.b32 enable, %5, 0;\n"
                 " tcgen05.mma.cta_group::1.kind::f16 [%0], %1, %2, %3, enable;\n"
                 " tcgen05.commit.cta_group::1.mbarrier::arrive::one.shared::cluster.b64 [%4];\n"
                 "}" ::"r"(slot),
                 "l"(adesc + start), "l"(bdesc + start + TileBytes / 16), "r"(idesc), "r"(sharedAddress(&barrier)), "r"(0u)
                 : "memory");
  }
  uint32_t done = 0;
  while (!done)
    asm volatile("{\n"
                 " .reg .pred ready;\n"
                 " mbarrier.try_wait.parity.shared::cta.b64 ready, [%1], 0;\n"
                 " selp.b32 %0, 1, 0, ready;\n"
                 "}"
                 : "=r"(done)
                 : "r"(sharedAddress(&barrier))
                 : "memory");
  asm volatile("tcgen05.fence::after_thread_sync;" ::: "memory");
  const uint32_t base = slot + (warp << 21);
  for (uint32_t column = 0; column < N; ++column) {
    uint32_t value = 0;
    asm volatile("tcgen05.ld.sync.aligned.32x32b.x1.b32 {%0}, [%1];" : "=r"(value) : "r"(base + column) : "memory");
    asm volatile("tcgen05.wait::ld.sync.aligned;" ::: "memory");
    out[thread * N + column] = __uint_as_float(value);
  }
  threadSync();
  if (warp == 0) asm volatile("tcgen05.dealloc.cta_group::1.sync.aligned.b32 %0, 64;" ::"r"(slot) : "memory");
#endif
}

using unified::support::bfloat16Bits;

} // namespace

TEST_CASE("runtime/cuda/blackwell/tcgen05-mma-bf16", "[unified][runtime][blackwell][compact]") {
  if (!available()) SKIP("tcgen05 requires sm_100a code on a compute capability 10.0 device");
  const auto a = [](int row, int k) { return static_cast<float>((((row * 3) + (k * 5)) % 7) - 3); };
  const auto b = [](int k, int column) { return static_cast<float>((((k * 2) + (column * 7)) % 5) - 2) / 2; };
  std::vector<uint8_t> bytes(size_t{2} * TileBytes, 0);
  const auto put = [&](size_t offset, float value) {
    const uint16_t bits = bfloat16Bits(value);
    std::memcpy(&bytes[offset], &bits, sizeof(bits));
  };
  for (int row = 0; row < M; ++row)
    for (int k = 0; k < K; ++k)
      put(swizzled(row, k * 2), a(row, k));
  for (int column = 0; column < N; ++column)
    for (int k = 0; k < K; ++k)
      put(TileBytes + swizzled(column, k * 2), b(k, column));
  std::vector<uint4> image(2 * TileBytes / 16);
  std::memcpy(image.data(), bytes.data(), bytes.size());
  // leading byte offset 16 and stride byte offset 1024, the sm_100 version bit and the 128-byte swizzle
  const uint64_t descriptor = (uint64_t{16 >> 4} << 16) | (uint64_t{1024 >> 4} << 32) | (uint64_t{1} << 46) | (uint64_t{2} << 61);
  // F32 D, BF16 A and B, both K-major, N and M
  const uint32_t idesc = (1U << 4) | (1U << 7) | (1U << 10) | ((N >> 3) << 17) | ((M >> 4) << 24);
  Buffer<uint4> source(image);
  Buffer<float> out(std::vector<float>(static_cast<size_t>(M) * N, -1.0F));
  product<<<1, 128>>>(source.device, descriptor, descriptor, idesc, out.device);
  check(cudaGetLastError(), "product launch");
  check(cudaDeviceSynchronize(), "product");
  const auto actual = out.download(static_cast<size_t>(M) * N);
  for (int row = 0; row < M; ++row)
    for (int column = 0; column < N; ++column) {
      const auto expected = iota(0, K) | fold_left(0.0F, [&](float sum, int k) { return sum + (a(row, k) * b(k, column)); });
      CAPTURE(row, column);
      REQUIRE(actual[(row * N) + column] == expected);
    }
}

namespace {
using EncodeTiled = CUresult (*)(CUtensorMap *, CUtensorMapDataType, cuuint32_t, void *, const cuuint64_t *, const cuuint64_t *,
                                 const cuuint32_t *, const cuuint32_t *, CUtensorMapInterleave, CUtensorMapSwizzle, CUtensorMapL2promotion,
                                 CUtensorMapFloatOOBfill);

EncodeTiled encodeTiled() {
  void *function = nullptr;
  cudaDriverEntryPointQueryResult status{};
  check(cudaGetDriverEntryPointByVersion("cuTensorMapEncodeTiled", &function, 12000, cudaEnableDefault, &status),
        "cuTensorMapEncodeTiled entry point");
  if (status != cudaDriverEntryPointSuccess || !function) throw std::runtime_error("cuTensorMapEncodeTiled is unavailable");
  return reinterpret_cast<EncodeTiled>(function);
}

// gather4 reads columns 2 to 5 of rows 5, 2, 9 and 17 of a 16-row tensor into four box rows, then scatter4 writes
// them to columns 4 to 7 of rows 1, 3, 4 and 20 of another.
__global__ void gather([[maybe_unused]] const __grid_constant__ CUtensorMap load,
                       [[maybe_unused]] const __grid_constant__ CUtensorMap store, [[maybe_unused]] uint32_t *out) {
#ifdef __CUDA_ARCH_FEAT_SM100_ALL
  __shared__ alignas(128) uint32_t box[16];
  __shared__ alignas(8) uint64_t barrier;
  const uint32_t thread = threadIdx.x;
  if (thread == 0) {
    asm volatile("mbarrier.init.shared::cta.b64 [%0], 1;" ::"r"(sharedAddress(&barrier)) : "memory");
    asm volatile("fence.mbarrier_init.release.cluster;" ::: "memory");
  }
  __syncthreads();
  if (thread == 0) {
    asm volatile("cp.async.bulk.tensor.2d.shared::cluster.global.tile::gather4.mbarrier::complete_tx::bytes [%0], [%1, {%2, %3, %4, "
                 "%5, %6}], [%7];" ::"r"(sharedAddress(box)),
                 "l"(&load), "r"(2), "r"(5), "r"(2), "r"(9), "r"(17), "r"(sharedAddress(&barrier))
                 : "memory");
    asm volatile("mbarrier.arrive.expect_tx.release.cta.shared::cta.b64 _, [%0], 64;" ::"r"(sharedAddress(&barrier)) : "memory");
  }
  uint32_t done = 0;
  while (!done)
    asm volatile("{\n"
                 " .reg .pred ready;\n"
                 " mbarrier.try_wait.parity.shared::cta.b64 ready, [%1], 0;\n"
                 " selp.b32 %0, 1, 0, ready;\n"
                 "}"
                 : "=r"(done)
                 : "r"(sharedAddress(&barrier))
                 : "memory");
  out[thread] = box[thread];
  asm volatile("fence.proxy.async.shared::cta;" ::: "memory");
  __syncthreads();
  if (thread == 0) {
    asm volatile("cp.async.bulk.tensor.2d.global.shared::cta.tile::scatter4.bulk_group [%0, {%1, %2, %3, %4, %5}], [%6];" ::"l"(&store),
                 "r"(4), "r"(1), "r"(3), "r"(4), "r"(20), "r"(sharedAddress(box))
                 : "memory");
    asm volatile("cp.async.bulk.commit_group;" ::: "memory");
    asm volatile("cp.async.bulk.wait_group.read 0;" ::: "memory");
  }
#endif
}

} // namespace

TEST_CASE("runtime/cuda/blackwell/tensor-gather4-scatter4", "[unified][runtime][blackwell][compact]") {
  if (!available()) SKIP("tile::gather4 requires sm_100a code on a compute capability 10.0 device");
  const auto tensor = iota(0U, 16U * 8U) | map([](uint32_t index) { return (100 * (index / 8)) + (index % 8); }) | to_vector();
  Buffer<uint32_t> source(tensor);
  Buffer<uint32_t> target(std::vector<uint32_t>(size_t{20} * 8, 0));
  Buffer<uint32_t> out(std::vector<uint32_t>(16, 0xffffffffU));
  const auto encode = encodeTiled();
  const cuuint64_t strides[1] = {8 * sizeof(uint32_t)};
  const cuuint32_t box[2] = {4, 1};
  const cuuint32_t traversal[2] = {1, 1};
  CUtensorMap maps[2]{};
  const int rows[2] = {16, 20};
  uint32_t *bases[2] = {source.device, target.device};
  for (int index = 0; index < 2; ++index) {
    const cuuint64_t dimensions[2] = {8, static_cast<cuuint64_t>(rows[index])};
    check(encode(&maps[index], CU_TENSOR_MAP_DATA_TYPE_UINT32, 2, bases[index], dimensions, strides, box, traversal,
                 CU_TENSOR_MAP_INTERLEAVE_NONE, CU_TENSOR_MAP_SWIZZLE_NONE, CU_TENSOR_MAP_L2_PROMOTION_NONE,
                 CU_TENSOR_MAP_FLOAT_OOB_FILL_NONE),
          "cuTensorMapEncodeTiled");
  }
  gather<<<1, 16>>>(maps[0], maps[1], out.device);
  check(cudaGetLastError(), "gather launch");
  check(cudaDeviceSynchronize(), "gather");
  const auto boxes = out.download(16);
  const auto scattered = target.download(size_t{20} * 8);
  const int gathered[4] = {5, 2, 9, 17};
  for (int slot = 0; slot < 4; ++slot)
    for (int column = 0; column < 4; ++column) {
      CAPTURE(slot, column);
      const int row = gathered[slot];
      REQUIRE(boxes[(slot * 4) + column] == (row < 16 ? tensor[(row * 8) + 2 + column] : 0U));
    }
  const int written[4] = {1, 3, 4, 20};
  for (int row = 0; row < 20; ++row)
    for (int column = 0; column < 8; ++column) {
      uint32_t expected = 0;
      for (int slot = 0; slot < 4; ++slot)
        if (written[slot] == row && column >= 4) expected = boxes[(slot * 4) + column - 4];
      CAPTURE(row, column);
      REQUIRE(scattered[(row * 8) + column] == expected);
    }
}
