package net.kurobako.gemstone.isa.sass.fixtures

object HalfFixtures {
  val hadd2Cuda: String =
    """#include <cuda_fp16.h>
      |extern "C" __global__ void convert_half(float *out, const __half2 *in) {
      |  unsigned i = blockIdx.x;
      |  __half2 value = in[i];
      |  out[2 * i] = __low2float(value);
      |  out[2 * i + 1] = __high2float(value);
      |}
      |""".stripMargin

  val hadd2F32Cuda: String =
    """#include <cuda_fp16.h>
      |#include <stdint.h>
      |extern "C" __global__ void probe(uint32_t *out, const uint32_t *in) {
      |  unsigned i = blockIdx.x;
      |  __half2 x = *reinterpret_cast<const __half2 *>(in + i);
      |  __half2 z = __halves2half2(__float2half(-0.0f), __float2half(-0.0f));
      |  float2 r = __half22float2(__hadd2(x, z));
      |  out[2*i] = __float_as_uint(r.x);
      |  out[2*i+1] = __float_as_uint(r.y);
      |}
      |""".stripMargin

  val hadd2F32ToHalfCuda: String =
    """#include <cuda_fp16.h>
      |#include <stdint.h>
      |extern "C" __global__ void probe(uint32_t *out, const uint32_t *in) {
      |  unsigned i = blockIdx.x;
      |  float x = __uint_as_float(in[i]);
      |  __half2 converted = __float2half2_rn(x);
      |  __half2 negativeZero = __halves2half2(__ushort_as_half(0x8000), __ushort_as_half(0x8000));
      |  __half2 result = __hadd2(converted, negativeZero);
      |  out[i] = *reinterpret_cast<uint32_t *>(&result);
      |}
      |""".stripMargin

  val hadd2SelectedCuda: String =
    """#include <cuda_fp16.h>
      |extern "C" __global__ void hadd2_selected_low(unsigned *out, const unsigned *a, const unsigned *b) {
      |  unsigned i = threadIdx.x;
      |  union U { unsigned u; half2 h; } x, y, z;
      |  x.u = a[i]; y.u = b[i];
      |  z.h = __hadd2(__halves2half2(__low2half(x.h), __low2half(x.h)),
      |                __halves2half2(__low2half(y.h), __low2half(y.h)));
      |  out[i] = z.u;
      |}
      |extern "C" __global__ void hadd2_selected_high(unsigned *out, const unsigned *a, const unsigned *b) {
      |  unsigned i = threadIdx.x;
      |  union U { unsigned u; half2 h; } x, y, z;
      |  x.u = a[i]; y.u = b[i];
      |  z.h = __hadd2(__halves2half2(__high2half(x.h), __high2half(x.h)),
      |                __halves2half2(__high2half(y.h), __high2half(y.h)));
      |  out[i] = z.u;
      |}
      |""".stripMargin

  val hfma2Cuda: String =
    """#include <cuda_fp16.h>
      |
      |extern "C" __global__ void hfma2_vector(unsigned *output, const unsigned *left, const unsigned *right, const unsigned *addend) {
      |  const unsigned index = blockIdx.x * blockDim.x + threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(left + index);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(right + index);
      |  const __half2 c = *reinterpret_cast<const __half2 *>(addend + index);
      |  const __half2 result = __hfma2(a, b, c);
      |  reinterpret_cast<__half2 *>(output)[index] = result;
      |}
      |""".stripMargin

  val hfma2MmaCuda: String =
    """extern "C" __global__ void hfma2_mma_probe(unsigned *output, const unsigned *a, const unsigned *c) {
      |  const unsigned index = threadIdx.x;
      |  const unsigned left = a[index];
      |  unsigned result = c[index];
      |  asm volatile("fma.rn.f16x2 %0, %1, %2, %0;" : "+r"(result) : "r"(left), "r"(0u));
      |  output[index] = result;
      |}
      |
      |extern "C" __global__ void hfma2_add_reference(unsigned *output, const unsigned *a, const unsigned *c) {
      |  const unsigned index = threadIdx.x;
      |  unsigned result;
      |  asm volatile("add.rn.f16x2 %0, %1, %2;" : "=r"(result) : "r"(a[index]), "r"(c[index]));
      |  output[index] = result;
      |}
      |""".stripMargin

  val hfma2SelectedCuda: String =
    """#include <cuda_fp16.h>
      |
      |extern "C" __global__ void hfma2_selected(unsigned *output, const unsigned *left, const unsigned *right, const unsigned *addend) {
      |  const unsigned index = blockIdx.x * blockDim.x + threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(left + index);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(right + index);
      |  const __half2 low = __halves2half2(__low2half(a), __low2half(a));
      |  const __half2 high = __halves2half2(__high2half(b), __high2half(b));
      |  const __half2 c = *reinterpret_cast<const __half2 *>(addend + index);
      |  const __half2 result = __hfma2(low, high, c);
      |  reinterpret_cast<__half2 *>(output)[index] = result;
      |}
      |""".stripMargin

  val hmnmx2Cuda: String =
    """#include <cuda_fp16.h>
      |#include <stdint.h>
      |
      |extern "C" __global__ void hmnmx2_select_probe(
      |    const uint32_t *left, const uint32_t *right, uint32_t *output) {
      |  const unsigned index = threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(&left[index]);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(&right[index]);
      |  const __half minimum = __hmin(__low2half(a), __low2half(b));
      |  const __half maximum = __hmax(__low2half(a), __low2half(b));
      |  output[2 * index] = __half_as_ushort(minimum);
      |  output[2 * index + 1] = __half_as_ushort(maximum);
      |}
      |
      |extern "C" __global__ void hmnmx2_alias_probe(uint32_t *left, const uint32_t *right) {
      |  const unsigned index = threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(&left[index]);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(&right[index]);
      |  const __half maximum = __hmax(__low2half(a), __low2half(b));
      |  left[index] = (left[index] & 0xffff0000u) | __half_as_ushort(maximum);
      |}
      |
      |extern "C" __global__ void hmnmx2_mixed_probe(
      |    const uint32_t *left, const uint32_t *right, uint32_t *output) {
      |  const unsigned index = threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(&left[index]);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(&right[index]);
      |  const __half low = __low2half(b);
      |  const __half2 duplicated = __halves2half2(low, low);
      |  const __half2 maximum = __hmax2(a, duplicated);
      |  output[index] = *reinterpret_cast<const uint32_t *>(&maximum);
      |}
      |
      |extern "C" __global__ void hmnmx2_mixed_alias_probe(uint32_t *left, const uint32_t *right) {
      |  const unsigned index = threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(&left[index]);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(&right[index]);
      |  const __half low = __low2half(b);
      |  const __half2 duplicated = __halves2half2(low, low);
      |  const __half2 maximum = __hmax2(a, duplicated);
      |  left[index] = *reinterpret_cast<const uint32_t *>(&maximum);
      |}
      |""".stripMargin

  val hmul2Cuda: String =
    """#include <cuda_fp16.h>
      |
      |extern "C" __global__ void hmul2_vector(unsigned *output, const unsigned *left, const unsigned *right) {
      |  const unsigned index = blockIdx.x * blockDim.x + threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(left + index);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(right + index);
      |  const __half2 product = __hmul2(a, b);
      |  reinterpret_cast<__half2 *>(output)[index] = product;
      |}
      |""".stripMargin

  val hmul2F32Cuda: String =
    """#include <cuda_fp16.h>
      |#include <stdint.h>
      |
      |extern "C" __global__ void hmul2_f32_probe(uint32_t *output, const uint32_t *left, const uint32_t *right) {
      |  const unsigned index = blockIdx.x * blockDim.x + threadIdx.x;
      |  const uint32_t a = left[index];
      |  const uint32_t b = right[index];
      |  const __half a0 = __ushort_as_half(static_cast<unsigned short>(a));
      |  const __half a1 = __ushort_as_half(static_cast<unsigned short>(a >> 16));
      |  const __half b0 = __ushort_as_half(static_cast<unsigned short>(b));
      |  const __half b1 = __ushort_as_half(static_cast<unsigned short>(b >> 16));
      |  const float af0 = __half2float(a0);
      |  const float af1 = __half2float(a1);
      |  const float bf0 = __half2float(b0);
      |  const float bf1 = __half2float(b1);
      |  output[4 * index + 0] = __float_as_uint(af0 * bf0);
      |  output[4 * index + 1] = __float_as_uint(af0 * bf1);
      |  output[4 * index + 2] = __float_as_uint(af1 * bf0);
      |  output[4 * index + 3] = __float_as_uint(af1 * bf1);
      |}
      |""".stripMargin

