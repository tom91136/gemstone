#include <algorithm>
#include <cmath>
#include <cstdint>
#include <cstring>
#include <functional>
#include <random>
#include <stdexcept>
#include <string>
#include <utility>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <hip/hip_runtime.h>

#include "conformance/device.hpp"

#include "support.hpp"
#include "test_support.hpp"

// One wave runs one matrix instruction through its compiler builtin on registers the host packs from AMD's documented
// layouts (the CDNA ISA's MFMA chapter, AMD's matrix instruction calculator for gfx11 and gfx12, and the CDNA4 tables for
// gfx950's split and scaled forms); the host computes D from the same registers. Elements are small dyadic values, so
// every sum is exact whatever the accumulation order.

// The wave64 forms build from matrix_wave64.hip, which includes this file compiled for wave64; the anonymous namespace
// keeps the two translation units' kernels and helpers apart.
namespace unified_matrix { namespace {

#ifdef __gfx908__
  #define UNIFIED_CDNA 1
#elifdef __gfx90a__
  #define UNIFIED_CDNA 2
#elif defined(__gfx940__) || defined(__gfx941__) || defined(__gfx942__)
  #define UNIFIED_CDNA 3
#elifdef __gfx950__
  #define UNIFIED_CDNA 4
#else
  #define UNIFIED_CDNA 0
#endif

#if defined(__HIP_DEVICE_COMPILE__) && UNIFIED_CDNA >= 1
  #define UNIFIED_CDNA1_4(...) return __VA_ARGS__;
#else
  #define UNIFIED_CDNA1_4(...) return c;
#endif
#if defined(__HIP_DEVICE_COMPILE__) && UNIFIED_CDNA >= 1 && UNIFIED_CDNA <= 2
  #define UNIFIED_CDNA1_2(...) return __VA_ARGS__;
#else
  #define UNIFIED_CDNA1_2(...) return c;
#endif
#if defined(__HIP_DEVICE_COMPILE__) && UNIFIED_CDNA >= 2
  #define UNIFIED_CDNA2_4(...) return __VA_ARGS__;
#else
  #define UNIFIED_CDNA2_4(...) return c;
#endif
#if defined(__HIP_DEVICE_COMPILE__) && UNIFIED_CDNA == 3
  #define UNIFIED_CDNA3_3(...) return __VA_ARGS__;
#else
  #define UNIFIED_CDNA3_3(...) return c;
#endif
#if defined(__HIP_DEVICE_COMPILE__) && UNIFIED_CDNA >= 3
  #define UNIFIED_CDNA3_4(...) return __VA_ARGS__;
#else
  #define UNIFIED_CDNA3_4(...) return c;
#endif
#if defined(__HIP_DEVICE_COMPILE__) && UNIFIED_CDNA == 4
  #define UNIFIED_CDNA4_4(...) return __VA_ARGS__;
#else
  #define UNIFIED_CDNA4_4(...) return c;
#endif
#if defined(__HIP_DEVICE_COMPILE__) && defined(__GFX11__)
  #define UNIFIED_GFX11(...) return __VA_ARGS__;
#else
  #define UNIFIED_GFX11(...) return c;
#endif
#if defined(__HIP_DEVICE_COMPILE__) && defined(__GFX12__)
  #define UNIFIED_GFX12(...) return __VA_ARGS__;
#else
  #define UNIFIED_GFX12(...) return c;
#endif

template <class T, int N> using Vector = T __attribute__((ext_vector_type(N)));

// Families the variants run on, as bits.
constexpr unsigned Cdna1 = 1, Cdna2 = 2, Cdna3 = 4, Cdna4 = 8, Gfx11 = 16, Gfx12 = 32;

#define UNIFIED_OP(NAME, GROUP, TA, TB, TC, ...)                                                                                           \
  struct NAME {                                                                                                                            \
    using A = TA;                                                                                                                          \
    using B = TB;                                                                                                                          \
    using C = TC;                                                                                                                          \
    static __device__ C run(A a, B b, C c, int scaleA, int scaleB, int index) {                                                            \
      (void)a;                                                                                                                             \
      (void)b;                                                                                                                             \
      (void)scaleA;                                                                                                                        \
      (void)scaleB;                                                                                                                        \
      (void)index;                                                                                                                         \
      GROUP(__VA_ARGS__)                                                                                                                   \
    }                                                                                                                                      \
  };

using F4 = Vector<float, 4>;
using F16v = Vector<float, 16>;
using F32v = Vector<float, 32>;
using H4 = Vector<_Float16, 4>;
using H8 = Vector<_Float16, 8>;
using H16 = Vector<_Float16, 16>;
using S2 = Vector<short, 2>;
using S4 = Vector<short, 4>;
using S8 = Vector<short, 8>;
using S16 = Vector<short, 16>;
using Y8 = Vector<__bf16, 8>;
using Y16 = Vector<__bf16, 16>;
using Vi2 = Vector<int, 2>;
using Vi4 = Vector<int, 4>;
using Vi8 = Vector<int, 8>;
using Vi16 = Vector<int, 16>;
using Vi32 = Vector<int, 32>;
using D4 = Vector<double, 4>;
using F2 = Vector<float, 2>;
using F8 = Vector<float, 8>;

#ifndef UNIFIED_MATRIX_WAVE64
// CDNA1 and later: f32, f16 and the multi-block i8 shapes; the f32 2-block form broadcasts and permutes
UNIFIED_OP(F32x32x1, UNIFIED_CDNA1_4, float, float, F32v, __builtin_amdgcn_mfma_f32_32x32x1f32(a, b, c, 0, 0, 0))
UNIFIED_OP(F32x32x1Broadcast, UNIFIED_CDNA1_4, float, float, F32v, __builtin_amdgcn_mfma_f32_32x32x1f32(a, b, c, 1, 1, 3))
UNIFIED_OP(F32x16x1, UNIFIED_CDNA1_4, float, float, F16v, __builtin_amdgcn_mfma_f32_16x16x1f32(a, b, c, 2, 3, 0))
UNIFIED_OP(F32x4x1, UNIFIED_CDNA1_4, float, float, F4, __builtin_amdgcn_mfma_f32_4x4x1f32(a, b, c, 4, 9, 5))
UNIFIED_OP(F32x32x2, UNIFIED_CDNA1_4, float, float, F16v, __builtin_amdgcn_mfma_f32_32x32x2f32(a, b, c, 0, 0, 0))
UNIFIED_OP(F32x16x4, UNIFIED_CDNA1_4, float, float, F4, __builtin_amdgcn_mfma_f32_16x16x4f32(a, b, c, 0, 0, 2))
UNIFIED_OP(F16x32x4, UNIFIED_CDNA1_4, H4, H4, F32v, __builtin_amdgcn_mfma_f32_32x32x4f16(a, b, c, 0, 0, 0))
UNIFIED_OP(F16x16x4, UNIFIED_CDNA1_4, H4, H4, F16v, __builtin_amdgcn_mfma_f32_16x16x4f16(a, b, c, 0, 0, 0))
UNIFIED_OP(F16x4x4, UNIFIED_CDNA1_4, H4, H4, F4, __builtin_amdgcn_mfma_f32_4x4x4f16(a, b, c, 3, 5, 7))
UNIFIED_OP(F16x32x8, UNIFIED_CDNA1_4, H4, H4, F16v, __builtin_amdgcn_mfma_f32_32x32x8f16(a, b, c, 0, 0, 0))
UNIFIED_OP(F16x16x16, UNIFIED_CDNA1_4, H4, H4, F4, __builtin_amdgcn_mfma_f32_16x16x16f16(a, b, c, 0, 0, 0))
UNIFIED_OP(I8x32x4, UNIFIED_CDNA1_4, int, int, Vi32, __builtin_amdgcn_mfma_i32_32x32x4i8(a, b, c, 0, 0, 0))
UNIFIED_OP(I8x16x4, UNIFIED_CDNA1_4, int, int, Vi16, __builtin_amdgcn_mfma_i32_16x16x4i8(a, b, c, 0, 0, 0))
UNIFIED_OP(I8x4x4, UNIFIED_CDNA1_4, int, int, Vi4, __builtin_amdgcn_mfma_i32_4x4x4i8(a, b, c, 0, 0, 0))
// CDNA1 and CDNA2: single-block i8 and pre-1k bf16
UNIFIED_OP(I8x32x8, UNIFIED_CDNA1_2, int, int, Vi16, __builtin_amdgcn_mfma_i32_32x32x8i8(a, b, c, 0, 0, 0))
UNIFIED_OP(I8x16x16, UNIFIED_CDNA1_2, int, int, Vi4, __builtin_amdgcn_mfma_i32_16x16x16i8(a, b, c, 0, 0, 0))
UNIFIED_OP(Bf16x32x2, UNIFIED_CDNA1_2, S2, S2, F32v, __builtin_amdgcn_mfma_f32_32x32x2bf16(a, b, c, 0, 0, 0))
UNIFIED_OP(Bf16x16x2, UNIFIED_CDNA1_2, S2, S2, F16v, __builtin_amdgcn_mfma_f32_16x16x2bf16(a, b, c, 0, 0, 0))
UNIFIED_OP(Bf16x4x2, UNIFIED_CDNA1_2, S2, S2, F4, __builtin_amdgcn_mfma_f32_4x4x2bf16(a, b, c, 0, 0, 0))
UNIFIED_OP(Bf16x32x4, UNIFIED_CDNA1_2, S2, S2, F16v, __builtin_amdgcn_mfma_f32_32x32x4bf16(a, b, c, 0, 0, 0))
UNIFIED_OP(Bf16x16x8, UNIFIED_CDNA1_2, S2, S2, F4, __builtin_amdgcn_mfma_f32_16x16x8bf16(a, b, c, 0, 0, 0))
// CDNA2 and later: bf16_1k and f64; the f64 forms negate by BLGP from CDNA3
UNIFIED_OP(Bf16x32x4k, UNIFIED_CDNA2_4, S4, S4, F32v, __builtin_amdgcn_mfma_f32_32x32x4bf16_1k(a, b, c, 0, 0, 0))
UNIFIED_OP(Bf16x16x4k, UNIFIED_CDNA2_4, S4, S4, F16v, __builtin_amdgcn_mfma_f32_16x16x4bf16_1k(a, b, c, 0, 0, 0))
UNIFIED_OP(Bf16x4x4k, UNIFIED_CDNA2_4, S4, S4, F4, __builtin_amdgcn_mfma_f32_4x4x4bf16_1k(a, b, c, 0, 0, 0))
UNIFIED_OP(Bf16x32x8k, UNIFIED_CDNA2_4, S4, S4, F16v, __builtin_amdgcn_mfma_f32_32x32x8bf16_1k(a, b, c, 0, 0, 0))
UNIFIED_OP(Bf16x16x16k, UNIFIED_CDNA2_4, S4, S4, F4, __builtin_amdgcn_mfma_f32_16x16x16bf16_1k(a, b, c, 0, 0, 0))
UNIFIED_OP(F64x16x4, UNIFIED_CDNA2_4, double, double, D4, __builtin_amdgcn_mfma_f64_16x16x4f64(a, b, c, 0, 0, 0))
UNIFIED_OP(F64x4x4, UNIFIED_CDNA2_4, double, double, double, __builtin_amdgcn_mfma_f64_4x4x4f64(a, b, c, 0, 0, 0))
UNIFIED_OP(F64x16x4Negated, UNIFIED_CDNA3_4, double, double, D4, __builtin_amdgcn_mfma_f64_16x16x4f64(a, b, c, 0, 0, 5))
// CDNA3 alone: xf32
UNIFIED_OP(Xf32x16x8, UNIFIED_CDNA3_3, F2, F2, F4, __builtin_amdgcn_mfma_f32_16x16x8_xf32(a, b, c, 0, 0, 0))
UNIFIED_OP(Xf32x32x4, UNIFIED_CDNA3_3, F2, F2, F16v, __builtin_amdgcn_mfma_f32_32x32x4_xf32(a, b, c, 0, 0, 0))
// CDNA3 and later: double-depth i8, fp8 and bf8, and the 4:2 sparse SMFMAC
UNIFIED_OP(I8x16x32, UNIFIED_CDNA3_4, long, long, Vi4, __builtin_amdgcn_mfma_i32_16x16x32_i8(a, b, c, 0, 0, 0))
UNIFIED_OP(I8x32x16, UNIFIED_CDNA3_4, long, long, Vi16, __builtin_amdgcn_mfma_i32_32x32x16_i8(a, b, c, 0, 0, 0))
UNIFIED_OP(Fp8Fp8x16, UNIFIED_CDNA3_4, long, long, F4, __builtin_amdgcn_mfma_f32_16x16x32_fp8_fp8(a, b, c, 0, 0, 0))
UNIFIED_OP(Fp8Bf8x16, UNIFIED_CDNA3_4, long, long, F4, __builtin_amdgcn_mfma_f32_16x16x32_fp8_bf8(a, b, c, 0, 0, 0))
UNIFIED_OP(Bf8Fp8x16, UNIFIED_CDNA3_4, long, long, F4, __builtin_amdgcn_mfma_f32_16x16x32_bf8_fp8(a, b, c, 0, 0, 0))
UNIFIED_OP(Bf8Bf8x16, UNIFIED_CDNA3_4, long, long, F4, __builtin_amdgcn_mfma_f32_16x16x32_bf8_bf8(a, b, c, 0, 0, 0))
UNIFIED_OP(Fp8Fp8x32, UNIFIED_CDNA3_4, long, long, F16v, __builtin_amdgcn_mfma_f32_32x32x16_fp8_fp8(a, b, c, 0, 0, 0))
UNIFIED_OP(Bf8Bf8x32, UNIFIED_CDNA3_4, long, long, F16v, __builtin_amdgcn_mfma_f32_32x32x16_bf8_bf8(a, b, c, 0, 0, 0))
UNIFIED_OP(SparseF16x16, UNIFIED_CDNA3_4, H4, H8, F4, __builtin_amdgcn_smfmac_f32_16x16x32_f16(a, b, c, index, 0, 0))
UNIFIED_OP(SparseF16x16Set, UNIFIED_CDNA3_4, H4, H8, F4, __builtin_amdgcn_smfmac_f32_16x16x32_f16(a, b, c, index, 0, 2))
UNIFIED_OP(SparseF16x32, UNIFIED_CDNA3_4, H4, H8, F16v, __builtin_amdgcn_smfmac_f32_32x32x16_f16(a, b, c, index, 0, 0))
UNIFIED_OP(SparseBf16x16, UNIFIED_CDNA3_4, S4, S8, F4, __builtin_amdgcn_smfmac_f32_16x16x32_bf16(a, b, c, index, 0, 0))
UNIFIED_OP(SparseI8x16, UNIFIED_CDNA3_4, Vi2, Vi4, Vi4, __builtin_amdgcn_smfmac_i32_16x16x64_i8(a, b, c, index, 0, 1))
UNIFIED_OP(SparseI8x32, UNIFIED_CDNA3_4, Vi2, Vi4, Vi16, __builtin_amdgcn_smfmac_i32_32x32x32_i8(a, b, c, index, 0, 0))
UNIFIED_OP(SparseFp8Bf8x16, UNIFIED_CDNA3_4, Vi2, Vi4, F4, __builtin_amdgcn_smfmac_f32_16x16x64_fp8_bf8(a, b, c, index, 0, 0))
UNIFIED_OP(SparseBf8Fp8x32, UNIFIED_CDNA3_4, Vi2, Vi4, F16v, __builtin_amdgcn_smfmac_f32_32x32x32_bf8_fp8(a, b, c, index, 0, 0))
// CDNA4: double-rate f16, bf16 and i8, f8f6f4 with and without E8M0 scales, and the wider SMFMAC
UNIFIED_OP(F16x16x32, UNIFIED_CDNA4_4, H8, H8, F4, __builtin_amdgcn_mfma_f32_16x16x32_f16(a, b, c, 0, 0, 0))
UNIFIED_OP(F16x32x16, UNIFIED_CDNA4_4, H8, H8, F16v, __builtin_amdgcn_mfma_f32_32x32x16_f16(a, b, c, 0, 0, 0))
UNIFIED_OP(Bf16x16x32, UNIFIED_CDNA4_4, Y8, Y8, F4, __builtin_amdgcn_mfma_f32_16x16x32_bf16(a, b, c, 0, 0, 0))
UNIFIED_OP(Bf16x32x16, UNIFIED_CDNA4_4, Y8, Y8, F16v, __builtin_amdgcn_mfma_f32_32x32x16_bf16(a, b, c, 0, 0, 0))
UNIFIED_OP(I8x16x64, UNIFIED_CDNA4_4, Vi4, Vi4, Vi4, __builtin_amdgcn_mfma_i32_16x16x64_i8(a, b, c, 0, 0, 0))
UNIFIED_OP(I8x32x32, UNIFIED_CDNA4_4, Vi4, Vi4, Vi16, __builtin_amdgcn_mfma_i32_32x32x32_i8(a, b, c, 0, 0, 0))
UNIFIED_OP(Fp8Fp8x16Ocp, UNIFIED_CDNA4_4, long, long, F4, __builtin_amdgcn_mfma_f32_16x16x32_fp8_fp8(a, b, c, 0, 0, 0))
UNIFIED_OP(Bf8Fp8x32Ocp, UNIFIED_CDNA4_4, long, long, F16v, __builtin_amdgcn_mfma_f32_32x32x16_bf8_fp8(a, b, c, 0, 0, 0))
UNIFIED_OP(F8F6F4Unscaled, UNIFIED_CDNA4_4, Vi8, Vi8, F4, __builtin_amdgcn_mfma_scale_f32_16x16x128_f8f6f4(a, b, c, 0, 0, 0, 0, 0, 0))
UNIFIED_OP(F8F6F4Fp8Fp4, UNIFIED_CDNA4_4, Vi8, Vi8, F4,
           __builtin_amdgcn_mfma_scale_f32_16x16x128_f8f6f4(a, b, c, 0, 4, 1, scaleA, 2, scaleB))
UNIFIED_OP(F8F6F4Fp6Bf8, UNIFIED_CDNA4_4, Vi8, Vi8, F16v,
           __builtin_amdgcn_mfma_scale_f32_32x32x64_f8f6f4(a, b, c, 2, 1, 0, scaleA, 3, scaleB))
UNIFIED_OP(F8F6F4Bf6Fp6, UNIFIED_CDNA4_4, Vi8, Vi8, F4,
           __builtin_amdgcn_mfma_scale_f32_16x16x128_f8f6f4(a, b, c, 3, 2, 0, scaleA, 0, scaleB))
UNIFIED_OP(SparseF16x16x64, UNIFIED_CDNA4_4, H8, H16, F4, __builtin_amdgcn_smfmac_f32_16x16x64_f16(a, b, c, index, 0, 1))
UNIFIED_OP(SparseBf16x32x32, UNIFIED_CDNA4_4, Y8, Y16, F16v, __builtin_amdgcn_smfmac_f32_32x32x32_bf16(a, b, c, index, 0, 0))
UNIFIED_OP(SparseI8x16x128, UNIFIED_CDNA4_4, Vi4, Vi8, Vi4, __builtin_amdgcn_smfmac_i32_16x16x128_i8(a, b, c, index, 0, 0))
UNIFIED_OP(SparseFp8x32x64, UNIFIED_CDNA4_4, Vi4, Vi8, F16v, __builtin_amdgcn_smfmac_f32_32x32x64_fp8_fp8(a, b, c, index, 0, 0))
// gfx11 wave32 WMMA
UNIFIED_OP(Wmma11F16, UNIFIED_GFX11, H16, H16, F8, __builtin_amdgcn_wmma_f32_16x16x16_f16_w32(a, b, c))
UNIFIED_OP(Wmma11Bf16, UNIFIED_GFX11, S16, S16, F8, __builtin_amdgcn_wmma_f32_16x16x16_bf16_w32(a, b, c))
UNIFIED_OP(Wmma11F16Out, UNIFIED_GFX11, H16, H16, H16, __builtin_amdgcn_wmma_f16_16x16x16_f16_w32(a, b, c, false))
UNIFIED_OP(Wmma11F16High, UNIFIED_GFX11, H16, H16, H16, __builtin_amdgcn_wmma_f16_16x16x16_f16_w32(a, b, c, true))
UNIFIED_OP(Wmma11Bf16Out, UNIFIED_GFX11, S16, S16, S16, __builtin_amdgcn_wmma_bf16_16x16x16_bf16_w32(a, b, c, false))
UNIFIED_OP(Wmma11Iu8, UNIFIED_GFX11, Vi4, Vi4, Vi8, __builtin_amdgcn_wmma_i32_16x16x16_iu8_w32(true, a, false, b, c, false))
UNIFIED_OP(Wmma11Iu8Clamp, UNIFIED_GFX11, Vi4, Vi4, Vi8, __builtin_amdgcn_wmma_i32_16x16x16_iu8_w32(false, a, true, b, c, true))
UNIFIED_OP(Wmma11Iu4, UNIFIED_GFX11, Vi2, Vi2, Vi8, __builtin_amdgcn_wmma_i32_16x16x16_iu4_w32(true, a, true, b, c, false))
// gfx12 wave32 WMMA and SWMMAC
UNIFIED_OP(Wmma12F16, UNIFIED_GFX12, H8, H8, F8, __builtin_amdgcn_wmma_f32_16x16x16_f16_w32_gfx12(a, b, c))
UNIFIED_OP(Wmma12Bf16, UNIFIED_GFX12, S8, S8, F8, __builtin_amdgcn_wmma_f32_16x16x16_bf16_w32_gfx12(a, b, c))
UNIFIED_OP(Wmma12F16Out, UNIFIED_GFX12, H8, H8, H8, __builtin_amdgcn_wmma_f16_16x16x16_f16_w32_gfx12(a, b, c))
UNIFIED_OP(Wmma12Bf16Out, UNIFIED_GFX12, S8, S8, S8, __builtin_amdgcn_wmma_bf16_16x16x16_bf16_w32_gfx12(a, b, c))
UNIFIED_OP(Wmma12Iu8, UNIFIED_GFX12, Vi2, Vi2, Vi8, __builtin_amdgcn_wmma_i32_16x16x16_iu8_w32_gfx12(false, a, true, b, c, false))
UNIFIED_OP(Wmma12Iu8Clamp, UNIFIED_GFX12, Vi2, Vi2, Vi8, __builtin_amdgcn_wmma_i32_16x16x16_iu8_w32_gfx12(true, a, true, b, c, true))
UNIFIED_OP(Wmma12Iu4, UNIFIED_GFX12, int, int, Vi8, __builtin_amdgcn_wmma_i32_16x16x16_iu4_w32_gfx12(true, a, false, b, c, false))
UNIFIED_OP(Wmma12Iu4x32, UNIFIED_GFX12, Vi2, Vi2, Vi8, __builtin_amdgcn_wmma_i32_16x16x32_iu4_w32_gfx12(true, a, true, b, c, false))
UNIFIED_OP(Wmma12Fp8Bf8, UNIFIED_GFX12, Vi2, Vi2, F8, __builtin_amdgcn_wmma_f32_16x16x16_fp8_bf8_w32_gfx12(a, b, c))
UNIFIED_OP(Wmma12Bf8Bf8, UNIFIED_GFX12, Vi2, Vi2, F8, __builtin_amdgcn_wmma_f32_16x16x16_bf8_bf8_w32_gfx12(a, b, c))
UNIFIED_OP(Swmmac12F16, UNIFIED_GFX12, H8, H16, F8, __builtin_amdgcn_swmmac_f32_16x16x32_f16_w32(a, b, c, index))
UNIFIED_OP(Swmmac12Bf16Out, UNIFIED_GFX12, S8, S16, S8, __builtin_amdgcn_swmmac_bf16_16x16x32_bf16_w32(a, b, c, index))
UNIFIED_OP(Swmmac12Iu8, UNIFIED_GFX12, Vi2, Vi4, Vi8, __builtin_amdgcn_swmmac_i32_16x16x32_iu8_w32(true, a, false, b, c, index, false))
UNIFIED_OP(Swmmac12Fp8Fp8, UNIFIED_GFX12, Vi2, Vi4, F8, __builtin_amdgcn_swmmac_f32_16x16x32_fp8_fp8_w32(a, b, c, index))
#else
// gfx11 wave64 WMMA
UNIFIED_OP(Wmma11F16, UNIFIED_GFX11, H16, H16, F4, __builtin_amdgcn_wmma_f32_16x16x16_f16_w64(a, b, c))
UNIFIED_OP(Wmma11Bf16, UNIFIED_GFX11, S16, S16, F4, __builtin_amdgcn_wmma_f32_16x16x16_bf16_w64(a, b, c))
UNIFIED_OP(Wmma11F16High, UNIFIED_GFX11, H16, H16, H8, __builtin_amdgcn_wmma_f16_16x16x16_f16_w64(a, b, c, true))
UNIFIED_OP(Wmma11Bf16Out, UNIFIED_GFX11, S16, S16, S8, __builtin_amdgcn_wmma_bf16_16x16x16_bf16_w64(a, b, c, false))
UNIFIED_OP(Wmma11Iu8, UNIFIED_GFX11, Vi4, Vi4, Vi4, __builtin_amdgcn_wmma_i32_16x16x16_iu8_w64(true, a, false, b, c, false))
UNIFIED_OP(Wmma11Iu4Clamp, UNIFIED_GFX11, Vi2, Vi2, Vi4, __builtin_amdgcn_wmma_i32_16x16x16_iu4_w64(true, a, true, b, c, true))
// gfx12 wave64 WMMA and SWMMAC
UNIFIED_OP(Wmma12F16, UNIFIED_GFX12, H4, H4, F4, __builtin_amdgcn_wmma_f32_16x16x16_f16_w64_gfx12(a, b, c))
UNIFIED_OP(Wmma12Bf16, UNIFIED_GFX12, S4, S4, F4, __builtin_amdgcn_wmma_f32_16x16x16_bf16_w64_gfx12(a, b, c))
UNIFIED_OP(Wmma12F16Out, UNIFIED_GFX12, H4, H4, H4, __builtin_amdgcn_wmma_f16_16x16x16_f16_w64_gfx12(a, b, c))
UNIFIED_OP(Wmma12Bf16Out, UNIFIED_GFX12, S4, S4, S4, __builtin_amdgcn_wmma_bf16_16x16x16_bf16_w64_gfx12(a, b, c))
UNIFIED_OP(Wmma12Iu8Clamp, UNIFIED_GFX12, int, int, Vi4, __builtin_amdgcn_wmma_i32_16x16x16_iu8_w64_gfx12(false, a, true, b, c, true))
UNIFIED_OP(Wmma12Iu4, UNIFIED_GFX12, int, int, Vi4, __builtin_amdgcn_wmma_i32_16x16x16_iu4_w64_gfx12(true, a, false, b, c, false))
UNIFIED_OP(Wmma12Iu4x32, UNIFIED_GFX12, int, int, Vi4, __builtin_amdgcn_wmma_i32_16x16x32_iu4_w64_gfx12(true, a, true, b, c, false))
UNIFIED_OP(Wmma12Fp8Bf8, UNIFIED_GFX12, int, int, F4, __builtin_amdgcn_wmma_f32_16x16x16_fp8_bf8_w64_gfx12(a, b, c))
UNIFIED_OP(Wmma12Bf8Fp8, UNIFIED_GFX12, int, int, F4, __builtin_amdgcn_wmma_f32_16x16x16_bf8_fp8_w64_gfx12(a, b, c))
UNIFIED_OP(Swmmac12F16, UNIFIED_GFX12, H4, H8, F4, __builtin_amdgcn_swmmac_f32_16x16x32_f16_w64(a, b, c, index))
UNIFIED_OP(Swmmac12Bf16Out, UNIFIED_GFX12, S4, S8, S4, __builtin_amdgcn_swmmac_bf16_16x16x32_bf16_w64(a, b, c, index))
UNIFIED_OP(Swmmac12Iu8, UNIFIED_GFX12, int, Vi2, Vi4, __builtin_amdgcn_swmmac_i32_16x16x32_iu8_w64(true, a, false, b, c, index, false))
UNIFIED_OP(Swmmac12Fp8Fp8, UNIFIED_GFX12, int, Vi2, F4, __builtin_amdgcn_swmmac_f32_16x16x32_fp8_fp8_w64(a, b, c, index))
#endif

template <class Op>
__global__ void matrixKernel(const uint32_t *a, const uint32_t *b, const uint32_t *c, uint32_t *d, const uint32_t *extra) {
  const unsigned lane = threadIdx.x;
  typename Op::A va;
  typename Op::B vb;
  typename Op::C vc;
  __builtin_memcpy(&va, a + (lane * (sizeof va / 4)), sizeof va);
  __builtin_memcpy(&vb, b + (lane * (sizeof vb / 4)), sizeof vb);
  __builtin_memcpy(&vc, c + (lane * (sizeof vc / 4)), sizeof vc);
  const typename Op::C vd =
      Op::run(va, vb, vc, static_cast<int>(extra[lane]), static_cast<int>(extra[64 + lane]), static_cast<int>(extra[128 + lane]));
  __builtin_memcpy(d + (lane * (sizeof vd / 4)), &vd, sizeof vd);
}

enum class Format { F64, F32, XF32, F16, BF16, I32, I8, U8, I4, U4, FP8, BF8, FP8FNUZ, BF8FNUZ, FP6, BF6, FP4 };
enum class Layout { Core, Wave11, Wave12 };

int width(Format format) {
  switch (format) {
    case Format::F64: return 64;
    case Format::F32:
    case Format::XF32:
    case Format::I32: return 32;
    case Format::F16:
    case Format::BF16: return 16;
    case Format::FP6:
    case Format::BF6: return 6;
    case Format::I4:
    case Format::U4:
    case Format::FP4: return 4;
    default: return 8;
  }
}

bool integer(Format format) {
  return format == Format::I32 || format == Format::I8 || format == Format::U8 || format == Format::I4 || format == Format::U4;
}

// The minifloat fields of a format: exponent and mantissa bits and bias.
struct Minifloat {
  int exponent, mantissa, bias;
};

Minifloat minifloat(Format format) {
  switch (format) {
    case Format::F16: return {.exponent = 5, .mantissa = 10, .bias = 15};
    case Format::BF16: return {.exponent = 8, .mantissa = 7, .bias = 127};
    case Format::FP8: return {.exponent = 4, .mantissa = 3, .bias = 7};
    case Format::BF8: return {.exponent = 5, .mantissa = 2, .bias = 15};
    case Format::FP8FNUZ: return {.exponent = 4, .mantissa = 3, .bias = 8};
    case Format::BF8FNUZ: return {.exponent = 5, .mantissa = 2, .bias = 16};
    case Format::FP6: return {.exponent = 2, .mantissa = 3, .bias = 1};
    case Format::BF6: return {.exponent = 3, .mantissa = 2, .bias = 3};
    case Format::FP4: return {.exponent = 2, .mantissa = 1, .bias = 1};
    default: throw std::logic_error("not a minifloat");
  }
}

// Bits of an exactly representable value.
uint64_t encode(Format format, double value) {
  switch (format) {
    case Format::F64: return unified::support::doubleBits(value);
    case Format::F32:
    case Format::XF32: return unified::support::floatBits(static_cast<float>(value));
    case Format::I32:
    case Format::I8:
    case Format::I4:
    case Format::U8:
    case Format::U4:
      return static_cast<uint64_t>(static_cast<int64_t>(value)) & (width(format) == 32 ? 0xffffffffULL : (1ULL << width(format)) - 1);
    default: {
      const auto [exponentBits, mantissaBits, bias] = minifloat(format);
      const uint64_t sign = value < 0 ? 1ULL << (exponentBits + mantissaBits) : 0;
      const double magnitude = std::fabs(value);
      if (magnitude == 0) return 0;
      int exponent = std::ilogb(magnitude);
      exponent = std::max(exponent, 1 - bias);
      const double scaled = std::ldexp(magnitude, mantissaBits - exponent);
      const uint64_t significand = static_cast<uint64_t>(scaled);
      if (static_cast<double>(significand) != scaled) throw std::logic_error("value not representable");
      const uint64_t biased = significand >> mantissaBits ? static_cast<uint64_t>(exponent + bias) : 0;
      return sign | (biased << mantissaBits) | (significand & ((1ULL << mantissaBits) - 1));
    }
  }
}

double decode(Format format, uint64_t bits) {
  switch (format) {
    case Format::F64: return unified::support::bitsDouble(bits);
    case Format::F32:
    case Format::XF32: return static_cast<double>(unified::support::bitsFloat(static_cast<uint32_t>(bits)));
    case Format::I32: return static_cast<double>(static_cast<int32_t>(static_cast<uint32_t>(bits)));
    case Format::I8: return static_cast<double>(static_cast<int8_t>(static_cast<uint8_t>(bits)));
    case Format::U8: return static_cast<double>(static_cast<uint8_t>(bits));
    case Format::I4: return static_cast<double>(static_cast<int>((bits & 15) ^ 8) - 8);
    case Format::U4: return static_cast<double>(bits & 15);
    default: {
      const auto [exponentBits, mantissaBits, bias] = minifloat(format);
      const int exponent = static_cast<int>((bits >> mantissaBits) & ((1U << exponentBits) - 1));
      const double fraction = static_cast<double>(bits & ((1ULL << mantissaBits) - 1));
      const double magnitude = exponent == 0
                                   ? std::ldexp(fraction, 1 - bias - mantissaBits)
                                   : std::ldexp(fraction + static_cast<double>(1ULL << mantissaBits), exponent - bias - mantissaBits);
      return (bits >> (exponentBits + mantissaBits)) & 1 ? -magnitude : magnitude;
    }
  }
}

struct Shape {
  Layout layout;
  int m, n, k, blocks;
  Format a, b, c;
  bool sparse = false;
  int chunkA = 0, chunkB = 0;
  int wave = 64;
};

struct Controls {
  int blockBroadcast = 0, blockId = 0, laneGroups = 0;
  int negate = 0;
  bool high = false;
  int indexSet = 0;
  bool clamp = false;
  bool scaled = false;
  int selectionA = 0, selectionB = 0;
};

struct Coordinates {
  int block, row, k;
};

int storedK(const Shape &shape) { return shape.sparse ? shape.k / 2 : shape.k; }

int inputItems(const Shape &shape, bool first) {
  const int depth = first ? storedK(shape) : shape.k;
  const int rows = first ? shape.m : shape.n;
  switch (shape.layout) {
    case Layout::Core: return depth * rows * shape.blocks / 64;
    case Layout::Wave11: return depth;
    default: {
      const int narrow = depth * 16 / 32;
      return shape.wave == 64 && narrow * width(first ? shape.a : shape.b) > 32 ? narrow / 2 : narrow;
    }
  }
}

// The element item `item` of `lane` holds of A (`first`) or B, from the registers-to-element formulas; false where the
// lane holds none.
bool input(const Shape &shape, bool first, int lane, int item, Coordinates &at) {
  const int rows = first ? shape.m : shape.n;
  const int depth = first ? storedK(shape) : shape.k;
  switch (shape.layout) {
    case Layout::Core: {
      const int count = depth * rows * shape.blocks / 64;
      const int chunk = first ? shape.chunkA : shape.chunkB;
      const int run = chunk ? chunk : count;
      const int groups = 64 / (rows * shape.blocks);
      at = {.block = lane / rows % shape.blocks,
            .row = lane % rows,
            .k = (run * groups * (item / run)) + (run * (lane / (rows * shape.blocks))) + (item % run)};
      return true;
    }
    case Layout::Wave11: at = {.block = 0, .row = lane % 16, .k = item}; return true;
    default: {
      const int narrow = depth * 16 / 32;
      const int run = width(first ? shape.a : shape.b) == 16 ? narrow / 2 : narrow;
      int folded = lane;
      int element = item;
      if (shape.wave == 64) {
        if (narrow * width(first ? shape.a : shape.b) > 32) {
          folded = lane % 32;
          element = item + (narrow / 2 * (lane / 32));
        } else if (lane >= 32) {
          return false;
        }
      }
      at = {.block = 0, .row = folded % 16, .k = (run * 2 * (element / run)) + (run * (folded / 16)) + (element % run)};
      return true;
    }
  }
}

int outputItems(const Shape &shape) { return shape.m * shape.n * shape.blocks / shape.wave; }

// The block, row and column of D that item `item` of `lane` holds.
Coordinates output(const Shape &shape, int lane, int item) {
  switch (shape.layout) {
    case Layout::Core: {
      const int height = shape.c == Format::F64 ? 1 : 4;
      const int perItem = std::max(1, 64 / (shape.n * shape.m / height));
      const int rows = 64 / perItem / shape.n;
      const int groups = shape.m / (height * rows);
      const int group = lane / shape.n;
      return {.block = (group % perItem) + (perItem * (item / (height * groups))),
              .row = (item % height) + (height * (group / perItem)) + (height * rows * (item / height % groups)),
              .k = lane % shape.n};
    }
    case Layout::Wave11: return {.block = 0, .row = (lane / 16) + (shape.wave / 16 * item), .k = lane % 16};
    default:
      return {.block = 0,
              .row = shape.wave == 32 ? (8 * (lane / 16)) + item : (8 * (lane / 16 % 2)) + item + (4 * (lane / 32)),
              .k = lane % 16};
  }
}

int outputBit(const Shape &shape, const Controls &controls, int item) {
  if (shape.layout == Layout::Wave11 && width(shape.c) == 16) return (32 * item) + (controls.high ? 16 : 0);
  return item * width(shape.c);
}

uint64_t bitsAt(const std::vector<uint32_t> &registers, int count, int lane, int bit, int bits) {
  uint64_t value = 0;
  for (int offset = 0; offset < bits; ++offset) {
    const int at = bit + offset;
    value |= static_cast<uint64_t>((registers[(lane * count) + (at / 32)] >> (at % 32)) & 1) << offset;
  }
  return value;
}

void setBits(std::vector<uint32_t> &registers, int count, int lane, int bit, int bits, uint64_t value) {
  for (int offset = 0; offset < bits; ++offset) {
    const int at = bit + offset;
    auto &word = registers[(lane * count) + (at / 32)];
    word = (word & ~(1U << (at % 32))) | (static_cast<uint32_t>((value >> offset) & 1) << (at % 32));
  }
}

using Launch = void (*)(const uint32_t *, const uint32_t *, const uint32_t *, uint32_t *, const uint32_t *, int);

template <class Op> void launch(const uint32_t *a, const uint32_t *b, const uint32_t *c, uint32_t *d, const uint32_t *extra, int wave) {
  hipLaunchKernelGGL(matrixKernel<Op>, dim3(1), dim3(wave), 0, nullptr, a, b, c, d, extra);
}

struct Variant {
  const char *name;
  unsigned families;
  Shape shape;
  Controls controls;
  Launch run;
  // dwords of each lane's A, B and C/D as the builtin takes them
  int aStride, bStride, cStride;
};

template <class Op> Variant variant(const char *name, unsigned families, Shape shape, Controls controls = {}) {
  return {name,
          families,
          shape,
          controls,
          launch<Op>,
          static_cast<int>(sizeof(typename Op::A) / 4),
          static_cast<int>(sizeof(typename Op::B) / 4),
          static_cast<int>(sizeof(typename Op::C) / 4)};
}

using hip_support::alignedDot;
using hip_support::alignedDots;
using hip_support::check;
using hip_support::DotResult;

// A value of `format` for the generator's draw: small dyadic magnitudes every format holds exactly.
double element(Format format, std::mt19937 &random, bool integral) {
  switch (format) {
    case Format::I8:
    case Format::I4: return static_cast<double>(static_cast<int>(random() % 15) - 7);
    case Format::U8:
    case Format::U4: return static_cast<double>(random() % 16);
    default: {
      const int draw = static_cast<int>(random() % 9) - 4;
      const int halved = draw / 2;
      return integral ? static_cast<double>(halved) : draw * 0.5;
    }
  }
}

DotResult dotResult(Format format) {
  switch (format) {
    case Format::F16: return DotResult::Half;
    case Format::BF16: return DotResult::BFloat16;
    default: return DotResult::Single;
  }
}

void runVariant(const Variant &variant, bool aligned) {
  const auto &shape = variant.shape;
  const auto &controls = variant.controls;
  std::mt19937 random(0x5eed + static_cast<unsigned>(std::hash<std::string>{}(variant.name)));
  const int lanes = shape.wave;
  const int aWords = variant.aStride;
  const int bWords = variant.bStride;
  const int cItems = outputItems(shape);
  const int cWords = variant.cStride;
  // bf16 results hold 8 significant bits, so their inputs stay integral
  const bool integral = shape.c == Format::BF16;
  std::vector<uint32_t> a(static_cast<size_t>(lanes) * aWords);
  std::vector<uint32_t> b(static_cast<size_t>(lanes) * bWords);
  std::vector<uint32_t> c(static_cast<size_t>(lanes) * cWords);
  std::vector<uint32_t> d(static_cast<size_t>(lanes) * cWords);
  std::vector<uint32_t> extra(192);
  for (int lane = 0; lane < lanes; ++lane) {
    for (int item = 0; item < inputItems(shape, true); ++item)
      setBits(a, aWords, lane, item * width(shape.a), width(shape.a), encode(shape.a, element(shape.a, random, integral)));
    for (int item = 0; item < inputItems(shape, false); ++item)
      setBits(b, bWords, lane, item * width(shape.b), width(shape.b), encode(shape.b, element(shape.b, random, integral)));
    for (int item = 0; item < cItems; ++item) {
      const double value = integer(shape.c) && controls.clamp ? 2147483600.0 + static_cast<double>(random() % 32)
                                                              : static_cast<double>(static_cast<int>(random() % 17) - 8);
      setBits(c, cWords, lane, outputBit(shape, controls, item), width(shape.c), encode(shape.c, value));
    }
    // index pairs choose two of each four elements, in order
    uint32_t index = 0;
    for (int pair = 0; pair < 8; ++pair) {
      const int first = static_cast<int>(random() % 3);
      const int second = first + 1 + static_cast<int>(random() % (3 - first));
      index |= static_cast<uint32_t>(first | (second << 2)) << (4 * pair);
    }
    // the gfx12 builtins pass a 16-bit index, which the compiler extends
    extra[128 + lane] = shape.layout == Layout::Wave12 ? (index & 0x0fff) | 0x4000 : index;
    extra[lane] = (125 + (random() % 5)) | ((125 + (random() % 5)) << 8) | ((125 + (random() % 5)) << 16) | ((125 + (random() % 5)) << 24);
    extra[64 + lane] =
        (125 + (random() % 5)) | ((125 + (random() % 5)) << 8) | ((125 + (random() % 5)) << 16) | ((125 + (random() % 5)) << 24);
  }
  // where A's and B's elements lie
  const int aDepth = storedK(shape);
  std::vector<int> aLane(static_cast<size_t>(shape.blocks) * shape.m * aDepth, -1);
  std::vector<int> aItem(aLane.size());
  std::vector<int> bLane(static_cast<size_t>(shape.blocks) * shape.n * shape.k, -1);
  std::vector<int> bItem(bLane.size());
  for (int lane = 0; lane < lanes; ++lane) {
    Coordinates at{};
    for (int item = 0; item < inputItems(shape, true); ++item)
      if (input(shape, true, lane, item, at)) {
        const int key = (((at.block * shape.m) + at.row) * aDepth) + at.k;
        if (aLane[key] < 0) aLane[key] = lane, aItem[key] = item;
      }
    for (int item = 0; item < inputItems(shape, false); ++item)
      if (input(shape, false, lane, item, at)) {
        const int key = (((at.block * shape.n) + at.row) * shape.k) + at.k;
        if (bLane[key] < 0) bLane[key] = lane, bItem[key] = item;
      }
  }
  std::vector<uint32_t> expected = c;
  const bool chained = aligned && shape.layout == Layout::Wave11 && (shape.a == Format::F16 || shape.a == Format::BF16);
  // the integer sum saturates after every 16 bits of A's elements
  const int grouped = aligned && shape.layout == Layout::Wave11 && integer(shape.a) && controls.clamp ? 16 / width(shape.a) : 0;
  for (int lane = 0; lane < lanes; ++lane)
    for (int item = 0; item < cItems; ++item) {
      const auto [block, row, column] = output(shape, lane, item);
      double sum = decode(shape.c, bitsAt(c, cWords, lane, outputBit(shape, controls, item), width(shape.c)));
      if (controls.negate & 4) sum = -sum;
      std::pair<double, double> pending;
      for (int k = 0; k < aDepth; ++k) {
        const int source = shape.layout == Layout::Core && controls.blockBroadcast
                               ? (block >> controls.blockBroadcast << controls.blockBroadcast) + controls.blockId
                               : block;
        const int aKey = (((source * shape.m) + row) * aDepth) + k;
        const int fromA = aLane[aKey] + (shape.layout == Layout::Wave11 ? lane % 32 / 16 * 16 : 0);
        double left = decode(shape.a, bitsAt(a, aWords, fromA, aItem[aKey] * width(shape.a), width(shape.a)));
        int dense = k;
        if (shape.sparse) {
          const int pairs = inputItems(shape, true) / 2;
          const uint32_t index = extra[128 + fromA] >> (4 * pairs * controls.indexSet);
          dense = (4 * (k / 2)) + static_cast<int>((index >> ((4 * (aItem[aKey] / 2)) + (2 * (aItem[aKey] % 2)))) & 3);
        }
        const int bKey = (((block * shape.n) + column) * shape.k) + dense;
        int fromB = bLane[bKey] + (shape.layout == Layout::Wave11 ? lane % 32 / 16 * 16 : 0);
        switch (controls.laneGroups) {
          case 1: fromB %= 32; break;
          case 2: fromB = (fromB % 32) + 32; break;
          case 3: fromB = (fromB + 16) % 64; break;
          case 4:
          case 5:
          case 6:
          case 7: fromB = (fromB % 16) + (16 * (controls.laneGroups - 4)); break;
          default: break;
        }
        double right = decode(shape.b, bitsAt(b, bWords, fromB, bItem[bKey] * width(shape.b), width(shape.b)));
        if (controls.negate & 1) left = -left;
        if (controls.negate & 2) right = -right;
        if (controls.scaled) {
          const int scaleA = static_cast<int>((extra[row + (shape.m * (dense / 32))] >> (8 * controls.selectionA)) & 0xff);
          const int scaleB = static_cast<int>((extra[64 + column + (shape.n * (dense / 32))] >> (8 * controls.selectionB)) & 0xff);
          left = std::ldexp(left, scaleA - 127);
          right = std::ldexp(right, scaleB - 127);
        }
        if (chained && k % 2 == 0) {
          pending = {left, right};
        } else if (chained) {
          sum = alignedDot(pending.first, pending.second, left, right, sum, dotResult(shape.c));
        } else {
          sum += left * right;
          if (grouped && (k + 1) % grouped == 0) sum = std::min(2147483647.0, std::max(-2147483648.0, sum));
        }
      }
      if (integer(shape.c))
        sum = controls.clamp ? std::min(2147483647.0, std::max(-2147483648.0, sum))
                             : static_cast<double>(static_cast<int32_t>(static_cast<uint32_t>(static_cast<int64_t>(sum))));
      setBits(expected, cWords, lane, outputBit(shape, controls, item), width(shape.c), encode(shape.c, sum));
    }
  uint32_t *da{};
  uint32_t *db{};
  uint32_t *dc{};
  uint32_t *dd{};
  uint32_t *de{};
  check(hipMalloc(&da, a.size() * 4), "hipMalloc");
  check(hipMalloc(&db, b.size() * 4), "hipMalloc");
  check(hipMalloc(&dc, c.size() * 4), "hipMalloc");
  check(hipMalloc(&dd, d.size() * 4), "hipMalloc");
  check(hipMalloc(&de, extra.size() * 4), "hipMalloc");
  check(hipMemcpy(da, a.data(), a.size() * 4, hipMemcpyHostToDevice), "upload A");
  check(hipMemcpy(db, b.data(), b.size() * 4, hipMemcpyHostToDevice), "upload B");
  check(hipMemcpy(dc, c.data(), c.size() * 4, hipMemcpyHostToDevice), "upload C");
  check(hipMemcpy(dd, c.data(), c.size() * 4, hipMemcpyHostToDevice), "initialise D");
  check(hipMemcpy(de, extra.data(), extra.size() * 4, hipMemcpyHostToDevice), "upload scales and indices");
  variant.run(da, db, dc, dd, de, lanes);
  check(hipGetLastError(), "launch");
  check(hipDeviceSynchronize(), "hipDeviceSynchronize");
  check(hipMemcpy(d.data(), dd, d.size() * 4, hipMemcpyDeviceToHost), "download D");
  for (auto *pointer : {da, db, dc, dd, de})
    (void)hipFree(pointer);
  for (int lane = 0; lane < lanes; ++lane)
    for (int item = 0; item < cItems; ++item) {
      const int bit = outputBit(shape, controls, item);
      const double found = decode(shape.c, bitsAt(d, cWords, lane, bit, width(shape.c)));
      const double wanted = decode(shape.c, bitsAt(expected, cWords, lane, bit, width(shape.c)));
      CAPTURE(variant.name, lane, item);
      REQUIRE(found == wanted);
    }
}

constexpr Shape core(int m, int n, int k, int blocks, Format a, Format b, Format c) {
  return {.layout = Layout::Core, .m = m, .n = n, .k = k, .blocks = blocks, .a = a, .b = b, .c = c};
}
constexpr Shape sparse(int m, int n, int k, Format a, Format c, int chunkA = 0, int chunkB = 0) {
  return {.layout = Layout::Core,
          .m = m,
          .n = n,
          .k = k,
          .blocks = 1,
          .a = a,
          .b = a,
          .c = c,
          .sparse = true,
          .chunkA = chunkA,
          .chunkB = chunkB};
}
[[maybe_unused]] constexpr Shape wave64(Layout layout, int k, Format a, Format b, Format c, bool sparse = false) {
  return {.layout = layout,
          .m = 16,
          .n = 16,
          .k = k,
          .blocks = 1,
          .a = a,
          .b = b,
          .c = c,
          .sparse = sparse,
          .chunkA = 0,
          .chunkB = 0,
          .wave = 64};
}
constexpr Shape wave(Layout layout, int k, Format a, Format b, Format c, bool sparse = false) {
  return {.layout = layout,
          .m = 16,
          .n = 16,
          .k = k,
          .blocks = 1,
          .a = a,
          .b = b,
          .c = c,
          .sparse = sparse,
          .chunkA = 0,
          .chunkB = 0,
          .wave = 32};
}

using enum Format;
constexpr unsigned AllCdna = Cdna1 | Cdna2 | Cdna3 | Cdna4;

const std::vector<Variant> &variants() {
  static const std::vector<Variant> all{
#ifndef UNIFIED_MATRIX_WAVE64
      variant<F32x32x1>("f32-32x32x1-2b", AllCdna, core(32, 32, 1, 2, F32, F32, F32), {}),
      variant<F32x32x1Broadcast>("f32-32x32x1-2b-cbsz1-abid1-blgp3", AllCdna, core(32, 32, 1, 2, F32, F32, F32),
                                 {.blockBroadcast = 1, .blockId = 1, .laneGroups = 3}),
      variant<F32x16x1>("f32-16x16x1-4b-cbsz2-abid3", AllCdna, core(16, 16, 1, 4, F32, F32, F32),
                        {.blockBroadcast = 2, .blockId = 3, .laneGroups = 0}),
      variant<F32x4x1>("f32-4x4x1-16b-cbsz4-abid9-blgp5", AllCdna, core(4, 4, 1, 16, F32, F32, F32),
                       {.blockBroadcast = 4, .blockId = 9, .laneGroups = 5}),
      variant<F32x32x2>("f32-32x32x2", AllCdna, core(32, 32, 2, 1, F32, F32, F32), {}),
      variant<F32x16x4>("f32-16x16x4-blgp2", AllCdna, core(16, 16, 4, 1, F32, F32, F32),
                        {.blockBroadcast = 0, .blockId = 0, .laneGroups = 2}),
      variant<F16x32x4>("f16-32x32x4-2b", AllCdna, core(32, 32, 4, 2, F16, F16, F32), {}),
      variant<F16x16x4>("f16-16x16x4-4b", AllCdna, core(16, 16, 4, 4, F16, F16, F32), {}),
      variant<F16x4x4>("f16-4x4x4-16b-cbsz3-abid5-blgp7", AllCdna, core(4, 4, 4, 16, F16, F16, F32),
                       {.blockBroadcast = 3, .blockId = 5, .laneGroups = 7}),
      variant<F16x32x8>("f16-32x32x8", AllCdna, core(32, 32, 8, 1, F16, F16, F32), {}),
      variant<F16x16x16>("f16-16x16x16", AllCdna, core(16, 16, 16, 1, F16, F16, F32), {}),
      variant<I8x32x4>("i8-32x32x4-2b", AllCdna, core(32, 32, 4, 2, I8, I8, I32), {}),
      variant<I8x16x4>("i8-16x16x4-4b", AllCdna, core(16, 16, 4, 4, I8, I8, I32), {}),
      variant<I8x4x4>("i8-4x4x4-16b", AllCdna, core(4, 4, 4, 16, I8, I8, I32), {}),
      variant<I8x32x8>("i8-32x32x8", Cdna1 | Cdna2, core(32, 32, 8, 1, I8, I8, I32), {}),
      variant<I8x16x16>("i8-16x16x16", Cdna1 | Cdna2, core(16, 16, 16, 1, I8, I8, I32), {}),
      variant<Bf16x32x2>("bf16-32x32x2-2b", Cdna1 | Cdna2, core(32, 32, 2, 2, BF16, BF16, F32), {}),
      variant<Bf16x16x2>("bf16-16x16x2-4b", Cdna1 | Cdna2, core(16, 16, 2, 4, BF16, BF16, F32), {}),
      variant<Bf16x4x2>("bf16-4x4x2-16b", Cdna1 | Cdna2, core(4, 4, 2, 16, BF16, BF16, F32), {}),
      variant<Bf16x32x4>("bf16-32x32x4", Cdna1 | Cdna2, core(32, 32, 4, 1, BF16, BF16, F32), {}),
      variant<Bf16x16x8>("bf16-16x16x8", Cdna1 | Cdna2, core(16, 16, 8, 1, BF16, BF16, F32), {}),
      variant<Bf16x32x4k>("bf16-1k-32x32x4-2b", Cdna2 | Cdna3 | Cdna4, core(32, 32, 4, 2, BF16, BF16, F32), {}),
      variant<Bf16x16x4k>("bf16-1k-16x16x4-4b", Cdna2 | Cdna3 | Cdna4, core(16, 16, 4, 4, BF16, BF16, F32), {}),
      variant<Bf16x4x4k>("bf16-1k-4x4x4-16b", Cdna2 | Cdna3 | Cdna4, core(4, 4, 4, 16, BF16, BF16, F32), {}),
      variant<Bf16x32x8k>("bf16-1k-32x32x8", Cdna2 | Cdna3 | Cdna4, core(32, 32, 8, 1, BF16, BF16, F32), {}),
      variant<Bf16x16x16k>("bf16-1k-16x16x16", Cdna2 | Cdna3 | Cdna4, core(16, 16, 16, 1, BF16, BF16, F32), {}),
      variant<F64x16x4>("f64-16x16x4", Cdna2 | Cdna3 | Cdna4, core(16, 16, 4, 1, F64, F64, F64), {}),
      variant<F64x4x4>("f64-4x4x4-4b", Cdna2 | Cdna3 | Cdna4, core(4, 4, 4, 4, F64, F64, F64), {}),
      variant<F64x16x4Negated>("f64-16x16x4-negate-a-c", Cdna3 | Cdna4, core(16, 16, 4, 1, F64, F64, F64),
                               {.blockBroadcast = 0, .blockId = 0, .laneGroups = 0, .negate = 5}),
      variant<Xf32x16x8>("xf32-16x16x8", Cdna3, core(16, 16, 8, 1, XF32, XF32, F32), {}),
      variant<Xf32x32x4>("xf32-32x32x4", Cdna3, core(32, 32, 4, 1, XF32, XF32, F32), {}),
      variant<I8x16x32>("i8-16x16x32", Cdna3 | Cdna4, core(16, 16, 32, 1, I8, I8, I32), {}),
      variant<I8x32x16>("i8-32x32x16", Cdna3 | Cdna4, core(32, 32, 16, 1, I8, I8, I32), {}),
      variant<Fp8Fp8x16>("fp8-fp8-16x16x32", Cdna3, core(16, 16, 32, 1, FP8FNUZ, FP8FNUZ, F32), {}),
      variant<Fp8Bf8x16>("fp8-bf8-16x16x32", Cdna3, core(16, 16, 32, 1, FP8FNUZ, BF8FNUZ, F32), {}),
      variant<Bf8Fp8x16>("bf8-fp8-16x16x32", Cdna3, core(16, 16, 32, 1, BF8FNUZ, FP8FNUZ, F32), {}),
      variant<Bf8Bf8x16>("bf8-bf8-16x16x32", Cdna3, core(16, 16, 32, 1, BF8FNUZ, BF8FNUZ, F32), {}),
      variant<Fp8Fp8x32>("fp8-fp8-32x32x16", Cdna3, core(32, 32, 16, 1, FP8FNUZ, FP8FNUZ, F32), {}),
      variant<Bf8Bf8x32>("bf8-bf8-32x32x16", Cdna3, core(32, 32, 16, 1, BF8FNUZ, BF8FNUZ, F32), {}),
      variant<SparseF16x16>("sparse-f16-16x16x32", Cdna3 | Cdna4, sparse(16, 16, 32, F16, F32), {}),
      variant<SparseF16x16Set>("sparse-f16-16x16x32-set2", Cdna3 | Cdna4, sparse(16, 16, 32, F16, F32),
                               {.blockBroadcast = 0, .blockId = 0, .laneGroups = 0, .negate = 0, .high = false, .indexSet = 2}),
      variant<SparseF16x32>("sparse-f16-32x32x16", Cdna3 | Cdna4, sparse(32, 32, 16, F16, F32), {}),
      variant<SparseBf16x16>("sparse-bf16-16x16x32", Cdna3 | Cdna4, sparse(16, 16, 32, BF16, F32), {}),
      variant<SparseI8x16>("sparse-i8-16x16x64-set1", Cdna3 | Cdna4, sparse(16, 16, 64, I8, I32),
                           {.blockBroadcast = 0, .blockId = 0, .laneGroups = 0, .negate = 0, .high = false, .indexSet = 1}),
      variant<SparseI8x32>("sparse-i8-32x32x32", Cdna3 | Cdna4, sparse(32, 32, 32, I8, I32), {}),
      variant<SparseFp8Bf8x16>(
          "sparse-fp8-bf8-16x16x64", Cdna3,
          {.layout = Layout::Core, .m = 16, .n = 16, .k = 64, .blocks = 1, .a = FP8FNUZ, .b = BF8FNUZ, .c = F32, .sparse = true}, {}),
      variant<SparseBf8Fp8x32>(
          "sparse-bf8-fp8-32x32x32", Cdna3,
          {.layout = Layout::Core, .m = 32, .n = 32, .k = 32, .blocks = 1, .a = BF8FNUZ, .b = FP8FNUZ, .c = F32, .sparse = true}, {}),
      variant<SparseFp8Bf8x16>(
          "sparse-fp8-bf8-16x16x64-ocp", Cdna4,
          {.layout = Layout::Core, .m = 16, .n = 16, .k = 64, .blocks = 1, .a = FP8, .b = BF8, .c = F32, .sparse = true}, {}),
      variant<F16x16x32>("f16-16x16x32", Cdna4, core(16, 16, 32, 1, F16, F16, F32), {}),
      variant<F16x32x16>("f16-32x32x16", Cdna4, core(32, 32, 16, 1, F16, F16, F32), {}),
      variant<Bf16x16x32>("bf16-16x16x32", Cdna4, core(16, 16, 32, 1, BF16, BF16, F32), {}),
      variant<Bf16x32x16>("bf16-32x32x16", Cdna4, core(32, 32, 16, 1, BF16, BF16, F32), {}),
      variant<I8x16x64>("i8-16x16x64", Cdna4, core(16, 16, 64, 1, I8, I8, I32), {}),
      variant<I8x32x32>("i8-32x32x32", Cdna4, core(32, 32, 32, 1, I8, I8, I32), {}),
      variant<Fp8Fp8x16Ocp>("fp8-fp8-16x16x32-ocp", Cdna4, core(16, 16, 32, 1, FP8, FP8, F32), {}),
      variant<Bf8Fp8x32Ocp>("bf8-fp8-32x32x16-ocp", Cdna4, core(32, 32, 16, 1, BF8, FP8, F32), {}),
      variant<F8F6F4Unscaled>("f8f6f4-fp8-fp8-16x16x128", Cdna4,
                              {.layout = Layout::Core,
                               .m = 16,
                               .n = 16,
                               .k = 128,
                               .blocks = 1,
                               .a = FP8,
                               .b = FP8,
                               .c = F32,
                               .sparse = false,
                               .chunkA = 16,
                               .chunkB = 16},
                              {}),
      variant<F8F6F4Fp8Fp4>("f8f6f4-scaled-fp8-fp4-16x16x128", Cdna4,
                            {.layout = Layout::Core,
                             .m = 16,
                             .n = 16,
                             .k = 128,
                             .blocks = 1,
                             .a = FP8,
                             .b = FP4,
                             .c = F32,
                             .sparse = false,
                             .chunkA = 16,
                             .chunkB = 0},
                            {.blockBroadcast = 0,
                             .blockId = 0,
                             .laneGroups = 0,
                             .negate = 0,
                             .high = false,
                             .indexSet = 0,
                             .clamp = false,
                             .scaled = true,
                             .selectionA = 1,
                             .selectionB = 2}),
      variant<F8F6F4Fp6Bf8>("f8f6f4-scaled-fp6-bf8-32x32x64", Cdna4,
                            {.layout = Layout::Core,
                             .m = 32,
                             .n = 32,
                             .k = 64,
                             .blocks = 1,
                             .a = FP6,
                             .b = BF8,
                             .c = F32,
                             .sparse = false,
                             .chunkA = 0,
                             .chunkB = 16},
                            {.blockBroadcast = 0,
                             .blockId = 0,
                             .laneGroups = 0,
                             .negate = 0,
                             .high = false,
                             .indexSet = 0,
                             .clamp = false,
                             .scaled = true,
                             .selectionA = 0,
                             .selectionB = 3}),
      variant<F8F6F4Bf6Fp6>("f8f6f4-scaled-bf6-fp6-16x16x128", Cdna4,
                            {.layout = Layout::Core, .m = 16, .n = 16, .k = 128, .blocks = 1, .a = BF6, .b = FP6, .c = F32},
                            {.blockBroadcast = 0,
                             .blockId = 0,
                             .laneGroups = 0,
                             .negate = 0,
                             .high = false,
                             .indexSet = 0,
                             .clamp = false,
                             .scaled = true,
                             .selectionA = 0,
                             .selectionB = 0}),
      variant<SparseF16x16x64>("sparse-f16-16x16x64-set1", Cdna4, sparse(16, 16, 64, F16, F32, 4, 8),
                               {.blockBroadcast = 0, .blockId = 0, .laneGroups = 0, .negate = 0, .high = false, .indexSet = 1}),
      variant<SparseBf16x32x32>("sparse-bf16-32x32x32", Cdna4, sparse(32, 32, 32, BF16, F32, 4, 8), {}),
      variant<SparseI8x16x128>("sparse-i8-16x16x128", Cdna4, sparse(16, 16, 128, I8, I32, 8, 16), {}),
      variant<SparseFp8x32x64>("sparse-fp8-32x32x64", Cdna4, sparse(32, 32, 64, FP8, F32, 8, 16), {}),
      variant<Wmma11F16>("wmma11-f16", Gfx11, wave(Layout::Wave11, 16, F16, F16, F32), {}),
      variant<Wmma11Bf16>("wmma11-bf16", Gfx11, wave(Layout::Wave11, 16, BF16, BF16, F32), {}),
      variant<Wmma11F16Out>("wmma11-f16-out", Gfx11, wave(Layout::Wave11, 16, F16, F16, F16), {}),
      variant<Wmma11F16High>("wmma11-f16-out-high", Gfx11, wave(Layout::Wave11, 16, F16, F16, F16),
                             {.blockBroadcast = 0, .blockId = 0, .laneGroups = 0, .negate = 0, .high = true}),
      variant<Wmma11Bf16Out>("wmma11-bf16-out", Gfx11, wave(Layout::Wave11, 16, BF16, BF16, BF16), {}),
      variant<Wmma11Iu8>("wmma11-iu8-signed-unsigned", Gfx11, wave(Layout::Wave11, 16, I8, U8, I32), {}),
      variant<Wmma11Iu8Clamp>(
          "wmma11-iu8-clamp",
          Gfx11, wave(Layout::Wave11, 16, U8, I8, I32),
          {.blockBroadcast = 0, .blockId = 0, .laneGroups = 0, .negate = 0, .high = false, .indexSet = 0, .clamp = true}),
      variant<Wmma11Iu4>("wmma11-iu4", Gfx11, wave(Layout::Wave11, 16, I4, I4, I32), {}),
      variant<Wmma12F16>("wmma12-f16", Gfx12, wave(Layout::Wave12, 16, F16, F16, F32), {}),
      variant<Wmma12Bf16>("wmma12-bf16", Gfx12, wave(Layout::Wave12, 16, BF16, BF16, F32), {}),
      variant<Wmma12F16Out>("wmma12-f16-out", Gfx12, wave(Layout::Wave12, 16, F16, F16, F16), {}),
      variant<Wmma12Bf16Out>("wmma12-bf16-out", Gfx12, wave(Layout::Wave12, 16, BF16, BF16, BF16), {}),
      variant<Wmma12Iu8>("wmma12-iu8-unsigned-signed", Gfx12, wave(Layout::Wave12, 16, U8, I8, I32), {}),
      variant<Wmma12Iu8Clamp>(
          "wmma12-iu8-clamp",
          Gfx12, wave(Layout::Wave12, 16, I8, I8, I32),
          {.blockBroadcast = 0, .blockId = 0, .laneGroups = 0, .negate = 0, .high = false, .indexSet = 0, .clamp = true}),
      variant<Wmma12Iu4>("wmma12-iu4-16x16x16", Gfx12, wave(Layout::Wave12, 16, I4, U4, I32), {}),
      variant<Wmma12Iu4x32>("wmma12-iu4-16x16x32", Gfx12, wave(Layout::Wave12, 32, I4, I4, I32), {}),
      variant<Wmma12Fp8Bf8>("wmma12-fp8-bf8", Gfx12, wave(Layout::Wave12, 16, FP8, BF8, F32), {}),
      variant<Wmma12Bf8Bf8>("wmma12-bf8-bf8", Gfx12, wave(Layout::Wave12, 16, BF8, BF8, F32), {}),
      variant<Swmmac12F16>("swmmac12-f16", Gfx12, wave(Layout::Wave12, 32, F16, F16, F32, true), {}),
      variant<Swmmac12Bf16Out>("swmmac12-bf16-out", Gfx12, wave(Layout::Wave12, 32, BF16, BF16, BF16, true), {}),
      variant<Swmmac12Iu8>("swmmac12-iu8", Gfx12, wave(Layout::Wave12, 32, I8, U8, I32, true), {}),
      variant<Swmmac12Fp8Fp8>("swmmac12-fp8-fp8", Gfx12, wave(Layout::Wave12, 32, FP8, FP8, F32, true), {}),
#else
      variant<Wmma11F16>("wmma11-f16-w64", Gfx11, wave64(Layout::Wave11, 16, F16, F16, F32), {}),
      variant<Wmma11Bf16>("wmma11-bf16-w64", Gfx11, wave64(Layout::Wave11, 16, BF16, BF16, F32), {}),
      variant<Wmma11F16High>("wmma11-f16-out-high-w64", Gfx11, wave64(Layout::Wave11, 16, F16, F16, F16),
                             {.blockBroadcast = 0, .blockId = 0, .laneGroups = 0, .negate = 0, .high = true}),
      variant<Wmma11Bf16Out>("wmma11-bf16-out-w64", Gfx11, wave64(Layout::Wave11, 16, BF16, BF16, BF16), {}),
      variant<Wmma11Iu8>("wmma11-iu8-signed-unsigned-w64", Gfx11, wave64(Layout::Wave11, 16, I8, U8, I32), {}),
      variant<Wmma11Iu4Clamp>(
          "wmma11-iu4-clamp-w64", Gfx11, wave64(Layout::Wave11, 16, I4, I4, I32),
          {.blockBroadcast = 0, .blockId = 0, .laneGroups = 0, .negate = 0, .high = false, .indexSet = 0, .clamp = true}),
      variant<Wmma12F16>("wmma12-f16-w64", Gfx12, wave64(Layout::Wave12, 16, F16, F16, F32), {}),
      variant<Wmma12Bf16>("wmma12-bf16-w64", Gfx12, wave64(Layout::Wave12, 16, BF16, BF16, F32), {}),
      variant<Wmma12F16Out>("wmma12-f16-out-w64", Gfx12, wave64(Layout::Wave12, 16, F16, F16, F16), {}),
      variant<Wmma12Bf16Out>("wmma12-bf16-out-w64", Gfx12, wave64(Layout::Wave12, 16, BF16, BF16, BF16), {}),
      variant<Wmma12Iu8Clamp>(
          "wmma12-iu8-clamp-w64", Gfx12, wave64(Layout::Wave12, 16, U8, I8, I32),
          {.blockBroadcast = 0, .blockId = 0, .laneGroups = 0, .negate = 0, .high = false, .indexSet = 0, .clamp = true}),
      variant<Wmma12Iu4>("wmma12-iu4-16x16x16-w64", Gfx12, wave64(Layout::Wave12, 16, I4, U4, I32), {}),
      variant<Wmma12Iu4x32>("wmma12-iu4-16x16x32-w64", Gfx12, wave64(Layout::Wave12, 32, I4, I4, I32), {}),
      variant<Wmma12Fp8Bf8>("wmma12-fp8-bf8-w64", Gfx12, wave64(Layout::Wave12, 16, FP8, BF8, F32), {}),
      variant<Wmma12Bf8Fp8>("wmma12-bf8-fp8-w64", Gfx12, wave64(Layout::Wave12, 16, BF8, FP8, F32), {}),
      variant<Swmmac12F16>("swmmac12-f16-w64", Gfx12, wave64(Layout::Wave12, 32, F16, F16, F32, true), {}),
      variant<Swmmac12Bf16Out>("swmmac12-bf16-out-w64", Gfx12, wave64(Layout::Wave12, 32, BF16, BF16, BF16, true), {}),
      variant<Swmmac12Iu8>("swmmac12-iu8-w64", Gfx12, wave64(Layout::Wave12, 32, I8, U8, I32, true), {}),
      variant<Swmmac12Fp8Fp8>("swmmac12-fp8-fp8-w64", Gfx12, wave64(Layout::Wave12, 32, FP8, FP8, F32, true), {}),
#endif
  };
  return all;
}

unsigned family(const std::string &architecture) {
  if (architecture.starts_with("gfx908")) return Cdna1;
  if (architecture.starts_with("gfx90a")) return Cdna2;
  if (architecture.starts_with("gfx94")) return Cdna3;
  if (architecture.starts_with("gfx950")) return Cdna4;
  if (architecture.starts_with("gfx11")) return Gfx11;
  if (architecture.starts_with("gfx12") && !architecture.starts_with("gfx125")) return Gfx12;
  return 0;
}

}} // namespace unified_matrix

