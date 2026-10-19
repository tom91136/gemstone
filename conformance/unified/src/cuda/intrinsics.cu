#include <algorithm>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <stdexcept>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#if !defined(__CUDA_ARCH__) || __CUDA_ARCH__ >= 600
  #include <cuda/atomic>
#endif
#include "cuda/support.cuh"
#include <cuda_bf16.h>
#include <cuda_fp16.h>

#include "test_support.hpp"
#include <fmt/format.h>
#if CUDART_VERSION >= 11080
  #include <cuda_fp8.h>
#endif

using namespace aspartame;

// Device intrinsics whose instructions nvcc and clang emit directly, through the CUDA headers, or through ptxas.
namespace {

// The architecture the kernels were compiled for, divided by ten, or zero for the host.
__global__ void architecture(uint32_t *out) {
#ifdef __CUDA_ARCH__
  *out = __CUDA_ARCH__ / 10;
#else
  *out = 0;
#endif
}

uint32_t compiled() {
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>{0U});
  architecture<<<1, 1>>>(out.data);
  finish("architecture");
  return out.download()[0];
}

const std::vector<uint32_t> Words = {0x00000000U, 0x01020304U, 0x04030201U, 0x7f80ff01U, 0x80017f00U, 0xffff0001U,
                                     0x00020003U, 0xfffffffbU, 0x12345678U, 0xdeadbeefU, 0x7fffffffU, 0x80000000U,
                                     0x0000ffffU, 0x55aa55aaU, 0xa5a5a5a5U, 0x00ff00ffU};

uint32_t lane(uint32_t word, int index, int bits) { return (word >> (index * bits)) & ((1U << bits) - 1U); }
int32_t signedLane(uint32_t word, int index, int bits) {
  const uint32_t value = lane(word, index, bits);
  return (value >> (bits - 1)) ? static_cast<int32_t>(value) - (1 << bits) : static_cast<int32_t>(value);
}

// __sad, __usad, __dp4a, __dp2a_lo, __dp2a_hi, __fns and the SIMD intrinsics over every pair of the words.
__global__ void integerIntrinsics(const uint32_t *a, const uint32_t *b, uint32_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count * count) return;
  const uint32_t x = a[i / count];
  const uint32_t y = b[i % count];
  uint32_t *o = out + (static_cast<size_t>(i) * 12);
  o[0] = static_cast<uint32_t>(__sad(static_cast<int>(x), static_cast<int>(y), 7U));
  o[1] = __usad(x, y, 7U);
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 610
  o[2] = static_cast<uint32_t>(__dp4a(static_cast<int>(x), static_cast<int>(y), 100));
  o[3] = __dp4a(x, y, 100U);
  o[4] = static_cast<uint32_t>(__dp2a_lo(static_cast<int>(x), static_cast<int>(y), 100));
  o[5] = __dp2a_hi(x, y, 100U);
#endif
  o[6] = __vabsdiffu4(x, y);
  o[7] = __vsadu4(x, y);
  o[8] = __vadd2(x, y);
  o[9] = __vmaxs4(x, y);
  o[10] = __vcmpgeu4(x, y);
  o[11] = __fns(x, y & 31U, static_cast<int>(y % 5U) - 2);
}

uint32_t findNthSet(uint32_t mask, uint32_t base, int offset) {
  if (offset == 0) return (mask >> base) & 1U ? base : 0xffffffffU;
  int count = std::abs(offset) - 1;
  for (int position = static_cast<int>(base); position >= 0 && position < 32; position += offset > 0 ? 1 : -1)
    if ((mask >> position) & 1U) {
      if (count == 0) return static_cast<uint32_t>(position);
      --count;
    }
  return 0xffffffffU;
}

std::vector<uint32_t> integerReference(uint32_t x, uint32_t y) {
  const int32_t sx = static_cast<int32_t>(x);
  const int32_t sy = static_cast<int32_t>(y);
  int32_t dp4s = 100;
  int32_t dp2s = 100;
  uint32_t dp4u = 100;
  uint32_t dp2u = 100;
  uint32_t absdiff = 0;
  uint32_t sad = 0;
  uint32_t add = 0;
  uint32_t max = 0;
  uint32_t compare = 0;
  for (int i = 0; i < 4; ++i) {
    dp4s += signedLane(x, i, 8) * signedLane(y, i, 8);
    dp4u += lane(x, i, 8) * lane(y, i, 8);
    const uint32_t difference = lane(x, i, 8) > lane(y, i, 8) ? lane(x, i, 8) - lane(y, i, 8) : lane(y, i, 8) - lane(x, i, 8);
    absdiff |= difference << (8 * i);
    sad += difference;
    max |= static_cast<uint32_t>(std::max(signedLane(x, i, 8), signedLane(y, i, 8)) & 0xff) << (8 * i);
    compare |= (lane(x, i, 8) >= lane(y, i, 8) ? 0xffU : 0U) << (8 * i);
  }
  for (int i = 0; i < 2; ++i) {
    dp2s += signedLane(x, i, 16) * signedLane(y, i, 8);
    dp2u += lane(x, i, 16) * lane(y, i + 2, 8);
    add |= ((lane(x, i, 16) + lane(y, i, 16)) & 0xffffU) << (16 * i);
  }
  const uint32_t sadSigned = 7U + static_cast<uint32_t>(sx > sy ? static_cast<int64_t>(sx) - sy : static_cast<int64_t>(sy) - sx);
  const uint32_t sadUnsigned = 7U + (x > y ? x - y : y - x);
  return {sadSigned,
          sadUnsigned,
          static_cast<uint32_t>(dp4s),
          dp4u,
          static_cast<uint32_t>(dp2s),
          dp2u,
          absdiff,
          sad,
          add,
          max,
          compare,
          findNthSet(x, y & 31U, static_cast<int>(y % 5U) - 2)};
}

} // namespace