  val hmul2SelectedCuda: String =
    """#include <cuda_fp16.h>
      |
      |extern "C" __global__ void hmul2_selected(unsigned *output, const unsigned *left, const unsigned *right) {
      |  const unsigned index = blockIdx.x * blockDim.x + threadIdx.x;
      |  const __half2 a = *reinterpret_cast<const __half2 *>(left + index);
      |  const __half2 b = *reinterpret_cast<const __half2 *>(right + index);
      |  const __half2 low = __halves2half2(__low2half(a), __low2half(a));
      |  const __half2 high = __halves2half2(__high2half(b), __high2half(b));
      |  reinterpret_cast<__half2 *>(output)[index] = __hmul2(low, high);
      |}
      |""".stripMargin

  val hsetp2Cuda: String =
    """#include <cuda_fp16.h>
      |#include <stdint.h>
      |
      |extern "C" __global__ void hsetp2_probe(uint32_t *output, const uint32_t *left, const uint32_t *right) {
      |  const unsigned index = threadIdx.x;
      |  const uint32_t left_word = left[index];
      |  const uint32_t right_word = right[index];
      |  const __half a = __ushort_as_half(static_cast<unsigned short>(left_word));
      |  const __half b = __ushort_as_half(static_cast<unsigned short>(right_word));
      |  output[index] = __hgeu(a, b) | ((left_word >> 16) << 1) | ((right_word >> 16) << 17);
      |}
      |""".stripMargin

  val hsetp2NeCuda: String =
    """#include <cuda_fp16.h>
      |#include <stdint.h>
      |
      |extern "C" __global__ void hsetp2_ne_probe(
      |    uint32_t *output, const uint32_t *bits, const uint32_t *mask) {
      |  const unsigned index = threadIdx.x;
      |  const uint32_t word = bits[index];
      |  const __half value = __ushort_as_half(static_cast<unsigned short>(word));
      |  const uint32_t predicate = __hne(value, __ushort_as_half(0));
      |  output[index] = predicate | ((word >> 16) << 1) | (mask[index] & 0x80000000u);
      |}
      |""".stripMargin

  def hsetp2TwoPtx(sm: Int): String =
    s""".version 7.8
      |.target sm_$sm
      |.address_size 64
      |.visible .entry hsetp2_two(.param .u64 out, .param .u64 lhs, .param .u64 rhs) {
      |  .reg .pred %p0, %p1, %pgate;
      |  .reg .b32 %a, %b, %result;
      |  .reg .b64 %op, %lp, %rp, %off;
      |  .reg .u32 %lane;
      |  ld.param.u64 %op, [out];
      |  ld.param.u64 %lp, [lhs];
      |  ld.param.u64 %rp, [rhs];
      |  mov.u32 %lane, %tid.x;
      |  mov.pred %pgate, 1;
      |  mul.wide.u32 %off, %lane, 4;
      |  add.u64 %lp, %lp, %off;
      |  add.u64 %rp, %rp, %off;
      |  add.u64 %op, %op, %off;
      |  ld.global.u32 %a, [%lp];
      |  ld.global.u32 %b, [%rp];
      |  setp.geu.and.f16x2 %p0|%p1, %a, %b, %pgate;
      |  selp.u32 %result, 1, 0, %p0;
      |  st.global.u32 [%op], %result;
      |  add.u64 %op, %op, 128;
      |  selp.u32 %result, 1, 0, %p1;
      |  st.global.u32 [%op], %result;
      |  ret;
      |}
      |""".stripMargin

