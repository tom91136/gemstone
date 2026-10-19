#include <cstdint>
#include <random>
#include <string>
#include <vector>

#include "cuda/mma_support.cuh"
#include <catch2/catch_test_macros.hpp>
#include <cuda_runtime_api.h>

#include "test_support.hpp"

// wgmma.mma_async GEMM tiles on one warpgroup, as CUTLASS issues them: A and B staged in shared memory in the canonical
// layouts and swizzles of the matrix descriptor, a descriptor whose start advances along K for each instruction, the
// first instruction's scale-d dropping or keeping D, and the accumulator fragments read back through the documented
// layout. Values are small integers, so every product and sum is exact.
namespace {

constexpr int Rows = 64;
constexpr int Threads = 128;

// A product of an M=64 by `n` tile over `depth` columns of K, `k` per instruction, from `bits`-wide A and B elements,
// each in shared memory with a swizzle of `swizzle` bytes (0 for none), K-major or `transposed`, or A in registers.
struct Form {
  const char *name;
  int n, k, depth, bits, swizzle;
  bool transposed, registersA, sparse, halfAccumulator, negateB;
};

// The forms below; each kernel issues one of them per step along K.
enum class Kind { F16, BF16, TF32, FP8, S8, F16Half, Sparse };

#define WGMMA_D16 "{%0,%1,%2,%3,%4,%5,%6,%7,%8,%9,%10,%11,%12,%13,%14,%15}"
#define WGMMA_OUT16(d)                                                                                                                     \
  "+r"((d)[0]), "+r"((d)[1]), "+r"((d)[2]), "+r"((d)[3]), "+r"((d)[4]), "+r"((d)[5]), "+r"((d)[6]), "+r"((d)[7]), "+r"((d)[8]),            \
      "+r"((d)[9]), "+r"((d)[10]), "+r"((d)[11]), "+r"((d)[12]), "+r"((d)[13]), "+r"((d)[14]), "+r"((d)[15])
#define WGMMA_PREDICATE(index) "{\n.reg .pred p;\nsetp.ne.b32 p, %" #index ", 0;\n"

#ifdef __CUDA_ARCH_FEAT_SM90_ALL

// One instruction of `kind` into the sixteen accumulator registers `d`, keeping D where `scale` is nonzero.
__device__ inline void issue(Kind kind, uint32_t *d, uint64_t descA, uint64_t descB, const uint32_t *a, uint32_t metadata, uint32_t scale) {
  switch (kind) {
    case Kind::F16:
      asm volatile(WGMMA_PREDICATE(18) "wgmma.mma_async.sync.aligned.m64n32k16.f32.f16.f16 " WGMMA_D16 ", %16, %17, p, 1, -1, 0, 0;\n}\n"
                   : WGMMA_OUT16(d)
                   : "l"(descA), "l"(descB), "r"(scale));
      break;
    case Kind::BF16:
      asm volatile(WGMMA_PREDICATE(18) "wgmma.mma_async.sync.aligned.m64n32k16.f32.bf16.bf16 " WGMMA_D16 ", %16, %17, p, 1, 1, 1, 1;\n}\n"
                   : WGMMA_OUT16(d)
                   : "l"(descA), "l"(descB), "r"(scale));
      break;
    case Kind::TF32:
      asm volatile(WGMMA_PREDICATE(18) "wgmma.mma_async.sync.aligned.m64n32k8.f32.tf32.tf32 " WGMMA_D16 ", %16, %17, p, 1, 1;\n}\n"
                   : WGMMA_OUT16(d)
                   : "l"(descA), "l"(descB), "r"(scale));
      break;
    case Kind::FP8:
      asm volatile(WGMMA_PREDICATE(18) "wgmma.mma_async.sync.aligned.m64n32k32.f32.e4m3.e5m2 " WGMMA_D16 ", %16, %17, p, 1, 1;\n}\n"
                   : WGMMA_OUT16(d)
                   : "l"(descA), "l"(descB), "r"(scale));
      break;
    case Kind::S8:
      asm volatile(WGMMA_PREDICATE(21) "wgmma.mma_async.sync.aligned.m64n32k32.s32.s8.s8 " WGMMA_D16 ", {%16, %17, %18, %19}, %20, p;\n}\n"
                   : WGMMA_OUT16(d)
                   : "r"(a[0]), "r"(a[1]), "r"(a[2]), "r"(a[3]), "l"(descB), "r"(scale));
      break;
    case Kind::F16Half:
      asm volatile(WGMMA_PREDICATE(18) "wgmma.mma_async.sync.aligned.m64n64k16.f16.f16.f16 " WGMMA_D16 ", %16, %17, p, 1, 1, 0, 0;\n}\n"
                   : WGMMA_OUT16(d)
                   : "l"(descA), "l"(descB), "r"(scale));
      break;
    case Kind::Sparse:
      asm volatile(WGMMA_PREDICATE(19) "wgmma.mma_async.sp.sync.aligned.m64n32k32.f32.f16.f16 " WGMMA_D16
                                       ", %16, %17, %18, 0, p, 1, 1, 0, 0;\n}\n"
                   : WGMMA_OUT16(d)
                   : "l"(descA), "l"(descB), "r"(metadata), "r"(scale));
      break;
  }
}

// The byte offset of element (`mn`, `k`) of an `extent` by `columns` matrix from the start of its 1024-byte aligned
// tile, in the canonical layout (PTX ISA 9.7.16.5.1.2) that `leading` and `stride` describe, after the swizzle. Core
// matrices and swizzle atoms follow one another along MN, then along K.
__host__ __device__ inline uint32_t place(const Form &form, int extent, int columns, int mn, int k, uint32_t &leading, uint32_t &stride) {
  const int width = form.swizzle ? form.swizzle / 16 : 1;
  const int perChunk = 128 / form.bits;
  uint32_t offset = 0;
  if (!form.transposed && form.swizzle == 0) {
    leading = 128, stride = 128 * (columns * form.bits / 128);
    offset = (mn % 8 * 16) + (mn / 8 * stride) + (k / perChunk * leading) + (k % perChunk * form.bits / 8);
  } else if (!form.transposed) {
    leading = 16, stride = 128 * width;
    const int byte = k * form.bits / 8;
    offset = (mn % 8 * 16 * width) + (mn / 8 * stride) + (byte / (16 * width) * (extent / 8 * stride)) + (byte % (16 * width));
  } else {
    const int atoms = extent > perChunk * width ? extent / (perChunk * width) : 1;
    leading = 128 * width, stride = 128 * width * atoms;
    offset = (mn % perChunk * form.bits / 8) + (mn / perChunk % width * 16) + (mn / (perChunk * width) * leading) + (k % 8 * 16 * width)
             + (k / 8 * stride);
  }
  return width > 1 ? offset ^ ((offset >> 7) & (width - 1)) << 4 : offset;
}

__device__ inline uint64_t swizzleMode(int swizzle) {
  switch (swizzle) {
    case 128: return 1;
    case 64: return 2;
    case 32: return 3;
    default: return 0;
  }
}

// The descriptor of the instruction at `step` along K: the canonical layout from the first column of its block of K,
// which starts a row of a swizzle pattern, so the swizzle leaves its offset as is.
__device__ inline uint64_t descriptor(const Form &form, const uint8_t *tile, int extent, int columns, int step) {
  uint32_t leading{};
  uint32_t stride{};
  const uint32_t start = place(form, extent, columns, 0, step * columns / (form.depth / form.k), leading, stride);
  const auto address = static_cast<uint32_t>(__cvta_generic_to_shared(tile)) + start;
  const uint64_t mode = swizzleMode(form.swizzle);
  return static_cast<uint64_t>((address & 0x3ffff) >> 4) | static_cast<uint64_t>((leading & 0x3ffff) >> 4) << 16
         | static_cast<uint64_t>((stride & 0x3ffff) >> 4) << 32 | mode << 62;
}

#endif

// The row and column of D a thread's accumulator element `index` holds.
__host__ __device__ inline Coordinate accumulator(int thread, int index) {
  return {(thread / 32 * 16) + (thread % 32 / 4) + (index / 2 % 2 * 8), (index / 4 * 8) + (thread % 4 * 2) + (index % 2)};
}

// Lane and bit of the metadata nibble of `chunk` (four columns) of `row` for 16-bit elements and selector 0.
__host__ __device__ inline Nibble nibble(int row, int chunk) {
  return {(row / 16 * 32) + (4 * (row % 8)) + (chunk / 4), (16 * (row % 16 / 8)) + (4 * (chunk % 4))};
}

__global__ void supported(uint32_t *out) {
#ifdef __CUDA_ARCH_FEAT_SM90_ALL
  *out = 1;
#else
  *out = 0;
#endif
}

// A and B arrive row-major as encoded bytes: A by row over its (packed) columns, B by column of D over K. Each thread
// copies its share into shared memory, loads D and A's fragment from `c` and `a`, multiplies along K and stores D.
__global__ void gemm(Form form, Kind kind, const uint8_t *a, const uint8_t *b, const uint32_t *c, const uint32_t *fragments,
                     const uint32_t *metadata, uint32_t *d, uint32_t keep) {
#ifdef __CUDA_ARCH_FEAT_SM90_ALL
  constexpr int sharedBytes = 32768;
  __shared__ __align__(1024) uint8_t shared[sharedBytes];
  uint8_t *tileA = shared;
  uint8_t *tileB = shared + (sharedBytes / 2);
  const int bytes = form.bits / 8;
  const int columnsA = form.sparse ? form.depth / 2 : form.depth;
  uint32_t leading{};
  uint32_t stride{};
  for (int index = static_cast<int>(threadIdx.x); index < Rows * columnsA; index += Threads)
    for (int byte = 0; byte < bytes; ++byte)
      tileA[place(form, Rows, columnsA, index / columnsA, index % columnsA, leading, stride) + byte] = a[(index * bytes) + byte];
  for (int index = static_cast<int>(threadIdx.x); index < form.n * form.depth; index += Threads)
    for (int byte = 0; byte < bytes; ++byte)
      tileB[place(form, form.n, form.depth, index / form.depth, index % form.depth, leading, stride) + byte] = b[(index * bytes) + byte];
  asm volatile("fence.proxy.async.shared::cta;\n" ::: "memory");
  __syncthreads();
  uint32_t accumulators[16];
  for (int index = 0; index < 16; ++index)
    accumulators[index] = c[(threadIdx.x * 16) + index];
  for (int step = 0; step < form.depth / form.k; ++step) {
    uint32_t fragment[4];
    for (int index = 0; index < 4; ++index)
      fragment[index] = fragments[(((step * Threads) + threadIdx.x) * 4) + index];
    asm volatile("wgmma.fence.sync.aligned;\n" ::: "memory");
    issue(kind, accumulators, descriptor(form, tileA, Rows, columnsA, step), descriptor(form, tileB, form.n, form.depth, step), fragment,
          metadata[threadIdx.x], step > 0 || keep);
  }
  asm volatile("wgmma.commit_group.sync.aligned;\n" ::: "memory");
  asm volatile("wgmma.wait_group.sync.aligned 0;\n" ::: "memory");
  for (int index = 0; index < 16; ++index)
    d[(threadIdx.x * 16) + index] = accumulators[index];
#else
  __trap();
#endif
}

// The encoding of `value` in the form's element type.
uint32_t encode(Kind kind, bool second, double value) {
  switch (kind) {
    case Kind::F16:
    case Kind::F16Half:
    case Kind::Sparse: return half(value);
    case Kind::BF16: return bfloat(value);
    case Kind::TF32: return floatBits(value);
    case Kind::FP8: return second ? e5m2(value) : e4m3(value);
    case Kind::S8: return static_cast<uint32_t>(static_cast<int32_t>(value)) & 0xffU;
  }
  return 0;
}

void run(const Form &form, Kind kind, bool keep) {
  std::mt19937 random((form.n * 31) + form.depth + static_cast<int>(kind));
  const int columnsA = form.sparse ? form.depth / 2 : form.depth;
  const auto aValues = values(random, static_cast<size_t>(Rows) * columnsA, 3, 0);
  const auto bValues = values(random, static_cast<size_t>(form.n) * form.depth, 3, 0);
  const auto cValues = values(random, static_cast<size_t>(Rows) * form.n, 8, 0);
  const int bytes = form.bits / 8;
  std::vector<uint8_t> a(aValues.size() * bytes);
  std::vector<uint8_t> b(bValues.size() * bytes);
  for (size_t index = 0; index < aValues.size(); ++index)
    for (int byte = 0; byte < bytes; ++byte)
      a[(index * bytes) + byte] = static_cast<uint8_t>(encode(kind, false, aValues[index]) >> (8 * byte));
  for (size_t index = 0; index < bValues.size(); ++index)
    for (int byte = 0; byte < bytes; ++byte)
      b[(index * bytes) + byte] = static_cast<uint8_t>(encode(kind, true, bValues[index]) >> (8 * byte));
  // a sparse A keeps two of each four columns, which the metadata names
  std::vector<int> kept(Rows * form.depth / 4);
  std::vector<uint32_t> metadata(Threads);
  for (size_t chunk = 0; chunk < kept.size(); ++chunk) {
    kept[chunk] = static_cast<int>(random() % 6);
    if (form.sparse) {
      static constexpr int first[6] = {0, 0, 0, 1, 1, 2};
      static constexpr int second[6] = {1, 2, 3, 2, 3, 3};
      const auto [thread, offset] = nibble(static_cast<int>(chunk) / (form.depth / 4), static_cast<int>(chunk) % (form.depth / 4));
      metadata[thread] |= static_cast<uint32_t>(first[kept[chunk]] | second[kept[chunk]] << 2) << offset;
    }
  }
  auto logicalA = [&](int row, int column) {
    if (!form.sparse) return aValues[(row * form.depth) + column];
    static constexpr int first[6] = {0, 0, 0, 1, 1, 2};
    static constexpr int second[6] = {1, 2, 3, 2, 3, 3};
    const int chunk = column / 4;
    const int choice = kept[(row * (form.depth / 4)) + chunk];
    if (column % 4 == first[choice]) return aValues[(row * columnsA) + (chunk * 2)];
    if (column % 4 == second[choice]) return aValues[(row * columnsA) + (chunk * 2) + 1];
    return 0.0;
  };
  std::vector<uint32_t> c(static_cast<size_t>(Threads) * 16);
  std::vector<uint32_t> fragments(static_cast<size_t>(form.depth / form.k) * Threads * 4);
  std::vector<double> addend(static_cast<size_t>(Rows) * form.n);
  for (int thread = 0; thread < Threads; ++thread)
    for (int index = 0; index < 16 * (form.halfAccumulator ? 2 : 1); ++index) {
      const auto [row, column] = accumulator(thread, index);
      const double value = cValues[(row * form.n) + column];
      addend[(row * form.n) + column] = value;
      if (form.halfAccumulator) c[(thread * 16) + (index / 2)] |= half(value) << (index % 2 * 16);
      else c[(thread * 16) + index] = kind == Kind::S8 ? static_cast<uint32_t>(static_cast<int32_t>(value)) : floatBits(value);
    }
  if (form.registersA)
    for (int step = 0; step < form.depth / form.k; ++step)
      for (int thread = 0; thread < Threads; ++thread)
        for (int index = 0; index < 16; ++index) {
          // 8-bit A: four elements a register, rows and columns as mma.m16n8k32 lays them out for each warp
          const int row = (thread / 32 * 16) + (thread % 32 / 4) + (index / 4 % 2 * 8);
          const int column = (step * form.k) + (thread % 4 * 4) + (index % 4) + (index / 8 * 16);
          fragments[(((step * Threads) + thread) * 4) + (index / 4)] |= (encode(kind, false, aValues[(row * form.depth) + column]) & 0xffU)
                                                                        << (index % 4 * 8);
        }
  DeviceBuffer<uint8_t> deviceA(a);
  DeviceBuffer<uint8_t> deviceB(b);
  DeviceBuffer<uint32_t> deviceC(c);
  DeviceBuffer<uint32_t> deviceFragments(fragments);
  DeviceBuffer<uint32_t> deviceMetadata(metadata);
  DeviceBuffer<uint32_t> deviceD(std::vector<uint32_t>(static_cast<size_t>(Threads) * 16));
  gemm<<<1, Threads>>>(form, kind, deviceA.data, deviceB.data, deviceC.data, deviceFragments.data, deviceMetadata.data, deviceD.data,
                       keep ? 1U : 0U);
  finish(form.name);
  const auto result = deviceD.download();
  const int elements = 16 * (form.halfAccumulator ? 2 : 1);
  std::vector<uint32_t> expected(static_cast<size_t>(Threads) * elements);
  std::vector<uint32_t> actual(static_cast<size_t>(Threads) * elements);
  for (int thread = 0; thread < Threads; ++thread)
    for (int index = 0; index < elements; ++index) {
      const auto [row, column] = accumulator(thread, index);
      double sum = keep ? addend[(row * form.n) + column] : 0.0;
      for (int k = 0; k < form.depth; ++k)
        sum += logicalA(row, k) * bValues[(column * form.depth) + k] * (form.negateB ? -1.0 : 1.0);
      const size_t at = (static_cast<size_t>(thread) * elements) + index;
      if (form.halfAccumulator) {
        expected[at] = half(sum);
        actual[at] = result[(thread * 16) + (index / 2)] >> (index % 2 * 16) & 0xffffU;
      } else {
        expected[at] = kind == Kind::S8 ? static_cast<uint32_t>(static_cast<int32_t>(sum)) : floatBits(sum);
        actual[at] = result[(thread * 16) + index];
      }
    }
  expectEqual(actual, expected, form.name);
}

bool available() {
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>{0U});
  supported<<<1, 1>>>(out.data);
  finish("supported");
  return out.download()[0] != 0;
}

} // namespace

