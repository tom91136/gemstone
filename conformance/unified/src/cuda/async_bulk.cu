#include <cmath>
#include <cstdint>
#include <cstring>
#include <stdexcept>
#include <vector>

#include <cooperative_groups.h>

#include "cuda/support.cuh"
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators.hpp>
#include <cuda.h>
#include <cuda/barrier>
#include <cuda/ptx>
#include <cuda_runtime.h>

#include "conformance/device.hpp"

#include "test_support.hpp"

using namespace aspartame;

namespace {
#if __CUDA_ARCH__ >= 700
namespace cg = cooperative_groups;
#endif
#if __CUDA_ARCH__ >= 900
namespace ptx = cuda::ptx;
#endif

int computeCapability() {
  const auto selected = static_cast<int>(conformance::deviceSelection().nativeHandle);
  check(cudaSetDevice(selected), "cudaSetDevice");
  int major = 0;
  check(cudaDeviceGetAttribute(&major, cudaDevAttrComputeCapabilityMajor, selected), "compute capability");
  return major;
}

constexpr int BlockInts = 1024;

// cuda::memcpy_async completes through cuda::barrier: cp.async on sm_80 and a bulk copy with mbarrier transactions
// from sm_90.
__global__ void barrierCopy([[maybe_unused]] const int4 *input, [[maybe_unused]] int4 *output) {
#if __CUDA_ARCH__ >= 700
  using Barrier = cuda::barrier<cuda::thread_scope_block>;
  __shared__ alignas(128) int4 staged[BlockInts / 4];
  // Raw storage that init constructs in place, as shared variables cannot have dynamic initialisers under Clang.
  __shared__ alignas(Barrier) unsigned char storage[sizeof(Barrier)];
  auto &barrier = *reinterpret_cast<Barrier *>(storage);
  auto block = cg::this_thread_block();
  if (block.thread_rank() == 0) init(&barrier, block.size());
  block.sync();
  cuda::memcpy_async(block, staged, input + (static_cast<size_t>(blockIdx.x) * (BlockInts / 4)), cuda::aligned_size_t<16>(sizeof(staged)),
                     barrier);
  barrier.arrive_and_wait();
  const int4 value = staged[(threadIdx.x * 7) % (BlockInts / 4)];
  output[(blockIdx.x * (BlockInts / 4)) + threadIdx.x] = make_int4(value.x + 1, value.y * 2, value.z - 3, value.w ^ 5);
#endif
}

} // namespace

TEST_CASE("runtime/cuda/async/memcpy-async-barrier", "[unified][runtime][async][compact]") {
  if (computeCapability() < 7) SKIP("cuda::barrier requires compute capability 7.0");
  constexpr int blocks = 3;
  const auto input = iota(size_t{0}, static_cast<size_t>(blocks) * BlockInts)
                     | map([](size_t index) { return static_cast<int>(index * 37 % 1001) - 500; }) | to_vector();
  Buffer<int> source(input);
  Buffer<int> target(std::vector<int>(input.size(), 0));
  barrierCopy<<<blocks, BlockInts / 4>>>(reinterpret_cast<const int4 *>(source.device), reinterpret_cast<int4 *>(target.device));
  check(cudaGetLastError(), "barrierCopy launch");
  check(cudaDeviceSynchronize(), "barrierCopy");
  const auto actual = target.download(input.size());
  for (int block = 0; block < blocks; ++block)
    for (int thread = 0; thread < BlockInts / 4; ++thread) {
      const int *value = &input[(block * BlockInts) + (((thread * 7) % (BlockInts / 4)) * 4)];
      const int *result = &actual[static_cast<size_t>((block * BlockInts / 4) + thread) * 4];
      CAPTURE(block, thread);
      REQUIRE(result[0] == value[0] + 1);
      REQUIRE(result[1] == value[1] * 2);
      REQUIRE(result[2] == value[2] - 3);
      REQUIRE(result[3] == (value[3] ^ 5));
    }
}