TEST_CASE("intrinsics/cuda/integer", "[unified][intrinsics]") {
  const uint32_t count = static_cast<uint32_t>(Words.size());
  DeviceBuffer<uint32_t> a(Words);
  DeviceBuffer<uint32_t> b(Words);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(count) * count * 12));
  integerIntrinsics<<<((count * count) + 63) / 64, 64>>>(a.data, b.data, out.data, count);
  finish("integer intrinsics");
  const auto actual = out.download();
  // the dot products need compute capability 6.1
  const bool dots = compiled() >= 61;
  for (uint32_t i = 0; i < count * count; ++i) {
    const auto expected = integerReference(Words[i / count], Words[i % count]);
    for (size_t field = 0; field < expected.size(); ++field)
      if ((dots || field < 2 || field > 5) && actual[(static_cast<size_t>(i) * 12) + field] != expected[field])
        throw std::runtime_error(fmt::format("integer intrinsic {} of {:#x} and {:#x}: expected {:#x}, got {:#x}", field, Words[i / count],
                                             Words[i % count], expected[field], actual[(static_cast<size_t>(i) * 12) + field]));
  }
}

namespace {
// atomicInc, atomicDec, unused atomicAdd results, which compile to reductions, and cuda::atomic_ref, whose relaxed
// fetch_add is atom.add.relaxed.gpu.s32.
__global__ void atomics(uint32_t *counters, float *sum, int *signedSum) {
  atomicInc(&counters[0], 9U);
  atomicDec(&counters[1], 9U);
  atomicAdd(&counters[2], 3U);
  atomicAdd(sum, 0.5F);
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 600
  cuda::atomic_ref<int, cuda::thread_scope_device>(*signedSum).fetch_add(-2, cuda::std::memory_order_relaxed);
#else
  atomicAdd(signedSum, -2);
#endif
}

} // namespace

TEST_CASE("intrinsics/cuda/atomics", "[unified][intrinsics]") {
  constexpr uint32_t threads = 256;
  DeviceBuffer<uint32_t> counters(std::vector<uint32_t>{0U, 0U, 0U});
  DeviceBuffer<float> sum(std::vector<float>{0.0F});
  DeviceBuffer<int> signedSum(std::vector<int>{5});
  atomics<<<2, threads / 2>>>(counters.data, sum.data, signedSum.data);
  finish("atomics");
  // 256 increments wrapping after 9 leave 256 % 10; decrements from 0 wrap to 9 and count down.
  expectEqual(counters.download(), {threads % 10U, (10U - (threads % 10U)) % 10U, 3U * threads}, "atomics");
  if (sum.download()[0] != 0.5F * threads) throw std::runtime_error("atomic float sum mismatch");
  if (signedSum.download()[0] != 5 - (2 * static_cast<int>(threads))) throw std::runtime_error("atomic_ref sum mismatch");
}

namespace {
__device__ uint32_t bits32(__half2_raw value) {
  uint32_t bits = 0;
  memcpy(&bits, &value, sizeof(bits));
  return bits;
}

__device__ uint32_t bits32(__nv_bfloat162_raw value) {
  uint32_t bits = 0;
  memcpy(&bits, &value, sizeof(bits));
  return bits;
}

// __floats2half2_rn, __floats2bfloat162_rn, __heq2, __hisnan2 and, on sm_89 and later, the 8-bit float conversions.
__global__ void halves(const float *in, uint32_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count) return;
  const float x = in[i];
  const float y = in[(i + 1) % count];
  const __half2 h = __floats2half2_rn(x, y);
  const __nv_bfloat162 b = __floats2bfloat162_rn(x, y);
  out[static_cast<size_t>(i) * 5] = bits32(h);
  out[(i * 5) + 1] = bits32(b);
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ < 530
  // Packed half comparisons need sm_53; compare the widened halves.
  const float2 wide = __half22float2(h), same = __half22float2(__floats2half2_rn(x, x));
  out[i * 5 + 2] = (wide.x == same.x ? 0x3c00u : 0u) | (wide.y == same.y ? 0x3c000000u : 0u);
  out[i * 5 + 3] = (wide.x != wide.x ? 0x3c00u : 0u) | (wide.y != wide.y ? 0x3c000000u : 0u);
#else
  out[(i * 5) + 2] = bits32(__heq2(h, __floats2half2_rn(x, x)));
  out[(i * 5) + 3] = bits32(__hisnan2(h));
#endif
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 890 && CUDART_VERSION >= 11080
  out[(i * 5) + 4] = __nv_cvt_float2_to_fp8x2(make_float2(y, x), __NV_SATFINITE, __NV_E4M3);
#else
  out[(i * 5) + 4] = 0xffffffffU;
#endif
}