TEST_CASE("runtime/cuda/wgmma/floating", "[unified][runtime][tensor][compact]") {
  if (!available()) SKIP("wgmma requires sm_90a code");
  // name, n, k, depth, bits, swizzle, transposed, registersA, sparse, halfAccumulator, negateB
  run(Form{"wgmma f16 128B K-major", 32, 16, 64, 16, 128, false, false, false, false, true}, Kind::F16, true);
  run(Form{"wgmma bf16 64B MN-major", 32, 16, 32, 16, 64, true, false, false, false, false}, Kind::BF16, false);
  run(Form{"wgmma tf32 32B K-major", 32, 8, 16, 32, 32, false, false, false, false, false}, Kind::TF32, true);
  run(Form{"wgmma e4m3.e5m2 interleaved K-major", 32, 32, 64, 8, 0, false, false, false, false, false}, Kind::FP8, true);
  run(Form{"wgmma f16 into f16 64B K-major", 64, 16, 32, 16, 64, false, false, false, true, false}, Kind::F16Half, true);
}

TEST_CASE("runtime/cuda/wgmma/integer", "[unified][runtime][tensor][compact]") {
  if (!available()) SKIP("wgmma requires sm_90a code");
  run(Form{"wgmma s8 A in registers 128B K-major B", 32, 32, 64, 8, 128, false, true, false, false, false}, Kind::S8, true);
}

TEST_CASE("runtime/cuda/wgmma/sparse", "[unified][runtime][tensor][compact]") {
  if (!available()) SKIP("wgmma requires sm_90a code");
  run(Form{"wgmma.sp f16 128B K-major", 32, 32, 32, 16, 128, false, false, true, false, false}, Kind::Sparse, true);
}
