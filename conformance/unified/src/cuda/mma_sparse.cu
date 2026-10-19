#include <algorithm>
#include <cstdint>
#include <functional>
#include <random>
#include <vector>

#include "cuda/mma_support.cuh"
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <cuda_runtime_api.h>

#include "test_support.hpp"

using namespace aspartame;

// mma.sp with a structured-sparse A: A holds half of each row's elements, and the metadata the host derives from the
// positions it picks places them, as the documented fragment and metadata layouts lay them out across the warp.
namespace {

#define MMA_SP_2_2_4_4(PTX)                                                                                                                \
  asm volatile(PTX " {%0,%1,%2,%3}, {%4,%5}, {%6,%7}, {%8,%9,%10,%11}, %12, 0x0;"                                                          \
               : "=r"(d[0]), "=r"(d[1]), "=r"(d[2]), "=r"(d[3])                                                                            \
               : "r"(a[0]), "r"(a[1]), "r"(b[0]), "r"(b[1]), "r"(c[0]), "r"(c[1]), "r"(c[2]), "r"(c[3]), "r"(e))
#define MMA_SP_4_4_2_2(PTX)                                                                                                                \
  asm volatile(PTX " {%0,%1}, {%2,%3,%4,%5}, {%6,%7,%8,%9}, {%10,%11}, %12, 0x0;"                                                          \
               : "=r"(d[0]), "=r"(d[1])                                                                                                    \
               : "r"(a[0]), "r"(a[1]), "r"(a[2]), "r"(a[3]), "r"(b[0]), "r"(b[1]), "r"(b[2]), "r"(b[3]), "r"(c[0]), "r"(c[1]), "r"(e))
#define MMA_SP_4_4_4_4(PTX)                                                                                                                \
  asm volatile(PTX " {%0,%1,%2,%3}, {%4,%5,%6,%7}, {%8,%9,%10,%11}, {%12,%13,%14,%15}, %16, 0x0;"                                          \
               : "=r"(d[0]), "=r"(d[1]), "=r"(d[2]), "=r"(d[3])                                                                            \
               : "r"(a[0]), "r"(a[1]), "r"(a[2]), "r"(a[3]), "r"(b[0]), "r"(b[1]), "r"(b[2]), "r"(b[3]), "r"(c[0]), "r"(c[1]), "r"(c[2]),  \
                 "r"(c[3]), "r"(e))

#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 800
  #define MMA_SP_SM80(REGS, PTX) REGS(PTX)
#else
  #define MMA_SP_SM80(REGS, PTX) __trap()
#endif
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 890
  #define MMA_SP_SM89(REGS, PTX) REGS(PTX)
#else
  #define MMA_SP_SM89(REGS, PTX) __trap()
#endif
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 800 && __CUDA_ARCH__ < 900
  #define MMA_SP_SM8X(REGS, PTX) REGS(PTX)
#else
  #define MMA_SP_SM8X(REGS, PTX) __trap()
#endif

#define MMA_SP_FORM(NAME, ARCH, MAXIMUM, K, BITS, CBITS, REGS, PTX)                                                                        \
  struct NAME {                                                                                                                            \
    static constexpr int architecture = (ARCH), maximum = (MAXIMUM), k = (K), bits = (BITS), cBits = (CBITS);                              \
    __device__ static void run(uint32_t *d, const uint32_t *a, const uint32_t *b, const uint32_t *c, uint32_t e) {                         \
      MMA_SP_SM##ARCH(REGS, PTX);                                                                                                          \
    }                                                                                                                                      \
  };
#define MMA_SP_SM8X_FORM(NAME, K, BITS, REGS, PTX)                                                                                         \
  struct NAME {                                                                                                                            \
    static constexpr int architecture = 80, maximum = 89, k = (K), bits = (BITS), cBits = 32;                                              \
    __device__ static void run(uint32_t *d, const uint32_t *a, const uint32_t *b, const uint32_t *c, uint32_t e) {                         \
      MMA_SP_SM8X(REGS, PTX);                                                                                                              \
    }                                                                                                                                      \
  };

MMA_SP_FORM(F32F16K16, 80, 999, 16, 16, 32, MMA_SP_2_2_4_4, "mma.sp::ordered_metadata.sync.aligned.m16n8k16.row.col.f32.f16.f16.f32")
MMA_SP_FORM(F32F16K16Plain, 80, 999, 16, 16, 32, MMA_SP_2_2_4_4, "mma.sp.sync.aligned.m16n8k16.row.col.f32.f16.f16.f32")
MMA_SP_FORM(F16F16K32, 80, 999, 32, 16, 16, MMA_SP_4_4_2_2, "mma.sp::ordered_metadata.sync.aligned.m16n8k32.row.col.f16.f16.f16.f16")
MMA_SP_FORM(F32BF16K16, 80, 999, 16, 16, 32, MMA_SP_2_2_4_4, "mma.sp::ordered_metadata.sync.aligned.m16n8k16.row.col.f32.bf16.bf16.f32")
MMA_SP_FORM(F32BF16K32, 80, 999, 32, 16, 32, MMA_SP_4_4_4_4, "mma.sp::ordered_metadata.sync.aligned.m16n8k32.row.col.f32.bf16.bf16.f32")
MMA_SP_FORM(F32TF32K8, 80, 999, 8, 32, 32, MMA_SP_2_2_4_4, "mma.sp::ordered_metadata.sync.aligned.m16n8k8.row.col.f32.tf32.tf32.f32")
MMA_SP_FORM(F32TF32K16, 80, 999, 16, 32, 32, MMA_SP_4_4_4_4, "mma.sp::ordered_metadata.sync.aligned.m16n8k16.row.col.f32.tf32.tf32.f32")
MMA_SP_FORM(S8K32Sat, 80, 999, 32, 8, 32, MMA_SP_2_2_4_4, "mma.sp::ordered_metadata.sync.aligned.m16n8k32.row.col.satfinite.s32.s8.s8.s32")
MMA_SP_FORM(U8S8K64, 80, 999, 64, 8, 32, MMA_SP_4_4_4_4, "mma.sp::ordered_metadata.sync.aligned.m16n8k64.row.col.s32.u8.s8.s32")
MMA_SP_SM8X_FORM(S4K64, 64, 4, MMA_SP_2_2_4_4, "mma.sp::ordered_metadata.sync.aligned.m16n8k64.row.col.s32.s4.s4.s32")
MMA_SP_SM8X_FORM(U4K128, 128, 4, MMA_SP_4_4_4_4, "mma.sp::ordered_metadata.sync.aligned.m16n8k128.row.col.s32.u4.u4.s32")
MMA_SP_FORM(F32E4M3K64, 89, 999, 64, 8, 32, MMA_SP_4_4_4_4, "mma.sp::ordered_metadata.sync.aligned.m16n8k64.row.col.f32.e4m3.e4m3.f32")

// The lane and bit offset of the metadata nibble describing `chunk` of `row`, with sparsity selector 0: 16- and 32-bit
// elements put a row's nibbles in the low or high half of a quad's lanes, narrower ones a whole register per row.
inline Nibble nibble(int bits, int row, int chunk) {
  if (bits >= 16) return {(4 * (row % 8)) + (chunk / 4), (16 * (row / 8)) + (4 * (chunk % 4))};
  return {(4 * (row % 8)) + (row / 8) + (2 * (chunk / 8)), 4 * (chunk % 8)};
}

// `a` is the packed 16 x (k / 2) A row-major, `b` is k x 8 column-major, `c` and `d` 16 x 8 row-major, `e` each lane's
// metadata.
template <class Form> __global__ void sparseTile(const uint32_t *a, const uint32_t *b, const uint32_t *c, const uint32_t *e, uint32_t *d) {
  constexpr int pack = Form::bits < 32 ? 32 / Form::bits : 1;
  constexpr int packC = 32 / Form::cBits;
  constexpr int countA = 16 * Form::k / 2 / 32;
  constexpr int countB = Form::k * 8 / 32;
  constexpr int countC = 16 * 8 / 32;
  const int lane = static_cast<int>(threadIdx.x % 32);
  uint32_t fa[countA / pack];
  uint32_t fb[countB / pack];
  uint32_t fc[countC / packC];
  uint32_t fd[countC / packC];
  auto put = [](uint32_t *registers, int index, int pack, int bits, uint32_t value) {
    const uint32_t mask = bits == 32 ? 0xffffffffU : (1U << bits) - 1U;
    if (index % pack == 0) registers[index / pack] = 0;
    registers[index / pack] |= (value & mask) << (index % pack * bits);
  };
  for (int i = 0; i < countA; ++i) {
    const auto [row, column] = coordinateA(lane, i, pack);
    put(fa, i, pack, Form::bits, a[(row * (Form::k / 2)) + column]);
  }
  for (int i = 0; i < countB; ++i) {
    const auto [row, column] = coordinateB(lane, i, pack);
    put(fb, i, pack, Form::bits, b[(column * Form::k) + row]);
  }
  for (int i = 0; i < countC; ++i) {
    const auto [row, column] = coordinateC(lane, i);
    put(fc, i, packC, Form::cBits, c[(row * 8) + column]);
  }
  Form::run(fd, fa, fb, fc, e[lane]);
  for (int i = 0; i < countC; ++i) {
    const auto [row, column] = coordinateC(lane, i);
    const uint32_t mask = Form::cBits == 32 ? 0xffffffffU : (1U << Form::cBits) - 1U;
    d[(row * 8) + column] = (fd[i / packC] >> (i % packC * Form::cBits)) & mask;
  }
}

struct Domain {
  std::function<double(std::mt19937 &)> a, b, c;
  std::function<uint32_t(double)> encodeA, encodeC, encodeD;
  bool integer, saturate;
};

// Whether the device runs the form, after checking it.
template <class Form> bool run(const char *name, const Domain &domain) {
  const int device = capability();
  if (device < Form::architecture || device > Form::maximum) return false;
  std::mt19937 random(0x737061U + Form::k + Form::bits);
  constexpr int k = Form::k;
  constexpr int packed = k / 2;
  // a metadata nibble describes four units of a row: elements, TF32 half-words or 4-bit pairs
  constexpr int width = [] {
    if (Form::bits == 32) return 2;
    return Form::bits == 4 ? 8 : 4;
  }();
  std::vector<double> a(size_t{16} * packed);
  std::vector<double> b(size_t{k} * 8);
  std::vector<double> c(size_t{16} * 8);
  std::vector<int> columns(size_t{16} * packed);
  std::vector<uint32_t> metadata(32, 0);
  const int pairs[6][2] = {{0, 1}, {0, 2}, {1, 2}, {0, 3}, {1, 3}, {2, 3}};
  for (int row = 0; row < 16; ++row)
    for (int chunk = 0; chunk < k / width; ++chunk) {
      const auto *pair = pairs[std::uniform_int_distribution<int>(0, 5)(random)];
      int first = pair[0];
      int second = pair[1];
      if (Form::bits == 32) first = std::uniform_int_distribution<int>(0, 1)(random) * 2, second = first + 1;
      const auto [lane, offset] = nibble(Form::bits, row, chunk);
      metadata[lane] |= static_cast<uint32_t>(first | second << 2) << offset;
      const int stored = width / 2;
      for (int slot = 0; slot < stored; ++slot) {
        const int position = [&] {
          if (Form::bits == 32) return first / 2;
          if (Form::bits == 4) return ((slot < 2 ? first : second) * 2) + (slot % 2);
          return slot == 0 ? first : second;
        }();
        columns[(row * packed) + (chunk * stored) + slot] = (chunk * width) + position;
      }
    }
  for (auto &value : a)
    value = domain.a(random);
  for (auto &value : b)
    value = domain.b(random);
  for (auto &value : c)
    value = domain.c(random);
  std::vector<uint32_t> expected(size_t{16} * 8);
  for (int row = 0; row < 16; ++row)
    for (int column = 0; column < 8; ++column) {
      if (domain.integer) {
        int64_t sum = iota(0, packed) | fold_left(static_cast<int64_t>(c[(row * 8) + column]), [&](int64_t total, int index) {
                        return total
                               + (static_cast<int64_t>(a[(row * packed) + index])
                                  * static_cast<int64_t>(b[(column * k) + columns[(row * packed) + index]]));
                      });
        sum = domain.saturate ? std::clamp<int64_t>(sum, INT32_MIN, INT32_MAX) : static_cast<int32_t>(static_cast<uint32_t>(sum));
        expected[(row * 8) + column] = static_cast<uint32_t>(static_cast<int32_t>(sum));
      } else {
        const auto sum = iota(0, packed) | fold_left(c[(row * 8) + column], [&](double total, int index) {
                           return total + (a[(row * packed) + index] * b[(column * k) + columns[(row * packed) + index]]);
                         });
        expected[(row * 8) + column] = domain.encodeD(sum);
      }
    }
  DeviceBuffer<uint32_t> da(a ^ map(domain.encodeA));
  DeviceBuffer<uint32_t> db(b ^ map(domain.encodeA));
  DeviceBuffer<uint32_t> dc(c ^ map(domain.encodeC));
  DeviceBuffer<uint32_t> de(metadata);
  DeviceBuffer<uint32_t> dd(std::vector<uint32_t>(size_t{16} * 8, 0xdeadbeefU));
  sparseTile<Form><<<1, 32>>>(da.data, db.data, dc.data, de.data, dd.data);
  finish(name);
  expectEqual(dd.download(), expected, name);
  return true;
}

std::function<double(std::mt19937 &)> uniform(int low, int high, double scale = 1) {
  return [=](std::mt19937 &random) { return std::uniform_int_distribution<int>(low, high)(random) * scale; };
}

Domain floating(const std::function<uint32_t(double)> &encode, const std::function<uint32_t(double)> &result, int range, double scale) {
  return Domain{uniform(-range, range, scale), uniform(-range, range, scale), uniform(-64, 64), encode, result, result, false, false};
}

Domain integer(int low, int high, int bits, bool saturate, int32_t lowC, int32_t highC) {
  auto field = [bits](double value) {
    const auto word = static_cast<uint32_t>(static_cast<int64_t>(value));
    return bits == 32 ? word : word & ((1U << bits) - 1U);
  };
  auto word = [](double value) { return static_cast<uint32_t>(static_cast<int64_t>(value)); };
  return Domain{uniform(low, high),
                uniform(low, high),
                [=](std::mt19937 &random) { return static_cast<double>(std::uniform_int_distribution<int32_t>(lowC, highC)(random)); },
                field,
                word,
                word,
                true,
                saturate};
}

} // namespace

