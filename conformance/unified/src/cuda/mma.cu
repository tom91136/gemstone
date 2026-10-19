#include <algorithm>
#include <cstdint>
#include <functional>
#include <random>
#include <string>
#include <vector>

#include "cuda/mma_support.cuh"
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <cuda_runtime_api.h>

#include "test_support.hpp"

using namespace aspartame;

// mma.sync for every dense floating-point, integer and single-bit form up to sm_89, each as a GEMM tile: one warp
// accumulates an M x 8 tile of D over several K steps, loading every fragment from global memory in the documented
// lane layout.
namespace {

#define MMA_1_1_2_2(PTX)                                                                                                                   \
  asm volatile(PTX " {%0,%1}, {%2}, {%3}, {%4,%5};" : "=r"(d[0]), "=r"(d[1]) : "r"(a[0]), "r"(b[0]), "r"(c[0]), "r"(c[1]))
#define MMA_2_1_2_2(PTX)                                                                                                                   \
  asm volatile(PTX " {%0,%1}, {%2,%3}, {%4}, {%5,%6};" : "=r"(d[0]), "=r"(d[1]) : "r"(a[0]), "r"(a[1]), "r"(b[0]), "r"(c[0]), "r"(c[1]))
#define MMA_2_1_4_4(PTX)                                                                                                                   \
  asm volatile(PTX " {%0,%1,%2,%3}, {%4,%5}, {%6}, {%7,%8,%9,%10};"                                                                        \
               : "=r"(d[0]), "=r"(d[1]), "=r"(d[2]), "=r"(d[3])                                                                            \
               : "r"(a[0]), "r"(a[1]), "r"(b[0]), "r"(c[0]), "r"(c[1]), "r"(c[2]), "r"(c[3]))
#define MMA_4_2_2_2(PTX)                                                                                                                   \
  asm volatile(PTX " {%0,%1}, {%2,%3,%4,%5}, {%6,%7}, {%8,%9};"                                                                            \
               : "=r"(d[0]), "=r"(d[1])                                                                                                    \
               : "r"(a[0]), "r"(a[1]), "r"(a[2]), "r"(a[3]), "r"(b[0]), "r"(b[1]), "r"(c[0]), "r"(c[1]))
#define MMA_4_2_4_4(PTX)                                                                                                                   \
  asm volatile(PTX " {%0,%1,%2,%3}, {%4,%5,%6,%7}, {%8,%9}, {%10,%11,%12,%13};"                                                            \
               : "=r"(d[0]), "=r"(d[1]), "=r"(d[2]), "=r"(d[3])                                                                            \
               : "r"(a[0]), "r"(a[1]), "r"(a[2]), "r"(a[3]), "r"(b[0]), "r"(b[1]), "r"(c[0]), "r"(c[1]), "r"(c[2]), "r"(c[3]))

#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 750
  #define MMA_SM75(REGS, PTX) REGS(PTX)
#else
  #define MMA_SM75(REGS, PTX) __trap()
#endif
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 800
  #define MMA_SM80(REGS, PTX) REGS(PTX)
#else
  #define MMA_SM80(REGS, PTX) __trap()
#endif
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 890
  #define MMA_SM89(REGS, PTX) REGS(PTX)
#else
  #define MMA_SM89(REGS, PTX) __trap()
#endif

#define MMA_FORM(NAME, ARCH, M, K, ABITS, BBITS, CBITS, REGS, PTX)                                                                         \
  struct NAME {                                                                                                                            \
    static constexpr int architecture = (ARCH), m = (M), k = (K), aBits = (ABITS), bBits = (BBITS), cBits = (CBITS);                       \
    __device__ static void run(uint32_t *d, const uint32_t *a, const uint32_t *b, const uint32_t *c) { MMA_SM##ARCH(REGS, PTX); }          \
  };

MMA_FORM(F16F16K8, 75, 16, 8, 16, 16, 16, MMA_2_1_2_2, "mma.sync.aligned.m16n8k8.row.col.f16.f16.f16.f16")
MMA_FORM(F32F16K8, 75, 16, 8, 16, 16, 32, MMA_2_1_4_4, "mma.sync.aligned.m16n8k8.row.col.f32.f16.f16.f32")
MMA_FORM(F16F16K16, 80, 16, 16, 16, 16, 16, MMA_4_2_2_2, "mma.sync.aligned.m16n8k16.row.col.f16.f16.f16.f16")
MMA_FORM(F32F16K16, 80, 16, 16, 16, 16, 32, MMA_4_2_4_4, "mma.sync.aligned.m16n8k16.row.col.f32.f16.f16.f32")
MMA_FORM(F32BF16K8, 80, 16, 8, 16, 16, 32, MMA_2_1_4_4, "mma.sync.aligned.m16n8k8.row.col.f32.bf16.bf16.f32")
MMA_FORM(F32BF16K16, 80, 16, 16, 16, 16, 32, MMA_4_2_4_4, "mma.sync.aligned.m16n8k16.row.col.f32.bf16.bf16.f32")
MMA_FORM(F32TF32K4, 80, 16, 4, 32, 32, 32, MMA_2_1_4_4, "mma.sync.aligned.m16n8k4.row.col.f32.tf32.tf32.f32")
MMA_FORM(F32TF32K8, 80, 16, 8, 32, 32, 32, MMA_4_2_4_4, "mma.sync.aligned.m16n8k8.row.col.f32.tf32.tf32.f32")
MMA_FORM(S8K16M8, 75, 8, 16, 8, 8, 32, MMA_1_1_2_2, "mma.sync.aligned.m8n8k16.row.col.s32.s8.u8.s32")
MMA_FORM(S8K16M8Sat, 75, 8, 16, 8, 8, 32, MMA_1_1_2_2, "mma.sync.aligned.m8n8k16.row.col.satfinite.s32.s8.s8.s32")
MMA_FORM(U8K16, 80, 16, 16, 8, 8, 32, MMA_2_1_4_4, "mma.sync.aligned.m16n8k16.row.col.s32.u8.u8.s32")
MMA_FORM(S8K32, 80, 16, 32, 8, 8, 32, MMA_4_2_4_4, "mma.sync.aligned.m16n8k32.row.col.s32.s8.s8.s32")
MMA_FORM(S8K32Sat, 80, 16, 32, 8, 8, 32, MMA_4_2_4_4, "mma.sync.aligned.m16n8k32.row.col.satfinite.s32.s8.s8.s32")
MMA_FORM(U8S8K32Sat, 80, 16, 32, 8, 8, 32, MMA_4_2_4_4, "mma.sync.aligned.m16n8k32.row.col.satfinite.s32.u8.s8.s32")
MMA_FORM(S4K32M8, 75, 8, 32, 4, 4, 32, MMA_1_1_2_2, "mma.sync.aligned.m8n8k32.row.col.s32.s4.s4.s32")
MMA_FORM(U4K32M8Sat, 75, 8, 32, 4, 4, 32, MMA_1_1_2_2, "mma.sync.aligned.m8n8k32.row.col.satfinite.s32.u4.u4.s32")
MMA_FORM(S4U4K32, 80, 16, 32, 4, 4, 32, MMA_2_1_4_4, "mma.sync.aligned.m16n8k32.row.col.s32.s4.u4.s32")
MMA_FORM(S4K64, 80, 16, 64, 4, 4, 32, MMA_4_2_4_4, "mma.sync.aligned.m16n8k64.row.col.s32.s4.s4.s32")
MMA_FORM(XorK128M8, 75, 8, 128, 1, 1, 32, MMA_1_1_2_2, "mma.sync.aligned.m8n8k128.row.col.s32.b1.b1.s32.xor.popc")
MMA_FORM(AndK128M8, 80, 8, 128, 1, 1, 32, MMA_1_1_2_2, "mma.sync.aligned.m8n8k128.row.col.s32.b1.b1.s32.and.popc")
MMA_FORM(XorK128, 80, 16, 128, 1, 1, 32, MMA_2_1_4_4, "mma.sync.aligned.m16n8k128.row.col.s32.b1.b1.s32.xor.popc")
MMA_FORM(AndK256, 80, 16, 256, 1, 1, 32, MMA_4_2_4_4, "mma.sync.aligned.m16n8k256.row.col.s32.b1.b1.s32.and.popc")
MMA_FORM(F32E4M3K32, 89, 16, 32, 8, 8, 32, MMA_4_2_4_4, "mma.sync.aligned.m16n8k32.row.col.f32.e4m3.e4m3.f32")
MMA_FORM(F32E5M2E4M3K32, 89, 16, 32, 8, 8, 32, MMA_4_2_4_4, "mma.sync.aligned.m16n8k32.row.col.f32.e5m2.e4m3.f32")
MMA_FORM(F16E4M3K32, 89, 16, 32, 8, 8, 16, MMA_4_2_2_2, "mma.sync.aligned.m16n8k32.row.col.f16.e4m3.e4m3.f16")
MMA_FORM(F32E4M3E5M2K16, 89, 16, 16, 8, 8, 32, MMA_2_1_4_4, "mma.sync.aligned.m16n8k16.row.col.f32.e4m3.e5m2.f32")
MMA_FORM(F16E5M2K16, 89, 16, 16, 8, 8, 16, MMA_2_1_2_2, "mma.sync.aligned.m16n8k16.row.col.f16.e5m2.e5m2.f16")

// `a` is M x (steps * k) row-major, `b` is (steps * k) x 8 column-major and `c` and `d` are M x 8 row-major, each element
// a raw encoding in its own word.
template <class Form> __global__ void tile(const uint32_t *a, const uint32_t *b, const uint32_t *c, uint32_t *d, int steps) {
  constexpr int packA = Form::aBits < 32 ? 32 / Form::aBits : 1;
  constexpr int packB = Form::bBits < 32 ? 32 / Form::bBits : 1;
  constexpr int packC = 32 / Form::cBits;
  constexpr int countA = Form::m * Form::k / 32;
  constexpr int countB = Form::k * 8 / 32;
  constexpr int countC = Form::m * 8 / 32;
  const int lane = static_cast<int>(threadIdx.x % 32);
  const int depth = steps * Form::k;
  uint32_t fa[countA / packA];
  uint32_t fb[countB / packB];
  uint32_t fc[countC / packC];
  uint32_t fd[countC / packC];
  auto pack = [](uint32_t *registers, int index, int pack, int bits, uint32_t value) {
    const uint32_t mask = bits == 32 ? 0xffffffffU : (1U << bits) - 1U;
    if (index % pack == 0) registers[index / pack] = 0;
    registers[index / pack] |= (value & mask) << (index % pack * bits);
  };
  for (int i = 0; i < countC; ++i) {
    const auto [row, column] = coordinateC(lane, i);
    pack(fc, i, packC, Form::cBits, c[(row * 8) + column]);
  }
  for (int step = 0; step < steps; ++step) {
    for (int i = 0; i < countA; ++i) {
      const auto [row, column] = coordinateA(lane, i, packA);
      pack(fa, i, packA, Form::aBits, a[(row * depth) + (step * Form::k) + column]);
    }
    for (int i = 0; i < countB; ++i) {
      const auto [row, column] = coordinateB(lane, i, packB);
      pack(fb, i, packB, Form::bBits, b[(column * depth) + (step * Form::k) + row]);
    }
    Form::run(fd, fa, fb, fc);
    for (int i = 0; i < countC / packC; ++i)
      fc[i] = fd[i];
  }
  for (int i = 0; i < countC; ++i) {
    const auto [row, column] = coordinateC(lane, i);
    const uint32_t mask = Form::cBits == 32 ? 0xffffffffU : (1U << Form::cBits) - 1U;
    d[(row * 8) + column] = (fc[i / packC] >> (i % packC * Form::cBits)) & mask;
  }
}

// The values of a product's operands and how they encode, with the result its exact sum takes.
struct Domain {
  std::function<double(std::mt19937 &)> a, b, c;
  std::function<uint32_t(double)> encodeA, encodeB, encodeC;
  // the result of D from C, A's row and B's column, which every K step accumulates in turn
  std::function<uint32_t(double, const std::vector<double> &, const std::vector<double> &, int)> result;
};

// Whether the device runs the form, after checking it.
template <class Form> bool run(const char *name, const Domain &domain, int steps) {
  if (capability() < Form::architecture) return false;
  std::mt19937 random(0x6d6d61U + Form::k + (Form::aBits * 7) + Form::cBits);
  const int depth = steps * Form::k;
  std::vector<double> a(size_t(Form::m) * depth);
  std::vector<double> b(static_cast<size_t>(depth) * 8);
  std::vector<double> c(size_t(Form::m) * 8);
  for (auto &value : a)
    value = domain.a(random);
  for (auto &value : b)
    value = domain.b(random);
  for (auto &value : c)
    value = domain.c(random);
  std::vector<uint32_t> expected(c.size());
  for (int row = 0; row < Form::m; ++row)
    for (int column = 0; column < 8; ++column) {
      const auto left = a ^ slice(static_cast<size_t>(row) * depth, static_cast<size_t>(row + 1) * depth);
      const auto right = b ^ slice(static_cast<size_t>(column) * depth, static_cast<size_t>(column + 1) * depth);
      expected[(row * 8) + column] = domain.result(c[(row * 8) + column], left, right, Form::k);
    }
  DeviceBuffer<uint32_t> da(a ^ map(domain.encodeA));
  DeviceBuffer<uint32_t> db(b ^ map(domain.encodeB));
  DeviceBuffer<uint32_t> dc(c ^ map(domain.encodeC));
  DeviceBuffer<uint32_t> dd(std::vector<uint32_t>(c.size(), 0xdeadbeefU));
  tile<Form><<<1, 32>>>(da.data, db.data, dc.data, dd.data, steps);
  finish(name);
  expectEqual(dd.download(), expected, name);
  return true;
}

std::function<double(std::mt19937 &)> uniform(int low, int high, double scale = 1) {
  return [=](std::mt19937 &random) { return std::uniform_int_distribution<int>(low, high)(random) * scale; };
}

// An exact floating-point sum, encoded.
Domain floating(const std::function<uint32_t(double)> &encode, const std::function<uint32_t(double)> &result, int range, double scale) {
  return Domain{uniform(-range, range, scale),
                uniform(-range, range, scale),
                uniform(-64, 64),
                encode,
                encode,
                result,
                [result](double c, const std::vector<double> &a, const std::vector<double> &b, int) {
                  double sum = c;
                  for (size_t k = 0; k < a.size(); ++k)
                    sum += a[k] * b[k];
                  return result(sum);
                }};
}

// An integer sum that wraps, or clamps to 32 bits after every `chunk` products and at the end of each instruction.
Domain integer(int lowA, int highA, int lowB, int highB, int bits, bool saturate, int chunk, int32_t lowC, int32_t highC) {
  auto field = [bits](double value) {
    const auto word = static_cast<uint32_t>(static_cast<int64_t>(value));
    return bits == 32 ? word : word & ((1U << bits) - 1U);
  };
  auto clamp = [](int64_t value) { return std::clamp<int64_t>(value, INT32_MIN, INT32_MAX); };
  return Domain{uniform(lowA, highA),
                uniform(lowB, highB),
                [=](std::mt19937 &random) { return static_cast<double>(std::uniform_int_distribution<int32_t>(lowC, highC)(random)); },
                field,
                field,
                [](double value) { return static_cast<uint32_t>(static_cast<int64_t>(value)); },
                [=](double c, const std::vector<double> &a, const std::vector<double> &b, int k) {
                  int64_t sum = static_cast<int64_t>(c);
                  for (size_t index = 0; index < a.size(); ++index) {
                    sum += static_cast<int64_t>(a[index]) * static_cast<int64_t>(b[index]);
                    const bool boundary = (index + 1) % k == 0 || ((index % k) + 1) % chunk == 0;
                    if (boundary) sum = saturate ? clamp(sum) : static_cast<int32_t>(static_cast<uint32_t>(sum));
                  }
                  return static_cast<uint32_t>(static_cast<int32_t>(sum));
                }};
}

// Popcounts of 32-bit segments: a and b hold 0 or 1 per bit.
Domain bits(bool exclusive) {
  return Domain{uniform(0, 1),
                uniform(0, 1),
                uniform(-100000, 100000),
                [](double value) { return static_cast<uint32_t>(value); },
                [](double value) { return static_cast<uint32_t>(value); },
                [](double value) { return static_cast<uint32_t>(static_cast<int64_t>(value)); },
                [exclusive](double c, const std::vector<double> &a, const std::vector<double> &b, int) {
                  const auto set =
                      a ^ zip(b) ^ count([exclusive](double x, double y) { return exclusive ? (x != 0) != (y != 0) : x != 0 && y != 0; });
                  return static_cast<uint32_t>(static_cast<int32_t>(static_cast<int64_t>(c) + static_cast<int64_t>(set)));
                }};
}

} // namespace

TEST_CASE("runtime/cuda/mma/half", "[unified][runtime][tensor][compact]") {
  bool ran = false;
  const auto toHalf = floating(half, half, 4, 1);
  const auto toSingle = floating(half, [](double value) { return floatBits(value); }, 4, 1);
  ran |= run<F16F16K8>("m16n8k8 f16", toHalf, 3);
  ran |= run<F32F16K8>("m16n8k8 f32.f16", toSingle, 3);
  ran |= run<F16F16K16>("m16n8k16 f16", toHalf, 2);
  ran |= run<F32F16K16>("m16n8k16 f32.f16", toSingle, 3);
  if (!ran) SKIP("the device runs none of these forms");
}

TEST_CASE("runtime/cuda/mma/bfloat16-tf32", "[unified][runtime][tensor][compact]") {
  bool ran = false;
  const auto single = [](double value) { return floatBits(value); };
  ran |= run<F32BF16K8>("m16n8k8 f32.bf16", floating(bfloat, single, 8, 0.5), 3);
  ran |= run<F32BF16K16>("m16n8k16 f32.bf16", floating(bfloat, single, 8, 0.5), 3);
  ran |= run<F32TF32K4>("m16n8k4 f32.tf32", floating(single, single, 16, 0.25), 3);
  ran |= run<F32TF32K8>("m16n8k8 f32.tf32", floating(single, single, 16, 0.25), 3);
  if (!ran) SKIP("the device runs none of these forms");
}

TEST_CASE("runtime/cuda/mma/integer", "[unified][runtime][tensor][compact]") {
  bool ran = false;
  ran |= run<S8K16M8>("m8n8k16 s8.u8", integer(-128, 127, 0, 255, 8, false, 16, INT32_MIN, INT32_MAX), 3);
  ran |= run<S8K16M8Sat>("m8n8k16 satfinite s8", integer(-128, 127, -128, 127, 8, true, 16, INT32_MAX - 300000, INT32_MAX), 2);
  ran |= run<U8K16>("m16n8k16 u8", integer(0, 255, 0, 255, 8, false, 16, INT32_MAX - 500000, INT32_MAX), 2);
  ran |= run<S8K32>("m16n8k32 s8", integer(-128, 127, -128, 127, 8, false, 16, INT32_MIN, INT32_MAX), 2);
  ran |= run<S8K32Sat>("m16n8k32 satfinite s8", integer(-128, 127, -128, 127, 8, true, 16, INT32_MAX - 200000, INT32_MAX), 2);
  ran |= run<U8S8K32Sat>("m16n8k32 satfinite u8.s8", integer(0, 255, -128, 127, 8, true, 16, INT32_MIN, INT32_MIN + 300000), 2);
  ran |= run<S4K32M8>("m8n8k32 s4", integer(-8, 7, -8, 7, 4, false, 32, INT32_MIN, INT32_MAX), 3);
  ran |= run<U4K32M8Sat>("m8n8k32 satfinite u4", integer(0, 15, 0, 15, 4, true, 32, INT32_MAX - 4000, INT32_MAX), 2);
  ran |= run<S4U4K32>("m16n8k32 s4.u4", integer(-8, 7, 0, 15, 4, false, 32, INT32_MIN, INT32_MAX), 2);
  ran |= run<S4K64>("m16n8k64 s4", integer(-8, 7, -8, 7, 4, false, 32, -100000, 100000), 2);
  if (!ran) SKIP("the device runs none of these forms");
}

TEST_CASE("runtime/cuda/mma/single-bit", "[unified][runtime][tensor][compact]") {
  bool ran = false;
  ran |= run<XorK128M8>("m8n8k128 xor", bits(true), 2);
  ran |= run<AndK128M8>("m8n8k128 and", bits(false), 2);
  ran |= run<XorK128>("m16n8k128 xor", bits(true), 2);
  ran |= run<AndK256>("m16n8k256 and", bits(false), 2);
  if (!ran) SKIP("the device runs none of these forms");
}

TEST_CASE("runtime/cuda/mma/fp8", "[unified][runtime][tensor][compact]") {
  bool ran = false;
  const auto single = [](double value) { return floatBits(value); };
  ran |= run<F32E4M3K32>("m16n8k32 f32.e4m3", floating(e4m3, single, 8, 0.5), 3);
  auto mixed = floating(e5m2, single, 4, 1);
  mixed.b = uniform(-8, 8, 0.5);
  mixed.encodeB = e4m3;
  ran |= run<F32E5M2E4M3K32>("m16n8k32 f32.e5m2.e4m3", mixed, 2);
  ran |= run<F16E4M3K32>("m16n8k32 f16.e4m3", floating(e4m3, half, 3, 1), 2);
  auto narrow = floating(e4m3, single, 8, 0.5);
  narrow.b = uniform(-4, 4);
  narrow.encodeB = e5m2;
  ran |= run<F32E4M3E5M2K16>("m16n8k16 f32.e4m3.e5m2", narrow, 3);
  ran |= run<F16E5M2K16>("m16n8k16 f16.e5m2", floating(e5m2, half, 4, 1), 3);
  if (!ran) SKIP("the device runs none of these forms");
}

namespace {
// m8n8k4 binary64: A, B and C as above, with one register per element.
__global__ void doubleTile(const double *a, const double *b, const double *c, double *d, int steps) {
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 800
  const int lane = static_cast<int>(threadIdx.x % 32);
  const int depth = steps * 4;
  double fc[2];
  double fd[2];
  for (int i = 0; i < 2; ++i)
    fc[i] = c[(lane / 4 * 8) + (lane % 4 * 2) + i];
  for (int step = 0; step < steps; ++step) {
    const double fa = a[(lane / 4 * depth) + (step * 4) + (lane % 4)];
    const double fb = b[(lane / 4 * depth) + (step * 4) + (lane % 4)];
    asm volatile("mma.sync.aligned.m8n8k4.row.col.f64.f64.f64.f64 {%0,%1}, {%2}, {%3}, {%4,%5};"
                 : "=d"(fd[0]), "=d"(fd[1])
                 : "d"(fa), "d"(fb), "d"(fc[0]), "d"(fc[1]));
    fc[0] = fd[0];
    fc[1] = fd[1];
  }
  for (int i = 0; i < 2; ++i)
    d[(lane / 4 * 8) + (lane % 4 * 2) + i] = fc[i];
#else
  __trap();
#endif
}

} // namespace

TEST_CASE("runtime/cuda/mma/double", "[unified][runtime][tensor][compact]") {
  if (capability() < 80) SKIP("binary64 mma requires compute capability 8.0");
  std::mt19937 random(0x64626cU);
  constexpr int steps = 5;
  constexpr int depth = steps * 4;
  const auto pick = uniform(-1000, 1000, 0.125);
  std::vector<double> a(size_t{8} * depth);
  std::vector<double> b(size_t{8} * depth);
  std::vector<double> c(64);
  for (auto &value : a)
    value = pick(random);
  for (auto &value : b)
    value = pick(random);
  for (auto &value : c)
    value = pick(random);
  std::vector<double> expected(64);
  for (int row = 0; row < 8; ++row)
    for (int column = 0; column < 8; ++column) {
      expected[(row * 8) + column] = iota(0, depth) | fold_left(c[(row * 8) + column], [&](double sum, int k) {
                                       return std::fma(a[(row * depth) + k], b[(column * depth) + k], sum);
                                     });
    }
  DeviceBuffer<double> da(a);
  DeviceBuffer<double> db(b);
  DeviceBuffer<double> dc(c);
  DeviceBuffer<double> dd(std::vector<double>(64, -1));
  doubleTile<<<1, 32>>>(da.data, db.data, dc.data, dd.data, steps);
  finish("m8n8k4 f64");
  expectEqual(dd.download() ^ map(doubleBits), expected ^ map(doubleBits), "m8n8k4 f64");
}
