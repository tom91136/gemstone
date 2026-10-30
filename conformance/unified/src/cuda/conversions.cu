#include <cmath>
#include <cstdint>
#include <cstring>
#include <stdexcept>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <cuda_fp16.h>
#if CUDART_VERSION >= 11080
  #include <cuda_fp8.h>
#endif
#if CUDART_VERSION >= 11000
  #include <mma.h>
#endif

#include "cuda/support.cuh"

#include "test_support.hpp"
#include <fmt/format.h>

// TF32 rounding (cvt.rna.tf32.f32 from wmma::__float_to_tf32, and the satfinite, rn, rz and relu forms) and the 8-bit
// float conversions the cuda_fp8.h intrinsics emit (cvt.rn.satfinite.{e4m3x2,e5m2x2}.{f32,f16x2} and
// cvt.rn.f16x2.{e4m3x2,e5m2x2}).
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

const std::vector<uint32_t> Inputs = {0x00000000U, 0x80000000U, 0x3f800000U, 0xbf800000U, 0x3f801000U, 0x3f803000U, 0x3f800fffU,
                                      0xbf801001U, 0x00000001U, 0x80001fffU, 0x007ff000U, 0x7f7fffffU, 0xff7ff000U, 0x7f7ff000U,
                                      0x7f800000U, 0xff800000U, 0x7fc00000U, 0x7f800001U, 0xffc12345U, 0x43e00000U, 0x43e80000U,
                                      0xc3f00000U, 0x47600000U, 0x47700000U, 0x3b000000U, 0x3a800000U, 0x37800000U, 0xb7f00000U,
                                      0x3c100000U, 0x40490fdbU, 0xc0490fdbU, 0x3e2aaaabU};

constexpr uint32_t Tf32Count = 7;

__global__ void tf32(const uint32_t *in, uint32_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count) return;
  [[maybe_unused]] const float x = __uint_as_float(in[i]);
  uint32_t *o = out + (static_cast<size_t>(i) * Tf32Count);
  for (uint32_t field = 0; field < Tf32Count; ++field)
    o[field] = 0xdeadbeefU;
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 800
  o[0] = __float_as_uint(nvcuda::wmma::__float_to_tf32(x));
  uint32_t d = 0;
  asm("cvt.rna.tf32.f32 %0, %1;" : "=r"(d) : "f"(x));
  o[1] = d;
  asm("cvt.rna.satfinite.tf32.f32 %0, %1;" : "=r"(d) : "f"(x));
  o[2] = d;
#endif
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 900
  asm("cvt.rn.tf32.f32 %0, %1;" : "=r"(d) : "f"(x));
  o[3] = d;
  asm("cvt.rz.tf32.f32 %0, %1;" : "=r"(d) : "f"(x));
  o[4] = d;
  asm("cvt.rn.relu.tf32.f32 %0, %1;" : "=r"(d) : "f"(x));
  o[5] = d;
#endif
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 1000
  asm("cvt.rn.satfinite.tf32.f32 %0, %1;" : "=r"(d) : "f"(x));
  o[6] = d;
#endif
}

using unified::support::isNan;

// TF32 keeps the upper 19 bits of a binary32 register; `mode` is 'a' (nearest, ties away), 'n' (nearest even) or 'z'.
// ptxas lowers 'a' to adding half a step to finite bit patterns, truncating NaN payloads, and its satfinite to taking
// the top exponent down one step.
uint32_t tf32Reference(uint32_t bits, char mode, bool satfinite, bool relu) {
  const uint32_t sign = bits & 0x80000000U;
  const uint32_t magnitude = bits & 0x7fffffffU;
  if (mode == 'a') {
    const uint32_t rounded = (magnitude < 0x7f800000U ? bits + 0x1000U : bits) & 0xffffe000U;
    return satfinite && (rounded & 0x7f800000U) == 0x7f800000U ? rounded - 0x2000U : rounded;
  }
  if (isNan(bits)) return 0x7fffe000U;
  if (relu && sign && magnitude != 0) return 0U;
  uint32_t rounded = magnitude;
  if (magnitude < 0x7f800000U) {
    const uint32_t dropped = magnitude & 0x1fffU;
    rounded = magnitude & ~0x1fffU;
    if (mode == 'n' && (dropped > 0x1000U || (dropped == 0x1000U && (rounded & 0x2000U)))) rounded += 0x2000U;
  }
  if (satfinite && rounded >= 0x7f800000U) rounded = 0x7f7fe000U;
  return relu && sign ? 0U : sign | rounded;
}

} // namespace

TEST_CASE("conversions/cuda/tf32", "[unified][conversions]") {
  const uint32_t arch = compiled();
  if (arch < 80) SKIP("TF32 conversions require compute capability 8.0");
  const uint32_t count = static_cast<uint32_t>(Inputs.size());
  DeviceBuffer<uint32_t> in(Inputs);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(count) * Tf32Count));
  tf32<<<1, count>>>(in.data, out.data, count);
  finish("tf32");
  const auto actual = out.download();
  for (size_t i = 0; i < count; ++i) {
    const uint32_t x = Inputs[i];
    std::vector<uint32_t> expected = {tf32Reference(x, 'a', false, false), tf32Reference(x, 'a', false, false),
                                      tf32Reference(x, 'a', true, false)};
    if (arch >= 90)
      expected.insert(expected.end(),
                      {tf32Reference(x, 'n', false, false), tf32Reference(x, 'z', false, false), tf32Reference(x, 'n', false, true)});
    if (arch >= 100) expected.push_back(tf32Reference(x, 'n', true, false));
    for (size_t field = 0; field < expected.size(); ++field)
      if (actual[(i * Tf32Count) + field] != expected[field])
        throw std::runtime_error(
            fmt::format("tf32 {} of {:#x}: expected {:#x}, got {:#x}", field, x, expected[field], actual[(i * Tf32Count) + field]));
  }
}