  val listings: Map[String, String] = Map(
    "hadd2F32Sm75" ->
      """//--------------------- .text.probe               --------------------------
        |	.section	.text.probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=5"
        |	.align	128
        |        .global         probe
        |        .type           probe,@function
        |        .size           probe,(.L_x_1 - probe)
        |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |probe:
        |.text.probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                     /* 0x00000a0000017a02 */
        |                                                                              /* 0x000fe40000000f00 */
        |        /*0010*/                   S2UR UR6, SR_CTAID.X ;                     /* 0x00000000000679c3 */
        |                                                                              /* 0x000e220000002500 */
        |        /*0020*/                   UMOV UR7, 0x4 ;                            /* 0x0000000400077882 */
        |                                                                              /* 0x000fe40000000000 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x168] ;               /* 0x00005a0000047ab9 */
        |                                                                              /* 0x000fe40000000a00 */
        |        /*0040*/                   UIMAD.WIDE.U32 UR4, UR6, UR7, UR4 ;        /* 0x00000007060472a5 */
        |                                                                              /* 0x001fd2000f8e0004 */
        |        /*0050*/                   LDG.E.CONSTANT.SYS R0, [UR4] ;             /* 0x00000004ff007981 */
        |                                                                              /* 0x000ea2000c1e6900 */
        |        /*0060*/                   USHF.L.U32 UR6, UR6, 0x1, URZ ;            /* 0x0000000106067899 */
        |                                                                              /* 0x000fe4000800063f */
        |        /*0070*/                   ULDC.64 UR4, c[0x0][0x160] ;               /* 0x0000580000047ab9 */
        |                                                                              /* 0x000fe40000000a00 */
        |        /*0080*/                   UIMAD.WIDE.U32 UR4, UR6, UR7, UR4 ;        /* 0x00000007060472a5 */
        |                                                                              /* 0x000fe2000f8e0004 */
        |        /*0090*/                   HADD2 R0, R0, -RZ.H0_H0 ;                  /* 0xa00000ff00007230 */
        |                                                                              /* 0x004fcc0000000000 */
        |        /*00a0*/                   HADD2.F32 R2, R0.reuse.H0_H0, -RZ.H0_H0 ;  /* 0xa00000ff00027230 */
        |                                                                              /* 0x040fe40000004800 */
        |        /*00b0*/                   HADD2.F32 R0, R0.H1_H1, -RZ.H0_H0 ;        /* 0xa00000ff00007230 */
        |                                                                              /* 0x000fcc0000004c00 */
        |        /*00c0*/                   STG.E.SYS [UR4], R2 ;                      /* 0x00000002ff007986 */
        |                                                                              /* 0x000fe8000c10e904 */
        |        /*00d0*/                   STG.E.SYS [UR4+0x4], R0 ;                  /* 0x00000400ff007986 */
        |                                                                              /* 0x000fe2000c10e904 */
        |        /*00e0*/                   EXIT ;                                     /* 0x000000000000794d */
        |                                                                              /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hadd2F32ToHalfSm75Patched" ->
      """//--------------------- .text.probe               --------------------------
        |	.section	.text.probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=5"
        |	.align	128
        |        .global         probe
        |        .type           probe,@function
        |        .size           probe,(.L_x_1 - probe)
        |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |probe:
        |.text.probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;               /* 0x00000a0000017a02 */
        |                                                                        /* 0x000fc40000000f00 */
        |        /*0010*/                   S2UR UR6, SR_CTAID.X ;               /* 0x00000000000679c3 */
        |                                                                        /* 0x000e220000002500 */
        |        /*0020*/                   UMOV UR7, 0x4 ;                      /* 0x0000000400077882 */
        |                                                                        /* 0x000fe40000000000 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x168] ;         /* 0x00005a0000047ab9 */
        |                                                                        /* 0x000fe40000000a00 */
        |        /*0040*/                   UIMAD.WIDE.U32 UR4, UR6, UR7, UR4 ;  /* 0x00000007060472a5 */
        |                                                                        /* 0x001fd2000f8e0004 */
        |        /*0050*/                   LDG.E.SYS R0, [UR4] ;                /* 0x00000004ff007981 */
        |                                                                        /* 0x000ea2000c1ee900 */
        |        /*0060*/                   ULDC.64 UR4, c[0x0][0x160] ;         /* 0x0000580000047ab9 */
        |                                                                        /* 0x000fe40000000a00 */
        |        /*0070*/                   UIMAD.WIDE.U32 UR4, UR6, UR7, UR4 ;  /* 0x00000007060472a5 */
        |                                                                        /* 0x000fe2000f8e0004 */
        |        /*0080*/                   HADD2 R0, R0.F32, -RZ.H0_H0 ;        /* 0xa00000ff00007230 */
        |                                                                        /* 0x004e240000000400 */
        |        /*0090*/                   NOP ;                                /* 0x0000000000007918 */
        |                                                                        /* 0x001fd00000000800 */
        |        /*00a0*/                   STG.E.SYS [UR4], R0 ;                /* 0x00000000ff007986 */
        |                                                                        /* 0x000fe2000c10e904 */
        |        /*00b0*/                   EXIT ;                               /* 0x000000000000794d */
        |                                                                        /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hadd2SelectedHighSm89" ->
      """//--------------------- .text.hadd2_selected_high --------------------------
        |	.section	.text.hadd2_selected_high,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hadd2_selected_high
        |        .type           hadd2_selected_high,@function
        |        .size           hadd2_selected_high,(.L_x_1 - hadd2_selected_high)
        |        .other          hadd2_selected_high,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hadd2_selected_high:
        |.text.hadd2_selected_high:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
        |                                                                                          /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
        |                                                                                          /* 0x000e220000002100 */
        |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
        |                                                                                          /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
        |                                                                                          /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
        |                                                                                          /* 0x0c0fe400078e0007 */
        |        /*0060*/                   LDG.E.U16.CONSTANT R2, [R2.64+0x2] ;                   /* 0x0000020402027981 */
        |                                                                                          /* 0x000ea8000c1e9500 */
        |        /*0070*/                   LDG.E.U16.CONSTANT R5, [R4.64+0x2] ;                   /* 0x0000020404057981 */
        |                                                                                          /* 0x000ea2000c1e9500 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
        |                                                                                          /* 0x000fe200078e0007 */
        |        /*0090*/                   HADD2 R9, R2.H0_H0, R5.H0_H0 ;                         /* 0x2000000502097230 */
        |                                                                                          /* 0x004fca0000000800 */
        |        /*00a0*/                   STG.E [R6.64], R9 ;                                    /* 0x0000000906007986 */
        |                                                                                          /* 0x000fe2000c101904 */
        |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hadd2SelectedLowSm89" ->
      """//--------------------- .text.hadd2_selected_low  --------------------------
        |	.section	.text.hadd2_selected_low,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hadd2_selected_low
        |        .type           hadd2_selected_low,@function
        |        .size           hadd2_selected_low,(.L_x_1 - hadd2_selected_low)
        |        .other          hadd2_selected_low,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hadd2_selected_low:
        |.text.hadd2_selected_low:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
        |                                                                                          /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
        |                                                                                          /* 0x000e220000002100 */
        |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
        |                                                                                          /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
        |                                                                                          /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
        |                                                                                          /* 0x0c0fe400078e0007 */
        |        /*0060*/                   LDG.E.U16.CONSTANT R2, [R2.64] ;                       /* 0x0000000402027981 */
        |                                                                                          /* 0x000ea8000c1e9500 */
        |        /*0070*/                   LDG.E.U16.CONSTANT R5, [R4.64] ;                       /* 0x0000000404057981 */
        |                                                                                          /* 0x000ea2000c1e9500 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
        |                                                                                          /* 0x000fe200078e0007 */
        |        /*0090*/                   HADD2 R9, R2.H0_H0, R5.H0_H0 ;                         /* 0x2000000502097230 */
        |                                                                                          /* 0x004fca0000000800 */
        |        /*00a0*/                   STG.E [R6.64], R9 ;                                    /* 0x0000000906007986 */
        |                                                                                          /* 0x000fe2000c101904 */
        |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hadd2Sm89" ->
      """//--------------------- .text.convert_half        --------------------------
        |	.section	.text.convert_half,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         convert_half
        |        .type           convert_half,@function
        |        .size           convert_half,(.L_x_1 - convert_half)
        |        .other          convert_half,@"STO_CUDA_ENTRY STV_DEFAULT"
        |convert_half:
        |.text.convert_half:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_CTAID.X ;
        |        /*0020*/                   MOV R5, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R4, R5, c[0x0][0x168] ;
        |        /*0050*/                   LDG.E.CONSTANT R2, [R2.64] ;
        |        /*0060*/                   SHF.L.U32 R4, R4, 0x1, RZ ;
        |        /*0070*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x160] ;
        |        /*0080*/                   HADD2.F32 R7, -RZ, R2.reuse.H0_H0 ;
        |        /*0090*/                   HADD2.F32 R9, -RZ, R2.H1_H1 ;
        |        /*00a0*/                   STG.E [R4.64], R7 ;
        |        /*00b0*/                   STG.E [R4.64+0x4], R9 ;
        |        /*00c0*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hfma2Constant38004000Sm89Patched" ->
      """//--------------------- .text.hfma2_mma_probe     --------------------------
        |	.section	.text.hfma2_mma_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hfma2_mma_probe
        |        .type           hfma2_mma_probe,@function
        |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
        |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hfma2_mma_probe:
        |.text.hfma2_mma_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
        |                                                                                          /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
        |                                                                                          /* 0x000e220000002100 */
        |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
        |                                                                                          /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
        |                                                                                          /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
        |                                                                                          /* 0x0c0fe400078e0007 */
        |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
        |                                                                                          /* 0x000ea8000c1e1900 */
        |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
        |                                                                                          /* 0x000ea2000c1e1900 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
        |                                                                                          /* 0x000fe200078e0007 */
        |        /*0090*/                   HFMA2.MMA R9, -RZ, RZ, 0.5, 2 ;                        /* 0x38004000ff097435 */
        |                                                                                          /* 0x004fca00000001ff */
        |        /*00a0*/                   STG.E [R6.64], R9 ;                                    /* 0x0000000906007986 */
        |                                                                                          /* 0x000fe2000c101904 */
        |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hfma2Constant7c003c00Sm89Patched" ->
      """//--------------------- .text.hfma2_mma_probe     --------------------------
        |	.section	.text.hfma2_mma_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hfma2_mma_probe
        |        .type           hfma2_mma_probe,@function
        |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
        |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hfma2_mma_probe:
        |.text.hfma2_mma_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
        |                                                                                          /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
        |                                                                                          /* 0x000e220000002100 */
        |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
        |                                                                                          /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
        |                                                                                          /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
        |                                                                                          /* 0x0c0fe400078e0007 */
        |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
        |                                                                                          /* 0x000ea8000c1e1900 */
        |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
        |                                                                                          /* 0x000ea2000c1e1900 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
        |                                                                                          /* 0x000fe200078e0007 */
        |        /*0090*/                   HFMA2.MMA R9, -RZ, RZ, +INF , 1 ;                      /* 0x7c003c00ff097435 */
        |                                                                                          /* 0x004fca00000001ff */
        |        /*00a0*/                   STG.E [R6.64], R9 ;                                    /* 0x0000000906007986 */
        |                                                                                          /* 0x000fe2000c101904 */
        |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hfma2Constant7c01fc01Sm89Patched" ->
      """//--------------------- .text.hfma2_mma_probe     --------------------------
        |	.section	.text.hfma2_mma_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hfma2_mma_probe
        |        .type           hfma2_mma_probe,@function
        |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
        |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hfma2_mma_probe:
        |.text.hfma2_mma_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
        |                                                                                          /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
        |                                                                                          /* 0x000e220000002100 */
        |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
        |                                                                                          /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
        |                                                                                          /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
        |                                                                                          /* 0x0c0fe400078e0007 */
        |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
        |                                                                                          /* 0x000ea8000c1e1900 */
        |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
        |                                                                                          /* 0x000ea2000c1e1900 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
        |                                                                                          /* 0x000fe200078e0007 */
        |        /*0090*/                   HFMA2.MMA R9, -RZ, RZ, +SNAN , -SNAN  ;                /* 0x7c01fc01ff097435 */
        |                                                                                          /* 0x004fca00000001ff */
        |        /*00a0*/                   STG.E [R6.64], R9 ;                                    /* 0x0000000906007986 */
        |                                                                                          /* 0x000fe2000c101904 */
        |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hfma2Constant7e013c00Sm89Patched" ->
      """//--------------------- .text.hfma2_mma_probe     --------------------------
        |	.section	.text.hfma2_mma_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hfma2_mma_probe
        |        .type           hfma2_mma_probe,@function
        |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
        |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hfma2_mma_probe:
        |.text.hfma2_mma_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
        |                                                                                          /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
        |                                                                                          /* 0x000e220000002100 */
        |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
        |                                                                                          /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
        |                                                                                          /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
        |                                                                                          /* 0x0c0fe400078e0007 */
        |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
        |                                                                                          /* 0x000ea8000c1e1900 */
        |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
        |                                                                                          /* 0x000ea2000c1e1900 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
        |                                                                                          /* 0x000fe200078e0007 */
        |        /*0090*/                   HFMA2.MMA R9, -RZ, RZ, +QNAN , 1 ;                     /* 0x7e013c00ff097435 */
        |                                                                                          /* 0x004fca00000001ff */
        |        /*00a0*/                   STG.E [R6.64], R9 ;                                    /* 0x0000000906007986 */
        |                                                                                          /* 0x000fe2000c101904 */
        |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hfma2Constant80000000Sm89Patched" ->
      """//--------------------- .text.hfma2_mma_probe     --------------------------
        |	.section	.text.hfma2_mma_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hfma2_mma_probe
        |        .type           hfma2_mma_probe,@function
        |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
        |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hfma2_mma_probe:
        |.text.hfma2_mma_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
        |                                                                                          /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
        |                                                                                          /* 0x000e220000002100 */
        |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
        |                                                                                          /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
        |                                                                                          /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
        |                                                                                          /* 0x0c0fe400078e0007 */
        |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
        |                                                                                          /* 0x000ea8000c1e1900 */
        |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
        |                                                                                          /* 0x000ea2000c1e1900 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
        |                                                                                          /* 0x000fe200078e0007 */
        |        /*0090*/                   HFMA2.MMA R9, -RZ, RZ, -0.0 , 0 ;                      /* 0x80000000ff097435 */
        |                                                                                          /* 0x004fca00000001ff */
        |        /*00a0*/                   STG.E [R6.64], R9 ;                                    /* 0x0000000906007986 */
        |                                                                                          /* 0x000fe2000c101904 */
        |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hfma2ConstantFc007c00Sm89Patched" ->
      """//--------------------- .text.hfma2_mma_probe     --------------------------
        |	.section	.text.hfma2_mma_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hfma2_mma_probe
        |        .type           hfma2_mma_probe,@function
        |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
        |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hfma2_mma_probe:
        |.text.hfma2_mma_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
        |                                                                                          /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
        |                                                                                          /* 0x000e220000002100 */
        |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
        |                                                                                          /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
        |                                                                                          /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
        |                                                                                          /* 0x0c0fe400078e0007 */
        |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
        |                                                                                          /* 0x000ea8000c1e1900 */
        |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
        |                                                                                          /* 0x000ea2000c1e1900 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
        |                                                                                          /* 0x000fe200078e0007 */
        |        /*0090*/                   HFMA2.MMA R9, -RZ, RZ, -INF , +INF  ;                  /* 0xfc007c00ff097435 */
        |                                                                                          /* 0x004fca00000001ff */
        |        /*00a0*/                   STG.E [R6.64], R9 ;                                    /* 0x0000000906007986 */
        |                                                                                          /* 0x000fe2000c101904 */
        |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hfma2ConstantFe017e01Sm89Patched" ->
      """//--------------------- .text.hfma2_mma_probe     --------------------------
        |	.section	.text.hfma2_mma_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hfma2_mma_probe
        |        .type           hfma2_mma_probe,@function
        |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
        |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hfma2_mma_probe:
        |.text.hfma2_mma_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
        |                                                                                          /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
        |                                                                                          /* 0x000e220000002100 */
        |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
        |                                                                                          /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
        |                                                                                          /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
        |                                                                                          /* 0x0c0fe400078e0007 */
        |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
        |                                                                                          /* 0x000ea8000c1e1900 */
        |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
        |                                                                                          /* 0x000ea2000c1e1900 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
        |                                                                                          /* 0x000fe200078e0007 */
        |        /*0090*/                   HFMA2.MMA R9, -RZ, RZ, -QNAN , +QNAN  ;                /* 0xfe017e01ff097435 */
        |                                                                                          /* 0x004fca00000001ff */
        |        /*00a0*/                   STG.E [R6.64], R9 ;                                    /* 0x0000000906007986 */
        |                                                                                          /* 0x000fe2000c101904 */
        |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hfma2MmaAddReferenceSm89" ->
      """//--------------------- .text.hfma2_add_reference --------------------------
        |	.section	.text.hfma2_add_reference,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hfma2_add_reference
        |        .type           hfma2_add_reference,@function
        |        .size           hfma2_add_reference,(.L_x_1 - hfma2_add_reference)
        |        .other          hfma2_add_reference,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hfma2_add_reference:
        |.text.hfma2_add_reference:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
        |                                                                                          /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
        |                                                                                          /* 0x000e220000002100 */
        |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
        |                                                                                          /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
        |                                                                                          /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
        |                                                                                          /* 0x0c0fe400078e0007 */
        |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
        |                                                                                          /* 0x000ea8000c1e1900 */
        |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
        |                                                                                          /* 0x000ea2000c1e1900 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
        |                                                                                          /* 0x000fe200078e0007 */
        |        /*0090*/                   HADD2 R9, R2, R5 ;                                     /* 0x0000000502097230 */
        |                                                                                          /* 0x004fca0000000000 */
        |        /*00a0*/                   STG.E [R6.64], R9 ;                                    /* 0x0000000906007986 */
        |                                                                                          /* 0x000fe2000c101904 */
        |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hfma2MmaAliasSm89Patched" ->
      """//--------------------- .text.hfma2_mma_probe     --------------------------
        |	.section	.text.hfma2_mma_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hfma2_mma_probe
        |        .type           hfma2_mma_probe,@function
        |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
        |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hfma2_mma_probe:
        |.text.hfma2_mma_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
        |                                                                                          /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
        |                                                                                          /* 0x000e220000002100 */
        |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
        |                                                                                          /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
        |                                                                                          /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
        |                                                                                          /* 0x0c0fe400078e0007 */
        |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
        |                                                                                          /* 0x000ea8000c1e1900 */
        |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
        |                                                                                          /* 0x000ea2000c1e1900 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
        |                                                                                          /* 0x000fe200078e0007 */
        |        /*0090*/                   HFMA2.MMA R5, R2, 1, 1, R5 ;                           /* 0x3c003c0002057835 */
        |                                                                                          /* 0x004fca0000000005 */
        |        /*00a0*/                   STG.E [R6.64], R5 ;                                    /* 0x0000000506007986 */
        |                                                                                          /* 0x000fe2000c101904 */
        |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hfma2MmaNonaliasSm89Patched" ->
      """//--------------------- .text.hfma2_mma_probe     --------------------------
        |	.section	.text.hfma2_mma_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hfma2_mma_probe
        |        .type           hfma2_mma_probe,@function
        |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
        |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hfma2_mma_probe:
        |.text.hfma2_mma_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
        |                                                                                          /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
        |                                                                                          /* 0x000e220000002100 */
        |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
        |                                                                                          /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
        |                                                                                          /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
        |                                                                                          /* 0x0c0fe400078e0007 */
        |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
        |                                                                                          /* 0x000ea8000c1e1900 */
        |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
        |                                                                                          /* 0x000ea2000c1e1900 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
        |                                                                                          /* 0x000fe200078e0007 */
        |        /*0090*/                   HFMA2.MMA R9, R2, 1, 1, R5 ;                           /* 0x3c003c0002097835 */
        |                                                                                          /* 0x004fca0000000005 */
        |        /*00a0*/                   STG.E [R6.64], R9 ;                                    /* 0x0000000906007986 */
        |                                                                                          /* 0x000fe2000c101904 */
        |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hfma2MmaZeroAliasSm89Patched" ->
      """//--------------------- .text.hfma2_mma_probe     --------------------------
        |	.section	.text.hfma2_mma_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hfma2_mma_probe
        |        .type           hfma2_mma_probe,@function
        |        .size           hfma2_mma_probe,(.L_x_1 - hfma2_mma_probe)
        |        .other          hfma2_mma_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hfma2_mma_probe:
        |.text.hfma2_mma_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
        |                                                                                          /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
        |                                                                                          /* 0x000e220000002100 */
        |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
        |                                                                                          /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
        |                                                                                          /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
        |                                                                                          /* 0x0c0fe400078e0007 */
        |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
        |                                                                                          /* 0x000ea8000c1e1900 */
        |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
        |                                                                                          /* 0x000ea2000c1e1900 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
        |                                                                                          /* 0x000fe200078e0007 */
        |        /*0090*/                   HFMA2.MMA R5, R2, RZ, R5 ;                             /* 0x200000ff02057235 */
        |                                                                                          /* 0x004fca0000000005 */
        |        /*00a0*/                   STG.E [R6.64], R5 ;                                    /* 0x0000000506007986 */
        |                                                                                          /* 0x000fe2000c101904 */
        |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hfma2SelectedSm89" ->
      """//--------------------- .text.hfma2_selected      --------------------------
        |	.section	.text.hfma2_selected,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         hfma2_selected
        |        .type           hfma2_selected,@function
        |        .size           hfma2_selected,(.L_x_1 - hfma2_selected)
        |        .other          hfma2_selected,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hfma2_selected:
        |.text.hfma2_selected:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R8, SR_CTAID.X ;
        |        /*0020*/                   MOV R9, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   S2R R3, SR_TID.X ;
        |        /*0050*/                   IMAD R8, R8, c[0x0][0x0], R3 ;
        |        /*0060*/                   IMAD.WIDE.U32 R2, R8, R9, c[0x0][0x168] ;
        |        /*0070*/                   IMAD.WIDE.U32 R4, R8.reuse, R9.reuse, c[0x0][0x170] ;
        |        /*0080*/                   LDG.E.U16.CONSTANT R2, [R2.64] ;
        |        /*0090*/                   IMAD.WIDE.U32 R6, R8.reuse, R9.reuse, c[0x0][0x178] ;
        |        /*00a0*/                   LDG.E.U16.CONSTANT R5, [R4.64+0x2] ;
        |        /*00b0*/                   LDG.E.CONSTANT R6, [R6.64] ;
        |        /*00c0*/                   IMAD.WIDE.U32 R8, R8, R9, c[0x0][0x160] ;
        |        /*00d0*/                   HFMA2 R11, R2.H0_H0, R5.H0_H0, R6 ;
        |        /*00e0*/                   STG.E [R8.64], R11 ;
        |        /*00f0*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hfma2Sm89" ->
      """//--------------------- .text.hfma2_vector        --------------------------
        |	.section	.text.hfma2_vector,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         hfma2_vector
        |        .type           hfma2_vector,@function
        |        .size           hfma2_vector,(.L_x_1 - hfma2_vector)
        |        .other          hfma2_vector,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hfma2_vector:
        |.text.hfma2_vector:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R8, SR_CTAID.X ;
        |        /*0020*/                   MOV R9, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   S2R R3, SR_TID.X ;
        |        /*0050*/                   IMAD R8, R8, c[0x0][0x0], R3 ;
        |        /*0060*/                   IMAD.WIDE.U32 R2, R8, R9, c[0x0][0x168] ;
        |        /*0070*/                   IMAD.WIDE.U32 R4, R8.reuse, R9.reuse, c[0x0][0x170] ;
        |        /*0080*/                   LDG.E.CONSTANT R2, [R2.64] ;
        |        /*0090*/                   IMAD.WIDE.U32 R6, R8.reuse, R9.reuse, c[0x0][0x178] ;
        |        /*00a0*/                   LDG.E.CONSTANT R5, [R4.64] ;
        |        /*00b0*/                   LDG.E.CONSTANT R6, [R6.64] ;
        |        /*00c0*/                   IMAD.WIDE.U32 R8, R8, R9, c[0x0][0x160] ;
        |        /*00d0*/                   HFMA2 R11, R2, R5, R6 ;
        |        /*00e0*/                   STG.E [R8.64], R11 ;
        |        /*00f0*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hmnmx2MixedAliasSm80Patched" ->
      """//--------------------- .text.hmnmx2_mixed_alias_probe --------------------------
        |	.section	.text.hmnmx2_mixed_alias_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         hmnmx2_mixed_alias_probe
        |        .type           hmnmx2_mixed_alias_probe,@function
        |        .size           hmnmx2_mixed_alias_probe,(.L_x_1 - hmnmx2_mixed_alias_probe)
        |        .other          hmnmx2_mixed_alias_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmnmx2_mixed_alias_probe:
        |.text.hmnmx2_mixed_alias_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                             /* 0x00000a0000017a02 */
        |                                                                                      /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R2, SR_TID.X ;                                 /* 0x0000000000027919 */
        |                                                                                      /* 0x000e220000002100 */
        |        /*0020*/                   HFMA2.MMA R3, -RZ, RZ, 0, 2.384185791015625e-07 ;  /* 0x00000004ff037435 */
        |                                                                                      /* 0x000fe200000001ff */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                       /* 0x0000460000047ab9 */
        |                                                                                      /* 0x000fd20000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R4, R2, R3, c[0x0][0x168] ;          /* 0x00005a0002047625 */
        |                                                                                      /* 0x001fc800078e0003 */
        |        /*0050*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;          /* 0x0000580002027625 */
        |                                                                                      /* 0x000fe400078e0003 */
        |        /*0060*/                   LDG.E.U16.CONSTANT R5, [R4.64] ;                   /* 0x0000000404057981 */
        |                                                                                      /* 0x000ea8000c1e9500 */
        |        /*0070*/                   LDG.E R0, [R2.64] ;                                /* 0x0000000402007981 */
        |                                                                                      /* 0x000ea4000c1e1900 */
        |        /*0080*/                   HMNMX2 R0, R0, R5.H0_H0, !PT ;                     /* 0x2000000500007240 */
        |                                                                                      /* 0x004fca0007800000 */
        |        /*0090*/                   STG.E [R2.64], R0 ;                                /* 0x0000000002007986 */
        |                                                                                      /* 0x000fe2000c101904 */
        |        /*00a0*/                   EXIT ;                                             /* 0x000000000000794d */
        |                                                                                      /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hmnmx2MixedSm80" ->
      """//--------------------- .text.hmnmx2_mixed_probe  --------------------------
        |	.section	.text.hmnmx2_mixed_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hmnmx2_mixed_probe
        |        .type           hmnmx2_mixed_probe,@function
        |        .size           hmnmx2_mixed_probe,(.L_x_1 - hmnmx2_mixed_probe)
        |        .other          hmnmx2_mixed_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmnmx2_mixed_probe:
        |.text.hmnmx2_mixed_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
        |                                                                                          /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
        |                                                                                          /* 0x000e220000002100 */
        |        /*0020*/                   HFMA2.MMA R7, -RZ, RZ, 0, 2.384185791015625e-07 ;      /* 0x00000004ff077435 */
        |                                                                                          /* 0x000fe200000001ff */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
        |                                                                                          /* 0x000fd20000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x160] ;              /* 0x0000580006027625 */
        |                                                                                          /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x168] ;  /* 0x00005a0006047625 */
        |                                                                                          /* 0x0c0fe400078e0007 */
        |        /*0060*/                   LDG.E.CONSTANT R2, [R2.64] ;                           /* 0x0000000402027981 */
        |                                                                                          /* 0x000ea8000c1e9900 */
        |        /*0070*/                   LDG.E.U16.CONSTANT R5, [R4.64] ;                       /* 0x0000000404057981 */
        |                                                                                          /* 0x000ea2000c1e9500 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x170] ;              /* 0x00005c0006067625 */
        |                                                                                          /* 0x000fe200078e0007 */
        |        /*0090*/                   HMNMX2 R9, R2, R5.H0_H0, !PT ;                         /* 0x2000000502097240 */
        |                                                                                          /* 0x004fca0007800000 */
        |        /*00a0*/                   STG.E [R6.64], R9 ;                                    /* 0x0000000906007986 */
        |                                                                                          /* 0x000fe2000c101904 */
        |        /*00b0*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hmnmx2SelectSm80" ->
      """//--------------------- .text.hmnmx2_select_probe --------------------------
        |	.section	.text.hmnmx2_select_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         hmnmx2_select_probe
        |        .type           hmnmx2_select_probe,@function
        |        .size           hmnmx2_select_probe,(.L_x_1 - hmnmx2_select_probe)
        |        .other          hmnmx2_select_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmnmx2_select_probe:
        |.text.hmnmx2_select_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                             /* 0x00000a0000017a02 */
        |                                                                                      /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                 /* 0x0000000000067919 */
        |                                                                                      /* 0x000e220000002100 */
        |        /*0020*/                   HFMA2.MMA R7, -RZ, RZ, 0, 2.384185791015625e-07 ;  /* 0x00000004ff077435 */
        |                                                                                      /* 0x000fe200000001ff */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                       /* 0x0000460000047ab9 */
        |                                                                                      /* 0x000fd20000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x160] ;          /* 0x0000580006027625 */
        |                                                                                      /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7, c[0x0][0x168] ;    /* 0x00005a0006047625 */
        |                                                                                      /* 0x040fe400078e0007 */
        |        /*0060*/                   LDG.E.U16.CONSTANT R2, [R2.64] ;                   /* 0x0000000402027981 */
        |                                                                                      /* 0x000ea8000c1e9500 */
        |        /*0070*/                   LDG.E.U16.CONSTANT R5, [R4.64] ;                   /* 0x0000000404057981 */
        |                                                                                      /* 0x000ea2000c1e9500 */
        |        /*0080*/                   SHF.L.U32 R6, R6, 0x1, RZ ;                        /* 0x0000000106067819 */
        |                                                                                      /* 0x000fca00000006ff */
        |        /*0090*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x170] ;          /* 0x00005c0006067625 */
        |                                                                                      /* 0x000fe200078e0007 */
        |        /*00a0*/                   HMNMX2 R0, R2.reuse.H0_H0, R5.reuse.H0_H0, PT ;    /* 0x2000000502007240 */
        |                                                                                      /* 0x0c4fe40003800800 */
        |        /*00b0*/                   HMNMX2 R8, R2.H0_H0, R5.H0_H0, !PT ;               /* 0x2000000502087240 */
        |                                                                                      /* 0x000fc60007800800 */
        |        /*00c0*/                   LOP3.LUT R9, R0, 0xffff, RZ, 0xc0, !PT ;           /* 0x0000ffff00097812 */
        |                                                                                      /* 0x000fe400078ec0ff */
        |        /*00d0*/                   LOP3.LUT R11, R8, 0xffff, RZ, 0xc0, !PT ;          /* 0x0000ffff080b7812 */
        |                                                                                      /* 0x000fc600078ec0ff */
        |        /*00e0*/                   STG.E [R6.64], R9 ;                                /* 0x0000000906007986 */
        |                                                                                      /* 0x000fe8000c101904 */
        |        /*00f0*/                   STG.E [R6.64+0x4], R11 ;                           /* 0x0000040b06007986 */
        |                                                                                      /* 0x000fe2000c101904 */
        |        /*0100*/                   EXIT ;                                             /* 0x000000000000794d */
        |                                                                                      /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hmul2F32Sm75Patched" ->
      """//--------------------- .text.hmul2_f32_probe     --------------------------
        |	.section	.text.hmul2_f32_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=18"
        |	.align	128
        |        .global         hmul2_f32_probe
        |        .type           hmul2_f32_probe,@function
        |        .size           hmul2_f32_probe,(.L_x_1 - hmul2_f32_probe)
        |        .other          hmul2_f32_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmul2_f32_probe:
        |.text.hmul2_f32_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                     /* 0x00000a0000017a02 */
        |                                                                              /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R0, SR_CTAID.X ;                       /* 0x0000000000007919 */
        |                                                                              /* 0x000e220000002500 */
        |        /*0020*/                   MOV R7, 0x4 ;                              /* 0x0000000400077802 */
        |                                                                              /* 0x000fc60000000f00 */
        |        /*0030*/                   S2R R3, SR_TID.X ;                         /* 0x0000000000037919 */
        |                                                                              /* 0x000e240000002100 */
        |        /*0040*/                   IMAD R0, R0, c[0x0][0x0], R3 ;             /* 0x0000000000007a24 */
        |                                                                              /* 0x001fc800078e0203 */
        |        /*0050*/                   IMAD.WIDE.U32 R2, R0, R7, c[0x0][0x168] ;  /* 0x00005a0000027625 */
        |                                                                              /* 0x000fc800078e0007 */
        |        /*0060*/                   IMAD.WIDE.U32 R4, R0, R7, c[0x0][0x170] ;  /* 0x00005c0000047625 */
        |                                                                              /* 0x000fc800078e0007 */
        |        /*0070*/                   LDG.E.SYS R2, [R2] ;                       /* 0x0000000002027381 */
        |                                                                              /* 0x000ea800001ee900 */
        |        /*0080*/                   LDG.E.SYS R4, [R4] ;                       /* 0x0000000004047381 */
        |                                                                              /* 0x000ee200001ee900 */
        |        /*0090*/                   SHF.L.U32 R6, R0, 0x2, RZ ;                /* 0x0000000200067819 */
        |                                                                              /* 0x000fca00000006ff */
        |        /*00a0*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;  /* 0x0000580006067625 */
        |                                                                              /* 0x000fe200078e0007 */
        |        /*00b0*/                   HADD2.F32 R0, R2.reuse.H0_H0, -RZ.H0_H0 ;  /* 0xa00000ff02007230 */
        |                                                                              /* 0x044fe40000004800 */
        |        /*00c0*/                   HADD2.F32 R8, R2.H1_H1, -RZ.H0_H0 ;        /* 0xa00000ff02087230 */
        |                                                                              /* 0x000fe40000004c00 */
        |        /*00d0*/                   HADD2.F32 R9, R4.reuse.H0_H0, -RZ.H0_H0 ;  /* 0xa00000ff04097230 */
        |                                                                              /* 0x048fe40000004800 */
        |        /*00e0*/                   HADD2.F32 R11, R4.H1_H1, -RZ.H0_H0 ;       /* 0xa00000ff040b7230 */
        |                                                                              /* 0x000fcc0000004c00 */
        |        /*00f0*/                   HMUL2.F32 R13, R2.H0_H0, R4.H0_H0 ;        /* 0x20000004020d7232 */
        |                                                                              /* 0x000fcc0000004800 */
        |        /*0100*/                   HMUL2.F32 R15, R2.H0_H0, R4.H1_H1 ;        /* 0x30000004020f7232 */
        |                                                                              /* 0x000fcc0000004800 */
        |        /*0110*/                   HMUL2.F32 R9, R2.H1_H1, R4.H0_H0 ;         /* 0x2000000402097232 */
        |                                                                              /* 0x000fcc0000004c00 */
        |        /*0120*/                   HMUL2.F32 R11, R2.H1_H1, R4.H1_H1 ;        /* 0x30000004020b7232 */
        |                                                                              /* 0x000fcc0000004c00 */
        |        /*0130*/                   STG.E.SYS [R6], R13 ;                      /* 0x0000000d06007386 */
        |                                                                              /* 0x000fe8000010e900 */
        |        /*0140*/                   STG.E.SYS [R6+0x4], R15 ;                  /* 0x0000040f06007386 */
        |                                                                              /* 0x000fe8000010e900 */
        |        /*0150*/                   STG.E.SYS [R6+0x8], R9 ;                   /* 0x0000080906007386 */
        |                                                                              /* 0x000fe8000010e900 */
        |        /*0160*/                   STG.E.SYS [R6+0xc], R11 ;                  /* 0x00000c0b06007386 */
        |                                                                              /* 0x000fe2000010e900 */
        |        /*0170*/                   EXIT ;                                     /* 0x000000000000794d */
        |                                                                              /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hmul2SelectedSm89" ->
      """//--------------------- .text.hmul2_selected      --------------------------
        |	.section	.text.hmul2_selected,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hmul2_selected
        |        .type           hmul2_selected,@function
        |        .size           hmul2_selected,(.L_x_1 - hmul2_selected)
        |        .other          hmul2_selected,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmul2_selected:
        |.text.hmul2_selected:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R6, SR_CTAID.X ;
        |        /*0020*/                   MOV R7, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   S2R R3, SR_TID.X ;
        |        /*0050*/                   IMAD R6, R6, c[0x0][0x0], R3 ;
        |        /*0060*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;
        |        /*0070*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;
        |        /*0080*/                   LDG.E.U16.CONSTANT R2, [R2.64] ;
        |        /*0090*/                   LDG.E.U16.CONSTANT R5, [R4.64+0x2] ;
        |        /*00a0*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;
        |        /*00b0*/                   HMUL2 R9, R2.H0_H0, R5.H0_H0 ;
        |        /*00c0*/                   STG.E [R6.64], R9 ;
        |        /*00d0*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hmul2Sm89" ->
      """//--------------------- .text.hmul2_vector        --------------------------
        |	.section	.text.hmul2_vector,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hmul2_vector
        |        .type           hmul2_vector,@function
        |        .size           hmul2_vector,(.L_x_1 - hmul2_vector)
        |        .other          hmul2_vector,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmul2_vector:
        |.text.hmul2_vector:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R6, SR_CTAID.X ;
        |        /*0020*/                   MOV R7, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   S2R R3, SR_TID.X ;
        |        /*0050*/                   IMAD R6, R6, c[0x0][0x0], R3 ;
        |        /*0060*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;
        |        /*0070*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;
        |        /*0080*/                   LDG.E.CONSTANT R2, [R2.64] ;
        |        /*0090*/                   LDG.E.CONSTANT R5, [R4.64] ;
        |        /*00a0*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;
        |        /*00b0*/                   HMUL2 R9, R2, R5 ;
        |        /*00c0*/                   STG.E [R6.64], R9 ;
        |        /*00d0*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hsetp2GeuRawSm89Patched" ->
      """//--------------------- .text.hsetp2_probe        --------------------------
        |	.section	.text.hsetp2_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         hsetp2_probe
        |        .type           hsetp2_probe,@function
        |        .size           hsetp2_probe,(.L_x_1 - hsetp2_probe)
        |        .other          hsetp2_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hsetp2_probe:
        |.text.hsetp2_probe:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;       /* 0x00000a00ff017624 */
        |                                                                                 /* 0x000fe400078e00ff */
        |        /*0010*/                   S2R R8, SR_TID.X ;                            /* 0x0000000000087919 */
        |                                                                                 /* 0x000e220000002100 */
        |        /*0020*/                   MOV R11, 0x4 ;                                /* 0x00000004000b7802 */
        |                                                                                 /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                  /* 0x0000460000047ab9 */
        |                                                                                 /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R4, R8, R11, c[0x0][0x170] ;    /* 0x00005c0008047625 */
        |                                                                                 /* 0x001fc800078e000b */
        |        /*0050*/                   IMAD.WIDE.U32 R2, R8, R11, c[0x0][0x168] ;    /* 0x00005a0008027625 */
        |                                                                                 /* 0x000fe400078e000b */
        |        /*0060*/                   LDG.E R5, [R4.64] ;                           /* 0x0000000404057981 */
        |                                                                                 /* 0x000ea8000c1e1900 */
        |        /*0070*/                   LDG.E R2, [R2.64] ;                           /* 0x0000000402027981 */
        |                                                                                 /* 0x000ee2000c1e1900 */
        |        /*0080*/                   IMAD.SHL.U32 R0, R5, 0x2, RZ ;                /* 0x0000000205007824 */
        |                                                                                 /* 0x004fe200078e00ff */
        |        /*0090*/                   HSETP2.GEU.AND P0, PT, R2, R5, PT ;           /* 0x0000000502007234 */
        |                                                                                 /* 0x008fe40003f0e000 */
        |        /*00a0*/                   SHF.R.U32.HI R6, RZ, 0xf, R2 ;                /* 0x0000000fff067819 */
        |                                                                                 /* 0x000fc40000011602 */
        |        /*00b0*/                   LOP3.LUT R7, R0, 0xfffe0000, RZ, 0xc0, !PT ;  /* 0xfffe000000077812 */
        |                                                                                 /* 0x000fe400078ec0ff */
        |        /*00c0*/                   SEL R0, RZ, 0x1, !P0 ;                        /* 0x00000001ff007807 */
        |                                                                                 /* 0x000fe40004000000 */
        |        /*00d0*/                   LOP3.LUT R9, R7, 0x1fffe, R6, 0xf8, !PT ;     /* 0x0001fffe07097812 */
        |                                                                                 /* 0x000fe200078ef806 */
        |        /*00e0*/                   IMAD.WIDE.U32 R6, R8, R11, c[0x0][0x160] ;    /* 0x0000580008067625 */
        |                                                                                 /* 0x000fc600078e000b */
        |        /*00f0*/                   LOP3.LUT R9, R9, R0, RZ, 0xfc, !PT ;          /* 0x0000000009097212 */
        |                                                                                 /* 0x000fca00078efcff */
        |        /*0100*/                   STG.E [R6.64], R9 ;                           /* 0x0000000906007986 */
        |                                                                                 /* 0x000fe2000c101904 */
        |        /*0110*/                   EXIT ;                                        /* 0x000000000000794d */
        |                                                                                 /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hsetp2NeRawSm89Patched" ->
      """//--------------------- .text.hsetp2_ne_probe     --------------------------
        |	.section	.text.hsetp2_ne_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         hsetp2_ne_probe
        |        .type           hsetp2_ne_probe,@function
        |        .size           hsetp2_ne_probe,(.L_x_1 - hsetp2_ne_probe)
        |        .other          hsetp2_ne_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hsetp2_ne_probe:
        |.text.hsetp2_ne_probe:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;       /* 0x00000a00ff017624 */
        |                                                                                 /* 0x000fe400078e00ff */
        |        /*0010*/                   S2R R6, SR_TID.X ;                            /* 0x0000000000067919 */
        |                                                                                 /* 0x000e220000002100 */
        |        /*0020*/                   MOV R7, 0x4 ;                                 /* 0x0000000400077802 */
        |                                                                                 /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                  /* 0x0000460000047ab9 */
        |                                                                                 /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;     /* 0x00005a0006027625 */
        |                                                                                 /* 0x001fcc00078e0007 */
        |        /*0050*/                   LDG.E R2, [R2.64] ;                           /* 0x0000000402027981 */
        |                                                                                 /* 0x000ea2000c1e1900 */
        |        /*0060*/                   IMAD.WIDE.U32 R4, R6, R7, c[0x0][0x170] ;     /* 0x00005c0006047625 */
        |                                                                                 /* 0x000fcc00078e0007 */
        |        /*0070*/                   LDG.E R5, [R4.64] ;                           /* 0x0000000404057981 */
        |                                                                                 /* 0x000ee2000c1e1900 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;     /* 0x0000580006067625 */
        |                                                                                 /* 0x000fe200078e0007 */
        |        /*0090*/                   SHF.R.U32.HI R0, RZ, 0x10, R2 ;               /* 0x00000010ff007819 */
        |                                                                                 /* 0x004fe40000011602 */
        |        /*00a0*/                   HSETP2.NE.AND P0, PT, R2, RZ, PT ;            /* 0x000000ff02007234 */
        |                                                                                 /* 0x000fc60003f05000 */
        |        /*00b0*/                   IMAD.SHL.U32 R9, R0, 0x2, RZ ;                /* 0x0000000200097824 */
        |                                                                                 /* 0x000fe400078e00ff */
        |        /*00c0*/                   SEL R0, RZ, 0x1, !P0 ;                        /* 0x00000001ff007807 */
        |                                                                                 /* 0x000fc80004000000 */
        |        /*00d0*/                   LOP3.LUT R0, R9, 0xfffffffe, R0, 0xe2, !PT ;  /* 0xfffffffe09007812 */
        |                                                                                 /* 0x000fc800078ee200 */
        |        /*00e0*/                   LOP3.LUT R9, R0, 0x80000000, R5, 0xf8, !PT ;  /* 0x8000000000097812 */
        |                                                                                 /* 0x008fca00078ef805 */
        |        /*00f0*/                   STG.E [R6.64], R9 ;                           /* 0x0000000906007986 */
        |                                                                                 /* 0x000fe2000c101904 */
        |        /*0100*/                   EXIT ;                                        /* 0x000000000000794d */
        |                                                                                 /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hsetp2Sm89" ->
      """//--------------------- .text.hsetp2_probe        --------------------------
        |	.section	.text.hsetp2_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         hsetp2_probe
        |        .type           hsetp2_probe,@function
        |        .size           hsetp2_probe,(.L_x_1 - hsetp2_probe)
        |        .other          hsetp2_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hsetp2_probe:
        |.text.hsetp2_probe:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;          /* 0x00000a00ff017624 */
        |                                                                                    /* 0x000fe400078e00ff */
        |        /*0010*/                   S2R R8, SR_TID.X ;                               /* 0x0000000000087919 */
        |                                                                                    /* 0x000e220000002100 */
        |        /*0020*/                   MOV R11, 0x4 ;                                   /* 0x00000004000b7802 */
        |                                                                                    /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                     /* 0x0000460000047ab9 */
        |                                                                                    /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R4, R8, R11, c[0x0][0x170] ;       /* 0x00005c0008047625 */
        |                                                                                    /* 0x001fc800078e000b */
        |        /*0050*/                   IMAD.WIDE.U32 R2, R8, R11, c[0x0][0x168] ;       /* 0x00005a0008027625 */
        |                                                                                    /* 0x000fe400078e000b */
        |        /*0060*/                   LDG.E R5, [R4.64] ;                              /* 0x0000000404057981 */
        |                                                                                    /* 0x000ea8000c1e1900 */
        |        /*0070*/                   LDG.E R2, [R2.64] ;                              /* 0x0000000402027981 */
        |                                                                                    /* 0x000ee2000c1e1900 */
        |        /*0080*/                   IMAD.SHL.U32 R0, R5, 0x2, RZ ;                   /* 0x0000000205007824 */
        |                                                                                    /* 0x004fe200078e00ff */
        |        /*0090*/                   HSETP2.GEU.AND P0, PT, R2.H0_H0, R5.H0_H0, PT ;  /* 0x2000000502007234 */
        |                                                                                    /* 0x008fe40003f0e800 */
        |        /*00a0*/                   SHF.R.U32.HI R6, RZ, 0xf, R2 ;                   /* 0x0000000fff067819 */
        |                                                                                    /* 0x000fc40000011602 */
        |        /*00b0*/                   LOP3.LUT R7, R0, 0xfffe0000, RZ, 0xc0, !PT ;     /* 0xfffe000000077812 */
        |                                                                                    /* 0x000fe400078ec0ff */
        |        /*00c0*/                   SEL R0, RZ, 0x1, !P0 ;                           /* 0x00000001ff007807 */
        |                                                                                    /* 0x000fe40004000000 */
        |        /*00d0*/                   LOP3.LUT R9, R7, 0x1fffe, R6, 0xf8, !PT ;        /* 0x0001fffe07097812 */
        |                                                                                    /* 0x000fe200078ef806 */
        |        /*00e0*/                   IMAD.WIDE.U32 R6, R8, R11, c[0x0][0x160] ;       /* 0x0000580008067625 */
        |                                                                                    /* 0x000fc600078e000b */
        |        /*00f0*/                   LOP3.LUT R9, R9, R0, RZ, 0xfc, !PT ;             /* 0x0000000009097212 */
        |                                                                                    /* 0x000fca00078efcff */
        |        /*0100*/                   STG.E [R6.64], R9 ;                              /* 0x0000000906007986 */
        |                                                                                    /* 0x000fe2000c101904 */
        |        /*0110*/                   EXIT ;                                           /* 0x000000000000794d */
        |                                                                                    /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hsetp2TwoSm70" ->
      """//--------------------- .text.hsetp2_two          --------------------------
        |	.section	.text.hsetp2_two,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         hsetp2_two
        |        .type           hsetp2_two,@function
        |        .size           hsetp2_two,(.L_x_1 - hsetp2_two)
        |        .other          hsetp2_two,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hsetp2_two:
        |.text.hsetp2_two:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                     /* 0x00000a0000017a02 */
        |                                                                              /* 0x000fc40000000f00 */
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;              /* 0x000000fffffff389 */
        |                                                                              /* 0x000fe200000e00ff */
        |        /*0020*/                   S2R R6, SR_TID.X ;                         /* 0x0000000000067919 */
        |                                                                              /* 0x000e220000002100 */
        |        /*0030*/                   MOV R7, 0x4 ;                              /* 0x0000000400077802 */
        |                                                                              /* 0x000fca0000000f00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;  /* 0x00005a0006027625 */
        |                                                                              /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6, R7, c[0x0][0x170] ;  /* 0x00005c0006047625 */
        |                                                                              /* 0x000fc800078e0007 */
        |        /*0060*/                   LDG.E.SYS R2, [R2] ;                       /* 0x0000000002027381 */
        |                                                                              /* 0x000ea800001ee900 */
        |        /*0070*/                   LDG.E.SYS R5, [R4] ;                       /* 0x0000000004057381 */
        |                                                                              /* 0x000ea200001ee900 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;  /* 0x0000580006067625 */
        |                                                                              /* 0x000fe200078e0007 */
        |        /*0090*/                   HSETP2.GEU.AND P0, P1, R2, R5, PT ;        /* 0x0000000502007234 */
        |                                                                              /* 0x004fd0000390e000 */
        |        /*00a0*/                   SEL R9, RZ, 0x1, !P0 ;                     /* 0x00000001ff097807 */
        |                                                                              /* 0x000fe40004000000 */
        |        /*00b0*/                   SEL R11, RZ, 0x1, !P1 ;                    /* 0x00000001ff0b7807 */
        |                                                                              /* 0x000fcc0004800000 */
        |        /*00c0*/                   STG.E.SYS [R6], R9 ;                       /* 0x0000000906007386 */
        |                                                                              /* 0x000fe8000010e900 */
        |        /*00d0*/                   STG.E.SYS [R6+0x80], R11 ;                 /* 0x0000800b06007386 */
        |                                                                              /* 0x000fe2000010e900 */
        |        /*00e0*/                   EXIT ;                                     /* 0x000000000000794d */
        |                                                                              /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hsetp2TwoSm89" ->
      """//--------------------- .text.hsetp2_two          --------------------------
        |	.section	.text.hsetp2_two,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         hsetp2_two
        |        .type           hsetp2_two,@function
        |        .size           hsetp2_two,(.L_x_1 - hsetp2_two)
        |        .other          hsetp2_two,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hsetp2_two:
        |.text.hsetp2_two:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                 /* 0x00000a0000017a02 */
        |                                                                                          /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                                     /* 0x0000000000067919 */
        |                                                                                          /* 0x000e220000002100 */
        |        /*0020*/                   MOV R7, 0x4 ;                                          /* 0x0000000400077802 */
        |                                                                                          /* 0x000fe20000000f00 */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                           /* 0x0000460000047ab9 */
        |                                                                                          /* 0x000fc80000000a00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R7, c[0x0][0x168] ;              /* 0x00005a0006027625 */
        |                                                                                          /* 0x001fc800078e0007 */
        |        /*0050*/                   IMAD.WIDE.U32 R4, R6.reuse, R7.reuse, c[0x0][0x170] ;  /* 0x00005c0006047625 */
        |                                                                                          /* 0x0c0fe400078e0007 */
        |        /*0060*/                   LDG.E R2, [R2.64] ;                                    /* 0x0000000402027981 */
        |                                                                                          /* 0x000ea8000c1e1900 */
        |        /*0070*/                   LDG.E R5, [R4.64] ;                                    /* 0x0000000404057981 */
        |                                                                                          /* 0x000ea2000c1e1900 */
        |        /*0080*/                   IMAD.WIDE.U32 R6, R6, R7, c[0x0][0x160] ;              /* 0x0000580006067625 */
        |                                                                                          /* 0x000fe200078e0007 */
        |        /*0090*/                   HSETP2.GEU.AND P0, P1, R2, R5, PT ;                    /* 0x0000000502007234 */
        |                                                                                          /* 0x004fca000390e000 */
        |        /*00a0*/                   SEL R9, RZ, 0x1, !P0 ;                                 /* 0x00000001ff097807 */
        |                                                                                          /* 0x000fe40004000000 */
        |        /*00b0*/                   SEL R11, RZ, 0x1, !P1 ;                                /* 0x00000001ff0b7807 */
        |                                                                                          /* 0x000fc60004800000 */
        |        /*00c0*/                   STG.E [R6.64], R9 ;                                    /* 0x0000000906007986 */
        |                                                                                          /* 0x000fe8000c101904 */
        |        /*00d0*/                   STG.E [R6.64+0x80], R11 ;                              /* 0x0000800b06007986 */
        |                                                                                          /* 0x000fe2000c101904 */
        |        /*00e0*/                   EXIT ;                                                 /* 0x000000000000794d */
        |                                                                                          /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin
  )
}