TEST_CASE("runtime/cuda/mma-sparse/floating", "[unified][runtime][tensor][compact]") {
  bool ran = false;
  const auto single = [](double value) { return floatBits(value); };
  ran |= run<F32F16K16>("sparse m16n8k16 f32.f16", floating(half, single, 4, 1));
  ran |= run<F32F16K16Plain>("sparse m16n8k16 f32.f16 unordered", floating(half, single, 4, 1));
  ran |= run<F16F16K32>("sparse m16n8k32 f16", floating(half, half, 4, 1));
  ran |= run<F32BF16K16>("sparse m16n8k16 f32.bf16", floating(bfloat, single, 8, 0.5));
  ran |= run<F32BF16K32>("sparse m16n8k32 f32.bf16", floating(bfloat, single, 8, 0.5));
  ran |= run<F32TF32K8>("sparse m16n8k8 f32.tf32", floating(single, single, 16, 0.25));
  ran |= run<F32TF32K16>("sparse m16n8k16 f32.tf32", floating(single, single, 16, 0.25));
  ran |= run<F32E4M3K64>("sparse m16n8k64 f32.e4m3", floating(e4m3, single, 8, 0.5));
  if (!ran) SKIP("the device runs none of these forms");
}

TEST_CASE("runtime/cuda/mma-sparse/integer", "[unified][runtime][tensor][compact]") {
  bool ran = false;
  ran |= run<S8K32Sat>("sparse m16n8k32 satfinite s8", integer(-128, 127, 8, true, INT32_MAX - 100000, INT32_MAX));
  auto mixed = integer(-128, 127, 8, false, INT32_MIN, INT32_MAX);
  mixed.a = uniform(0, 255);
  ran |= run<U8S8K64>("sparse m16n8k64 u8.s8", mixed);
  ran |= run<S4K64>("sparse m16n8k64 s4", integer(-8, 7, 4, false, INT32_MIN, INT32_MAX));
  ran |= run<U4K128>("sparse m16n8k128 u4", integer(0, 15, 4, false, -100000, 100000));
  if (!ran) SKIP("the device runs none of these forms");
}