namespace {
constexpr uint32_t Fp8Count = 8;

#if CUDART_VERSION >= 11080
__global__ void fp8(const uint32_t *in, uint32_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count * count) return;
  const float x = __uint_as_float(in[i / count]);
  const float y = __uint_as_float(in[i % count]);
  uint32_t *o = out + (static_cast<size_t>(i) * Fp8Count);
  o[0] = __nv_cvt_float2_to_fp8x2(make_float2(y, x), __NV_SATFINITE, __NV_E4M3);
  o[1] = __nv_cvt_float2_to_fp8x2(make_float2(y, x), __NV_SATFINITE, __NV_E5M2);
  const __half2_raw pair = __nv_cvt_fp8x2_to_halfraw2(static_cast<__nv_fp8x2_storage_t>(in[i / count] >> 8), __NV_E4M3);
  o[2] = static_cast<uint32_t>(pair.x) | static_cast<uint32_t>(pair.y) << 16;
  const __half2_raw wide = __nv_cvt_fp8x2_to_halfraw2(static_cast<__nv_fp8x2_storage_t>(in[i % count] >> 16), __NV_E5M2);
  o[3] = static_cast<uint32_t>(wide.x) | static_cast<uint32_t>(wide.y) << 16;
  __half2_raw halves;
  halves.x = static_cast<unsigned short>(in[i / count]);
  halves.y = static_cast<unsigned short>(in[i % count] >> 16);
  o[4] = __nv_cvt_halfraw2_to_fp8x2(halves, __NV_SATFINITE, __NV_E4M3);
  o[5] = __nv_cvt_halfraw2_to_fp8x2(halves, __NV_SATFINITE, __NV_E5M2);
  o[6] = 0xdeadbeefU;
  o[7] = 0xdeadbeefU;
  #if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 890
  unsigned short d = 0;
  asm("cvt.rn.satfinite.relu.e4m3x2.f32 %0, %1, %2;" : "=h"(d) : "f"(x), "f"(y));
  o[6] = d;
  asm("cvt.rn.satfinite.relu.e5m2x2.f32 %0, %1, %2;" : "=h"(d) : "f"(x), "f"(y));
  o[7] = d;
  #endif
}

uint32_t relu(uint32_t pair, __nv_fp8_interpretation_t format) {
  uint32_t result = 0;
  for (int shift = 0; shift < 16; shift += 8) {
    const uint32_t code = (pair >> shift) & 0xffU;
    const bool nan = format == __NV_E4M3 ? (code & 0x7fU) == 0x7fU : (code & 0x7fU) > 0x7cU;
    if (nan) result |= 0x7fU << shift;
    else if ((code & 0x80U) == 0) result |= code << shift;
  }
  return result;
}

std::vector<uint32_t> fp8Reference(uint32_t a, uint32_t b) {
  const float x = unified::support::bitsFloat(a);
  const float y = unified::support::bitsFloat(b);
  const __half2_raw pair = __nv_cvt_fp8x2_to_halfraw2(static_cast<__nv_fp8x2_storage_t>(a >> 8), __NV_E4M3);
  const __half2_raw wide = __nv_cvt_fp8x2_to_halfraw2(static_cast<__nv_fp8x2_storage_t>(b >> 16), __NV_E5M2);
  __half2_raw halves;
  halves.x = static_cast<unsigned short>(a);
  halves.y = static_cast<unsigned short>(b >> 16);
  return {__nv_cvt_float2_to_fp8x2(make_float2(y, x), __NV_SATFINITE, __NV_E4M3),
          __nv_cvt_float2_to_fp8x2(make_float2(y, x), __NV_SATFINITE, __NV_E5M2),
          static_cast<uint32_t>(pair.x) | static_cast<uint32_t>(pair.y) << 16,
          static_cast<uint32_t>(wide.x) | static_cast<uint32_t>(wide.y) << 16,
          __nv_cvt_halfraw2_to_fp8x2(halves, __NV_SATFINITE, __NV_E4M3),
          __nv_cvt_halfraw2_to_fp8x2(halves, __NV_SATFINITE, __NV_E5M2),
          relu(__nv_cvt_float2_to_fp8x2(make_float2(y, x), __NV_SATFINITE, __NV_E4M3), __NV_E4M3),
          relu(__nv_cvt_float2_to_fp8x2(make_float2(y, x), __NV_SATFINITE, __NV_E5M2), __NV_E5M2)};
}
#endif

} // namespace

TEST_CASE("conversions/cuda/fp8", "[unified][conversions]") {
#if CUDART_VERSION >= 11080
  if (compiled() < 89) SKIP("8-bit float conversions require compute capability 8.9");
  const uint32_t count = static_cast<uint32_t>(Inputs.size());
  DeviceBuffer<uint32_t> in(Inputs);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(count) * count * Fp8Count));
  fp8<<<((count * count) + 63) / 64, 64>>>(in.data, out.data, count);
  finish("fp8");
  const auto actual = out.download();
  for (uint32_t i = 0; i < count * count; ++i) {
    const auto expected = fp8Reference(Inputs[i / count], Inputs[i % count]);
    for (size_t field = 0; field < expected.size(); ++field)
      if (actual[(static_cast<size_t>(i) * Fp8Count) + field] != expected[field])
        throw std::runtime_error(fmt::format("fp8 {} of {:#x} and {:#x}: expected {:#x}, got {:#x}", field, Inputs[i / count],
                                             Inputs[i % count], expected[field], actual[(static_cast<size_t>(i) * Fp8Count) + field]));
  }
#else
  SKIP("8-bit float conversions require CUDA 11.8");
#endif
}