// CUDA 11.8 declares __half_as_ushort and __bfloat16_as_ushort for the device only.
template <typename T> uint16_t bits16(T value) {
  uint16_t bits = 0;
  std::memcpy(&bits, &value, sizeof(bits));
  return bits;
}

} // namespace

TEST_CASE("intrinsics/cuda/half-precision", "[unified][intrinsics]") {
  const std::vector<float> inputs = {1.5F, -2.0F, 0.0F, 65504.0F, 1.0e6F, 1.0e-8F, -0.0F, 3.140625F};
  const uint32_t count = static_cast<uint32_t>(inputs.size());
  DeviceBuffer<float> in(inputs);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(count) * 5));
  halves<<<1, count>>>(in.data, out.data, count);
  finish("half precision");
  const auto actual = out.download();
  for (uint32_t i = 0; i < count; ++i) {
    const float x = inputs[i];
    const float y = inputs[(i + 1) % count];
    const uint32_t packed = static_cast<uint32_t>(bits16(__float2half_rn(x))) | static_cast<uint32_t>(bits16(__float2half_rn(y))) << 16;
    const uint32_t bfloat =
        static_cast<uint32_t>(bits16(__float2bfloat16_rn(x))) | static_cast<uint32_t>(bits16(__float2bfloat16_rn(y))) << 16;
    const bool sameHigh = bits16(__float2half_rn(y)) == bits16(__float2half_rn(x))
                          || (__half2float(__float2half_rn(y)) == 0.0F && __half2float(__float2half_rn(x)) == 0.0F);
    const uint32_t equal = 0x3c00U | (sameHigh ? 0x3c000000U : 0U);
    expectEqual(actual ^ slice(size_t{i} * 5, (size_t{i} * 5) + 4), {packed, bfloat, equal, 0U}, fmt::format("half precision {}", i));
  }
}

namespace {
// __syncthreads_count, __syncthreads_and and __syncthreads_or: bar.red.popc.u32 and bar.red.{and,or}.pred.
__global__ void barrierReductions(const uint32_t *in, uint32_t *out) {
  const uint32_t value = in[threadIdx.x];
  const int count = __syncthreads_count(static_cast<int>(value & 1U));
  const int all = __syncthreads_and(value != 0U);
  const int any = __syncthreads_or(value > 100U);
  if (threadIdx.x == 0) {
    out[0] = static_cast<uint32_t>(count);
    out[1] = static_cast<uint32_t>(all);
    out[2] = static_cast<uint32_t>(any);
  }
}

} // namespace

TEST_CASE("intrinsics/cuda/barrier-reductions", "[unified][intrinsics]") {
  const auto inputs = iota(0U, 128U) | map([](uint32_t i) { return (i * 3U) + 1U; }) | to_vector();
  const auto odd = inputs ^ sum_by([](uint32_t value) { return value & 1U; });
  DeviceBuffer<uint32_t> in(inputs);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(3));
  barrierReductions<<<1, static_cast<uint32_t>(inputs.size())>>>(in.data, out.data);
  finish("barrier reductions");
  expectEqual(out.download(), {odd, 1U, 1U}, "barrier reductions");
}

namespace {
// clock64() and %smid: the cycle counter runs and the multiprocessor is one of %nsmid.
__global__ void counters(uint64_t *out) {
  const uint64_t start = clock64();
  uint32_t smid{};
  uint32_t nsmid{};
  asm volatile("mov.u32 %0, %%smid;" : "=r"(smid));
  asm volatile("mov.u32 %0, %%nsmid;" : "=r"(nsmid));
  const uint64_t end = clock64();
  out[static_cast<size_t>(blockIdx.x) * 2] = end > start ? 1U : 0U;
  out[(blockIdx.x * 2) + 1] = smid < nsmid ? 1U : 0U;
}

} // namespace

TEST_CASE("intrinsics/cuda/counters", "[unified][intrinsics]") {
  DeviceBuffer<uint64_t> out(std::vector<uint64_t>(16));
  counters<<<8, 32>>>(out.data);
  finish("counters");
  expectEqual(out.download(), std::vector<uint64_t>(16, 1U), "counters");
}