#ifdef UNIFIED_MATRIX_WAVE64
  #define UNIFIED_MATRIX_CASE "runtime/hip/matrix-builtins-wave64"
#else
  #define UNIFIED_MATRIX_CASE "runtime/hip/matrix-builtins"
#endif

TEST_CASE(UNIFIED_MATRIX_CASE, "[unified][runtime][tensor][compact]") {
  using namespace aspartame;
  using namespace unified_matrix;
  (void)conformance::deviceSelection();
  int ordinal{};
  check(hipGetDevice(&ordinal), "hipGetDevice");
  hipDeviceProp_t properties{};
  check(hipGetDeviceProperties(&properties, ordinal), "hipGetDeviceProperties");
  const std::string architecture = properties.gcnArchName;
  const unsigned selected = family(architecture);
  if (!selected) SKIP("no MFMA or WMMA on " + architecture);
  // the builtins are compiled for the artifact's own target alone
  if (family(CONFORMANCE_DEVICE_TARGETS) != selected) SKIP("the artifact targets " CONFORMANCE_DEVICE_TARGETS);
#ifdef UNIFIED_MATRIX_WAVE64
  if (!(selected & (Gfx11 | Gfx12))) SKIP("wave64 WMMA runs on gfx11 and gfx12 alone");
#else
  if ((selected & (Gfx11 | Gfx12)) && properties.warpSize != 32) SKIP("the WMMA cases run in wave32");
#endif
  const auto chosen = variants() ^ filter([&](const Variant &variant) { return (variant.families & selected) != 0; });
  const bool aligned = alignedDots(architecture);
  chosen ^ for_each([&](const Variant &variant) { runVariant(variant, aligned); });
  REQUIRE(!chosen.empty());
}