namespace {
// Bulk copies in, stores, reductions and a shared-to-shared copy, each completing through an mbarrier transaction
// or a bulk group. The shared-to-shared copy needs a launch with a cluster; without one, sm_90 hardware ends the launch
// with an illegal instruction.
__global__ void bulkCopies([[maybe_unused]] const int *input, [[maybe_unused]] int *output) {
#if __CUDA_ARCH__ >= 900
  __shared__ alignas(128) int loaded[256];
  __shared__ alignas(128) int copied[256];
  __shared__ uint64_t barrier;
  const unsigned thread = threadIdx.x;
  if (thread == 0) {
    ptx::mbarrier_init(&barrier, 1);
    ptx::fence_proxy_async(ptx::space_shared);
  }
  __syncthreads();
  if (thread == 0) {
    ptx::cp_async_bulk(ptx::space_cluster, ptx::space_global, loaded, input + (static_cast<size_t>(blockIdx.x) * 256), sizeof(loaded),
                       &barrier);
    (void)ptx::mbarrier_arrive_expect_tx(ptx::sem_release, ptx::scope_cta, ptx::space_shared, &barrier, sizeof(loaded));
  }
  while (!ptx::mbarrier_try_wait_parity(&barrier, 0)) {
  }
  loaded[thread] = (loaded[thread] * 3) + 1;
  ptx::fence_proxy_async(ptx::space_shared);
  __syncthreads();
  if (thread == 0) {
    int *block = output + (static_cast<size_t>(blockIdx.x) * 1024);
    ptx::cp_async_bulk(ptx::space_global, ptx::space_shared, block, loaded, sizeof(loaded));
    ptx::cp_reduce_async_bulk(ptx::space_global, ptx::space_shared, ptx::op_add, block + 256, loaded, sizeof(loaded));
    ptx::cp_reduce_async_bulk(ptx::space_global, ptx::space_shared, ptx::op_min, block + 512, loaded, sizeof(loaded));
    ptx::cp_async_bulk_commit_group();
    ptx::cp_async_bulk_wait_group_read(ptx::n32_t<0>());
    ptx::cp_async_bulk(ptx::space_cluster, ptx::space_shared, copied, loaded, sizeof(loaded), &barrier);
    (void)ptx::mbarrier_arrive_expect_tx(ptx::sem_release, ptx::scope_cta, ptx::space_shared, &barrier, sizeof(copied));
  }
  while (!ptx::mbarrier_try_wait_parity(&barrier, 1)) {
  }
  output[(blockIdx.x * 1024) + 768 + thread] = copied[255 - thread];
#endif
}

} // namespace

TEST_CASE("runtime/cuda/async/bulk-copy-store-reduce", "[unified][runtime][async][compact]") {
  if (computeCapability() < 9) SKIP("bulk asynchronous copies require compute capability 9.0");
  constexpr int blocks = 2;
  const auto input = iota(size_t{0}, static_cast<size_t>(blocks) * 256)
                     | map([](size_t index) { return static_cast<int>(index * 13 % 97) - 48; }) | to_vector();
  std::vector<int> initial(static_cast<size_t>(blocks) * 1024, 0);
  for (int block = 0; block < blocks; ++block)
    for (int index = 0; index < 256; ++index) {
      initial[(block * 1024) + 256 + index] = 1000 + index;
      initial[(block * 1024) + 512 + index] = index - 20;
    }
  Buffer<int> source(input);
  Buffer<int> target(initial);
  cudaLaunchConfig_t config{};
  config.gridDim = dim3(blocks);
  config.blockDim = dim3(256);
  cudaLaunchAttribute attribute{};
  attribute.id = cudaLaunchAttributeClusterDimension;
  attribute.val.clusterDim.x = 1;
  attribute.val.clusterDim.y = 1;
  attribute.val.clusterDim.z = 1;
  config.attrs = &attribute;
  config.numAttrs = 1;
  check(cudaLaunchKernelEx(&config, bulkCopies, source.device, target.device), "bulkCopies launch");
  check(cudaDeviceSynchronize(), "bulkCopies");
  const auto actual = target.download(initial.size());
  for (int block = 0; block < blocks; ++block)
    for (int index = 0; index < 256; ++index) {
      const int value = (input[(block * 256) + index] * 3) + 1;
      CAPTURE(block, index);
      REQUIRE(actual[(block * 1024) + index] == value);
      REQUIRE(actual[(block * 1024) + 256 + index] == 1000 + index + value);
      REQUIRE(actual[(block * 1024) + 512 + index] == std::min(index - 20, value));
      REQUIRE(actual[(block * 1024) + 768 + index] == (input[(block * 256) + 255 - index] * 3) + 1);
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

// A 2D box loaded through a tensor map, read back in shared-memory order, then stored and add-reduced through two
// more maps over the same box.
__global__ void tensorTile([[maybe_unused]] const __grid_constant__ CUtensorMap load,
                           [[maybe_unused]] const __grid_constant__ CUtensorMap store,
                           [[maybe_unused]] const __grid_constant__ CUtensorMap reduce, [[maybe_unused]] float *shared,
                           [[maybe_unused]] int x, [[maybe_unused]] int y) {
#if __CUDA_ARCH__ >= 900
  constexpr int tileBytes = 1024;
  __shared__ alignas(1024) float box[tileBytes / 4];
  __shared__ uint64_t barrier;
  const unsigned thread = threadIdx.x;
  if (thread == 0) {
    ptx::mbarrier_init(&barrier, 1);
    ptx::fence_proxy_async(ptx::space_shared);
  }
  __syncthreads();
  const int32_t coordinates[2] = {x, y};
  if (thread == 0) {
    ptx::cp_async_bulk_tensor(ptx::space_cluster, ptx::space_global, box, &load, coordinates, &barrier);
    (void)ptx::mbarrier_arrive_expect_tx(ptx::sem_release, ptx::scope_cta, ptx::space_shared, &barrier, sizeof(box));
  }
  while (!ptx::mbarrier_try_wait_parity(&barrier, 0)) {
  }
  shared[thread] = box[thread];
  ptx::fence_proxy_async(ptx::space_shared);
  __syncthreads();
  if (thread == 0) {
    ptx::cp_async_bulk_tensor(ptx::space_global, ptx::space_shared, &store, coordinates, box);
    ptx::cp_reduce_async_bulk_tensor(ptx::space_global, ptx::space_shared, ptx::op_add, &reduce, coordinates, box);
    ptx::cp_async_bulk_commit_group();
    ptx::cp_async_bulk_wait_group_read(ptx::n32_t<0>());
  }
#endif
}

// The swizzled shared-memory byte for a box byte, from a 1024-byte aligned start: 16-byte chunks XOR their index
// within 128 bytes with the low bits of the 128-byte row.
size_t swizzle(int mode, size_t offset) {
  const size_t row = offset >> 7;
  switch (mode) {
    case 1: return offset ^ ((row & 1) << 4);
    case 2: return offset ^ ((row & 3) << 4);
    case 3: return offset ^ ((row & 7) << 4);
    default: return offset;
  }
}

} // namespace

TEST_CASE("runtime/cuda/async/tensor-tile-swizzle-oob", "[unified][runtime][async][compact]") {
  if (computeCapability() < 9) SKIP("tensor memory access requires compute capability 9.0");
  const auto mode = GENERATE(0, 1, 2, 3);
  const auto nanFill = GENERATE(false, true);
  constexpr int width = 40;
  constexpr int height = 20;
  const int boxWidth = mode == 0 ? 32 : 4 << mode;
  const int boxHeight = 256 / boxWidth;
  const int x = width - (boxWidth / 2);
  const int y = height - (boxHeight / 2);
  const auto tensor = iota(0, width * height) | map([](int index) {
                        const int row = index / width;
                        return static_cast<float>((row * 100) + (index % width));
                      })
                      | to_vector();
  Buffer<float> source(tensor);
  Buffer<float> stored(std::vector<float>(tensor.size(), -1.0F));
  Buffer<float> reduced(std::vector<float>(tensor.size(), 1.0F));
  Buffer<float> shared(std::vector<float>(256, -2.0F));
  const auto encode = encodeTiled();
  const cuuint64_t dimensions[2] = {width, height};
  const cuuint64_t strides[1] = {width * sizeof(float)};
  const cuuint32_t box[2] = {static_cast<cuuint32_t>(boxWidth), static_cast<cuuint32_t>(boxHeight)};
  const cuuint32_t traversal[2] = {1, 1};
  const auto swizzleMode = static_cast<CUtensorMapSwizzle>(mode);
  const auto fill = nanFill ? CU_TENSOR_MAP_FLOAT_OOB_FILL_NAN_REQUEST_ZERO_FMA : CU_TENSOR_MAP_FLOAT_OOB_FILL_NONE;
  CUtensorMap maps[3]{};
  float *bases[3] = {source.device, stored.device, reduced.device};
  for (int index = 0; index < 3; ++index)
    check(encode(&maps[index], CU_TENSOR_MAP_DATA_TYPE_FLOAT32, 2, bases[index], dimensions, strides, box, traversal,
                 CU_TENSOR_MAP_INTERLEAVE_NONE, swizzleMode, CU_TENSOR_MAP_L2_PROMOTION_NONE, fill),
          "cuTensorMapEncodeTiled");
  tensorTile<<<1, 256>>>(maps[0], maps[1], maps[2], shared.device, x, y);
  check(cudaGetLastError(), "tensorTile launch");
  check(cudaDeviceSynchronize(), "tensorTile");
  const auto boxes = shared.download(256);
  const auto stores = stored.download(tensor.size());
  const auto reductions = reduced.download(tensor.size());
  CAPTURE(mode, nanFill);
  for (int word = 0; word < 256; ++word) {
    const auto logical = swizzle(mode, static_cast<size_t>(word) * 4) / 4;
    const int column = x + static_cast<int>(logical % boxWidth);
    const int row = y + static_cast<int>(logical / boxWidth);
    CAPTURE(word, column, row);
    if (column < width && row < height) REQUIRE(boxes[word] == tensor[(row * width) + column]);
    else if (nanFill) REQUIRE(std::isnan(boxes[word]));
    else REQUIRE(boxes[word] == 0.0F);
  }
  for (int row = 0; row < height; ++row)
    for (int column = 0; column < width; ++column) {
      const bool inside = column >= x && row >= y;
      CAPTURE(row, column);
      REQUIRE(stores[(row * width) + column] == (inside ? tensor[(row * width) + column] : -1.0F));
      REQUIRE(reductions[(row * width) + column] == (inside ? tensor[(row * width) + column] + 1.0F : 1.0F));
    }
}

namespace {
#if __CUDA_ARCH__ >= 900
// nvcc builds use cooperative_groups' cluster API; Clang's CUDA headers lack it, so those use cuda::ptx and mapa.
struct ClusterView {
  #ifdef __NVCC__
  static __device__ unsigned rank() { return cg::this_cluster().block_rank(); }
  static __device__ unsigned size() { return cg::this_cluster().num_blocks(); }
  static __device__ unsigned width() { return cg::this_cluster().dim_blocks().x; }
  static __device__ void sync() { cg::this_cluster().sync(); }
  template <typename T> static __device__ T *map(T *pointer, unsigned target) {
    return cg::this_cluster().map_shared_rank(pointer, target);
  }
  #else
  static __device__ unsigned rank() { return ptx::get_sreg_cluster_ctarank(); }
  static __device__ unsigned size() { return ptx::get_sreg_cluster_nctarank(); }
  static __device__ unsigned width() { return ptx::get_sreg_cluster_nctaid_x(); }
  static __device__ void sync() {
    ptx::barrier_cluster_arrive();
    ptx::barrier_cluster_wait();
  }
  template <typename T> static __device__ T *map(T *pointer, unsigned target) {
    T *mapped = nullptr;
    asm volatile("mapa.u64 %0, %1, %2;" : "=l"(mapped) : "l"(pointer), "r"(target));
    return mapped;
  }
  #endif
};
#endif

// Each CTA of a cluster publishes a slice, reads its neighbour's through distributed shared memory, adds to it and
// pushes a bulk copy into it that completes on the neighbour's mbarrier.
__global__ void clusterExchange([[maybe_unused]] int *output) {
#if __CUDA_ARCH__ >= 900
  __shared__ alignas(128) int slice[64];
  __shared__ alignas(128) int received[64];
  __shared__ uint64_t barrier;
  const unsigned rank = ClusterView::rank();
  const unsigned size = ClusterView::size();
  const unsigned thread = threadIdx.x;
  slice[thread] = static_cast<int>((blockIdx.x * 1000) + thread);
  if (thread == 0) {
    ptx::mbarrier_init(&barrier, 1);
    ptx::fence_mbarrier_init(ptx::sem_release, ptx::scope_cluster);
  }
  ClusterView::sync();
  const unsigned next = (rank + 1) % size;
  int *neighbour = ClusterView::map(slice, next);
  const int seen = neighbour[thread];
  atomicAdd(&neighbour[(thread + 1) % 64], 1);
  ClusterView::sync();
  if (thread == 0) {
    (void)ptx::mbarrier_arrive_expect_tx(ptx::sem_release, ptx::scope_cluster, ptx::space_shared, &barrier, sizeof(received));
    ptx::fence_proxy_async(ptx::space_shared);
    int *target = ClusterView::map(received, next);
    uint64_t *targetBarrier = ClusterView::map(&barrier, next);
    ptx::cp_async_bulk(ptx::space_cluster, ptx::space_shared, target, slice, sizeof(slice), targetBarrier);
  }
  while (!ptx::mbarrier_try_wait_parity(ptx::sem_acquire, ptx::scope_cluster, &barrier, 0)) {
  }
  int *row = output + (static_cast<size_t>(blockIdx.x) * 64 * 4) + (static_cast<size_t>(thread) * 4);
  row[0] = seen;
  row[1] = slice[thread];
  row[2] = received[thread];
  row[3] = static_cast<int>((rank * 100) + (size * 10) + ClusterView::width());
  ClusterView::sync();
#endif
}

} // namespace

TEST_CASE("runtime/cuda/async/cluster-distributed-shared-memory", "[unified][runtime][async][compact]") {
  if (computeCapability() < 9) SKIP("thread block clusters require compute capability 9.0");
  const unsigned clusterSize = GENERATE(2U, 4U);
  constexpr unsigned blocks = 8;
  Buffer<int> output(std::vector<int>(static_cast<size_t>(blocks) * 64 * 4, 0));
  cudaLaunchConfig_t config{};
  config.gridDim = dim3(blocks);
  config.blockDim = dim3(64);
  cudaLaunchAttribute attribute{};
  attribute.id = cudaLaunchAttributeClusterDimension;
  attribute.val.clusterDim.x = clusterSize;
  attribute.val.clusterDim.y = 1;
  attribute.val.clusterDim.z = 1;
  config.attrs = &attribute;
  config.numAttrs = 1;
  check(cudaLaunchKernelEx(&config, clusterExchange, output.device), "clusterExchange launch");
  check(cudaDeviceSynchronize(), "clusterExchange");
  const auto actual = output.download(static_cast<size_t>(blocks) * 64 * 4);
  CAPTURE(clusterSize);
  for (unsigned block = 0; block < blocks; ++block) {
    const unsigned rank = block % clusterSize;
    const unsigned next = block - rank + ((rank + 1) % clusterSize);
    const unsigned previous = block - rank + ((rank + clusterSize - 1) % clusterSize);
    for (unsigned thread = 0; thread < 64; ++thread) {
      const int *row = &actual[static_cast<size_t>((block * 64) + thread) * 4];
      CAPTURE(block, thread);
      REQUIRE(row[0] == static_cast<int>((next * 1000) + thread));
      REQUIRE(row[1] == static_cast<int>((block * 1000) + thread + 1));
      REQUIRE(row[2] == static_cast<int>((previous * 1000) + thread + 1));
      REQUIRE(row[3] == static_cast<int>((rank * 100) + (clusterSize * 10) + clusterSize));
    }
  }
}
