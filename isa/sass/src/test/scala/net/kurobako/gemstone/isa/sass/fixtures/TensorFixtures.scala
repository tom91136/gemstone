package net.kurobako.gemstone.isa.sass.fixtures

object TensorFixtures {
  val dmmaPtx: String =
    """.version 7.8
      |.target sm_80
      |.address_size 64
      |.visible .entry double_mma(.param .u64 out) {
      |  .reg .b64 %p, %offset;
      |  .reg .b32 %lane, %a, %b;
      |  .reg .f64 %left, %right, %c0, %c1;
      |  ld.param.u64 %p, [out];
      |  mov.u32 %lane, %laneid;
      |  add.u32 %a, %lane, 1;
      |  mad.lo.u32 %b, %lane, 2, 1;
      |  cvt.rn.f64.u32 %left, %a;
      |  cvt.rn.f64.u32 %right, %b;
      |  mov.f64 %c0, 0d4014000000000000;
      |  mov.f64 %c1, 0dc01c000000000000;
      |  mma.sync.aligned.m8n8k4.row.col.f64.f64.f64.f64 {%c0,%c1}, {%left}, {%right}, {%c0,%c1};
      |  mul.wide.u32 %offset, %lane, 16;
      |  add.u64 %p, %p, %offset;
      |  st.global.f64 [%p], %c0;
      |  st.global.f64 [%p+8], %c1;
      |  ret;
      |}
      |""".stripMargin

  def hmma884F16Ptx(a: String, b: String): String =
    s""".version 7.8
      |.target sm_70
      |.address_size 64
      |.visible .entry hmma884_f16_${a}_${b}_probe(
      | .param .u64 out,
      | .param .u64 a,
      | .param .u64 b,
      | .param .u64 c
      |) {
      |.reg .b32 %r<16>;
      |.reg .b64 %rd<8>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [a];
      |ld.param.u64 %rd2, [b];
      |ld.param.u64 %rd3, [c];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd4, %r0, 8;
      |mul.wide.u32 %rd5, %r0, 16;
      |add.u64 %rd6, %rd1, %rd4;
      |add.u64 %rd7, %rd2, %rd4;
      |ld.global.b32 %r1, [%rd6];
      |ld.global.b32 %r2, [%rd6+4];
      |ld.global.b32 %r3, [%rd7];
      |ld.global.b32 %r4, [%rd7+4];
      |add.u64 %rd6, %rd3, %rd5;
      |ld.global.b32 %r5, [%rd6];
      |ld.global.b32 %r6, [%rd6+4];
      |ld.global.b32 %r7, [%rd6+8];
      |ld.global.b32 %r8, [%rd6+12];
      |mma.sync.aligned.m8n8k4.$a.$b.f16.f16.f16.f16 {%r9,%r10,%r11,%r12},{%r1,%r2},{%r3,%r4},{%r5,%r6,%r7,%r8};
      |add.u64 %rd6, %rd0, %rd5;
      |st.global.b32 [%rd6], %r9;
      |st.global.b32 [%rd6+4], %r10;
      |st.global.b32 [%rd6+8], %r11;
      |st.global.b32 [%rd6+12], %r12;
      |ret;
      |}
      |""".stripMargin

  def hmma884F32Ptx(a: String, b: String): String =
    s""".version 7.8
      |.target sm_70
      |.address_size 64
      |.visible .entry hmma884_f32_${a}_${b}_probe(
      | .param .u64 out,
      | .param .u64 a,
      | .param .u64 b,
      | .param .u64 c
      |) {
      |.reg .pred %p<2>;
      |.reg .b32 %r<8>;
      |.reg .b64 %rd<8>;
      |.reg .f32 %f<16>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [a];
      |ld.param.u64 %rd2, [b];
      |ld.param.u64 %rd3, [c];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd4, %r0, 8;
      |mul.wide.u32 %rd5, %r0, 32;
      |add.u64 %rd6, %rd1, %rd4;
      |add.u64 %rd7, %rd2, %rd4;
      |ld.global.b32 %r1, [%rd6];
      |ld.global.b32 %r2, [%rd6+4];
      |ld.global.b32 %r3, [%rd7];
      |ld.global.b32 %r4, [%rd7+4];
      |add.u64 %rd6, %rd3, %rd5;
      |ld.global.f32 %f0, [%rd6];
      |ld.global.f32 %f1, [%rd6+4];
      |ld.global.f32 %f2, [%rd6+8];
      |ld.global.f32 %f3, [%rd6+12];
      |ld.global.f32 %f4, [%rd6+16];
      |ld.global.f32 %f5, [%rd6+20];
      |ld.global.f32 %f6, [%rd6+24];
      |ld.global.f32 %f7, [%rd6+28];
      |mma.sync.aligned.m8n8k4.$a.$b.f32.f16.f16.f32 {%f8,%f9,%f10,%f11,%f12,%f13,%f14,%f15},{%r1,%r2},{%r3,%r4},{%f0,%f1,%f2,%f3,%f4,%f5,%f6,%f7};
      |add.u64 %rd6, %rd0, %rd5;
      |st.global.f32 [%rd6], %f8;
      |st.global.f32 [%rd6+4], %f9;
      |st.global.f32 [%rd6+8], %f10;
      |st.global.f32 [%rd6+12], %f11;
      |st.global.f32 [%rd6+16], %f12;
      |st.global.f32 [%rd6+20], %f13;
      |st.global.f32 [%rd6+24], %f14;
      |st.global.f32 [%rd6+28], %f15;
      |ret;
      |}
      |""".stripMargin

  val hmma884F32ZeroPtx: String =
    """.version 7.8
      |.target sm_70
      |.address_size 64
      |.visible .entry hmma884_f32_probe(
      | .param .u64 out,
      | .param .u64 a,
      | .param .u64 b,
      | .param .u64 c
      |) {
      |.reg .pred %p<2>;
      |.reg .b32 %r<8>;
      |.reg .b64 %rd<8>;
      |.reg .f32 %f<16>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [a];
      |ld.param.u64 %rd2, [b];
      |ld.param.u64 %rd3, [c];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd4, %r0, 8;
      |mul.wide.u32 %rd5, %r0, 32;
      |add.u64 %rd6, %rd1, %rd4;
      |add.u64 %rd7, %rd2, %rd4;
      |ld.global.b32 %r1, [%rd6];
      |ld.global.b32 %r2, [%rd6+4];
      |ld.global.b32 %r3, [%rd7];
      |ld.global.b32 %r4, [%rd7+4];
      |add.u64 %rd6, %rd3, %rd5;
      |mov.f32 %f0, 0f00000000;
      |mov.f32 %f1, 0f00000000;
      |mov.f32 %f2, 0f00000000;
      |mov.f32 %f3, 0f00000000;
      |mov.f32 %f4, 0f00000000;
      |mov.f32 %f5, 0f00000000;
      |mov.f32 %f6, 0f00000000;
      |mov.f32 %f7, 0f00000000;
      |mma.sync.aligned.m8n8k4.row.col.f32.f16.f16.f32 {%f8,%f9,%f10,%f11,%f12,%f13,%f14,%f15},{%r1,%r2},{%r3,%r4},{%f0,%f1,%f2,%f3,%f4,%f5,%f6,%f7};
      |add.u64 %rd6, %rd0, %rd5;
      |st.global.f32 [%rd6], %f8;
      |st.global.f32 [%rd6+4], %f9;
      |st.global.f32 [%rd6+8], %f10;
      |st.global.f32 [%rd6+12], %f11;
      |st.global.f32 [%rd6+16], %f12;
      |st.global.f32 [%rd6+20], %f13;
      |st.global.f32 [%rd6+24], %f14;
      |st.global.f32 [%rd6+28], %f15;
      |ret;
      |}
      |""".stripMargin

  val hmmaF16AccCuda: String =
    """#include <stdint.h>
      |
      |#if __CUDA_ARCH__ >= 800
      |extern "C" __global__ void mma_f16_acc_alias(uint32_t *out, const uint32_t *a, const uint32_t *b, const uint32_t *c) {
      |  const unsigned lane = threadIdx.x & 31;
      |  uint32_t a0=a[lane*4+0], a1=a[lane*4+1], a2=a[lane*4+2], a3=a[lane*4+3];
      |  uint32_t b0=b[lane*2+0], b1=b[lane*2+1];
      |  uint32_t c0=c[lane*2+0], c1=c[lane*2+1];
      |  asm volatile(
      |    "mma.sync.aligned.m16n8k16.row.col.f16.f16.f16.f16 { %0, %1 }, "
      |    "{ %2, %3, %4, %5 }, "
      |    "{ %6, %7 }, { %0, %1 };"
      |    : "+r"(c0), "+r"(c1)
      |    : "r"(a0), "r"(a1), "r"(a2), "r"(a3), "r"(b0), "r"(b1));
      |  out[lane*2+0]=c0; out[lane*2+1]=c1;
      |}
      |
      |extern "C" __global__ void mma_f16_acc_nonalias(uint32_t *out, const uint32_t *a, const uint32_t *b, const uint32_t *c) {
      |  const unsigned lane = threadIdx.x & 31;
      |  uint32_t a0=a[lane*4+0], a1=a[lane*4+1], a2=a[lane*4+2], a3=a[lane*4+3];
      |  uint32_t b0=b[lane*2+0], b1=b[lane*2+1];
      |  uint32_t c0=c[lane*2+0], c1=c[lane*2+1], d0, d1;
      |  asm volatile(
      |    "mma.sync.aligned.m16n8k16.row.col.f16.f16.f16.f16 { %0, %1 }, "
      |    "{ %2, %3, %4, %5 }, "
      |    "{ %6, %7 }, { %8, %9 };"
      |    : "=r"(d0), "=r"(d1)
      |    : "r"(a0), "r"(a1), "r"(a2), "r"(a3), "r"(b0), "r"(b1), "r"(c0), "r"(c1));
      |  out[lane*2+0]=d0; out[lane*2+1]=d1;
      |  out[64+lane*2+0]=c0; out[64+lane*2+1]=c1;
      |}
      |
      |#endif
      |
      |extern "C" __global__ void mma_f16_acc_alias_1688(uint32_t *out, const uint32_t *a, const uint32_t *b, const uint32_t *c) {
      |  const unsigned lane = threadIdx.x & 31;
      |  uint32_t a0=a[lane*2+0], a1=a[lane*2+1];
      |  uint32_t b0=b[lane];
      |  uint32_t c0=c[lane*2+0], c1=c[lane*2+1];
      |  asm volatile(
      |    "mma.sync.aligned.m16n8k8.row.col.f16.f16.f16.f16 { %0, %1 }, "
      |    "{ %2, %3 }, { %4 }, { %0, %1 };"
      |    : "+r"(c0), "+r"(c1)
      |    : "r"(a0), "r"(a1), "r"(b0));
      |  out[lane*2+0]=c0; out[lane*2+1]=c1;
      |}
      |
      |extern "C" __global__ void mma_f16_acc_nonalias_1688(uint32_t *out, const uint32_t *a, const uint32_t *b, const uint32_t *c) {
      |  const unsigned lane = threadIdx.x & 31;
      |  uint32_t a0=a[lane*2+0], a1=a[lane*2+1];
      |  uint32_t b0=b[lane];
      |  uint32_t c0=c[lane*2+0], c1=c[lane*2+1], d0, d1;
      |  asm volatile(
      |    "mma.sync.aligned.m16n8k8.row.col.f16.f16.f16.f16 { %0, %1 }, "
      |    "{ %2, %3 }, { %4 }, { %5, %6 };"
      |    : "=r"(d0), "=r"(d1)
      |    : "r"(a0), "r"(a1), "r"(b0), "r"(c0), "r"(c1));
      |  out[lane*2+0]=d0; out[lane*2+1]=d1;
      |  out[64+lane*2+0]=c0; out[64+lane*2+1]=c1;
      |}
      |""".stripMargin

  val hmmaF16AccShuffleCuda: String =
    """#include <stdint.h>
      |
      |#if __CUDA_ARCH__ >= 800
      |extern "C" __global__ void mma_f16_acc_alias(uint32_t *out, const uint32_t *a, const uint32_t *b, const uint32_t *c) {
      |  const unsigned lane = threadIdx.x & 31;
      |  uint32_t a0=a[lane*4+0], a1=a[lane*4+1], a2=a[lane*4+2], a3=a[lane*4+3];
      |  uint32_t b0=b[lane*2+0], b1=b[lane*2+1];
      |  uint32_t c0=c[lane*2+0], c1=c[lane*2+1];
      |  asm volatile(
      |    "mma.sync.aligned.m16n8k16.row.col.f16.f16.f16.f16 { %0, %1 }, "
      |    "{ %2, %3, %4, %5 }, "
      |    "{ %6, %7 }, { %0, %1 };"
      |    : "+r"(c0), "+r"(c1)
      |    : "r"(a0), "r"(a1), "r"(a2), "r"(a3), "r"(b0), "r"(b1));
      |  out[lane*2+0]=c0; out[lane*2+1]=c1;
      |}
      |
      |extern "C" __global__ void mma_f16_acc_nonalias(uint32_t *out, const uint32_t *a, const uint32_t *b, const uint32_t *c) {
      |  const unsigned lane = threadIdx.x & 31;
      |  uint32_t a0=a[lane*4+0], a1=a[lane*4+1], a2=a[lane*4+2], a3=a[lane*4+3];
      |  uint32_t b0=b[lane*2+0], b1=b[lane*2+1];
      |  uint32_t c0=c[lane*2+0], c1=c[lane*2+1], d0, d1;
      |  asm volatile(
      |    "mma.sync.aligned.m16n8k16.row.col.f16.f16.f16.f16 { %0, %1 }, "
      |    "{ %2, %3, %4, %5 }, "
      |    "{ %6, %7 }, { %8, %9 };"
      |    : "=r"(d0), "=r"(d1)
      |    : "r"(a0), "r"(a1), "r"(a2), "r"(a3), "r"(b0), "r"(b1), "r"(c0), "r"(c1));
      |  out[lane*2+0]=d0; out[lane*2+1]=d1;
      |  out[64+lane*2+0]=c0; out[64+lane*2+1]=c1;
      |}
      |
      |#endif
      |
      |extern "C" __global__ void mma_f16_acc_alias_1688(uint32_t *out, const uint32_t *a, const uint32_t *b, const uint32_t *c) {
      |  const unsigned lane = threadIdx.x & 31;
      |  uint32_t a0=a[lane*2+0], a1=a[lane*2+1];
      |  uint64_t bp=(uint64_t)b + lane*4; uint32_t blo, bhi, b0; uint64_t mixed;
      |  asm volatile("mov.b64 {%0, %1}, %4; shfl.sync.bfly.b32 %0, %0, 0, 31, -1; shfl.sync.bfly.b32 %1, %1, 0, 31, -1; mov.b64 %2, {%0, %1}; ld.global.u32 %3, [%2];"
      |    : "=r"(blo), "=r"(bhi), "=l"(mixed), "=r"(b0) : "l"(bp));
      |  uint32_t c0=c[lane*2+0], c1=c[lane*2+1];
      |  asm volatile(
      |    "mma.sync.aligned.m16n8k8.row.col.f16.f16.f16.f16 { %0, %1 }, "
      |    "{ %2, %3 }, { %4 }, { %0, %1 };"
      |    : "+r"(c0), "+r"(c1)
      |    : "r"(a0), "r"(a1), "r"(b0));
      |  out[lane*2+0]=c0; out[lane*2+1]=c1;
      |}
      |
      |extern "C" __global__ void mma_f16_acc_nonalias_1688(uint32_t *out, const uint32_t *a, const uint32_t *b, const uint32_t *c) {
      |  const unsigned lane = threadIdx.x & 31;
      |  uint32_t a0=a[lane*2+0], a1=a[lane*2+1];
      |  uint64_t bp=(uint64_t)b + lane*4; uint32_t blo, bhi, b0; uint64_t mixed;
      |  asm volatile("mov.b64 {%0, %1}, %4; shfl.sync.bfly.b32 %0, %0, 0, 31, -1; shfl.sync.bfly.b32 %1, %1, 0, 31, -1; mov.b64 %2, {%0, %1}; ld.global.u32 %3, [%2];"
      |    : "=r"(blo), "=r"(bhi), "=l"(mixed), "=r"(b0) : "l"(bp));
      |  uint32_t c0=c[lane*2+0], c1=c[lane*2+1], d0, d1;
      |  asm volatile(
      |    "mma.sync.aligned.m16n8k8.row.col.f16.f16.f16.f16 { %0, %1 }, "
      |    "{ %2, %3 }, { %4 }, { %5, %6 };"
      |    : "=r"(d0), "=r"(d1)
      |    : "r"(a0), "r"(a1), "r"(b0), "r"(c0), "r"(c1));
      |  out[lane*2+0]=d0; out[lane*2+1]=d1;
      |  out[64+lane*2+0]=c0; out[64+lane*2+1]=c1;
      |}
      |""".stripMargin

  val hmmaF16AccSm75Ptx: String =
    """//
      |// Generated by NVIDIA NVVM Compiler
      |//
      |// Compiler Build ID: CL-38244171
      |// Cuda compilation tools, release 13.3, V13.3.73
      |// Based on NVVM 7.0.1
      |//
      |
      |.version 9.3
      |.target sm_75
      |.address_size 64
      |
      |	// .globl	mma_f16_acc_alias_1688
      |
      |.visible .entry mma_f16_acc_alias_1688(
      |	.param .u64 mma_f16_acc_alias_1688_param_0,
      |	.param .u64 mma_f16_acc_alias_1688_param_1,
      |	.param .u64 mma_f16_acc_alias_1688_param_2,
      |	.param .u64 mma_f16_acc_alias_1688_param_3
      |)
      |{
      |	.reg .b32 	%r<12>;
      |	.reg .b64 	%rd<16>;
      |
      |
      |	ld.param.u64 	%rd1, [mma_f16_acc_alias_1688_param_0];
      |	ld.param.u64 	%rd2, [mma_f16_acc_alias_1688_param_1];
      |	ld.param.u64 	%rd3, [mma_f16_acc_alias_1688_param_2];
      |	ld.param.u64 	%rd4, [mma_f16_acc_alias_1688_param_3];
      |	cvta.to.global.u64 	%rd5, %rd1;
      |	cvta.to.global.u64 	%rd6, %rd4;
      |	cvta.to.global.u64 	%rd7, %rd3;
      |	cvta.to.global.u64 	%rd8, %rd2;
      |	mov.u32 	%r8, %tid.x;
      |	shl.b32 	%r9, %r8, 1;
      |	and.b32  	%r10, %r9, 62;
      |	mul.wide.u32 	%rd9, %r10, 4;
      |	add.s64 	%rd10, %rd8, %rd9;
      |	ld.global.u32 	%r3, [%rd10];
      |	ld.global.u32 	%r4, [%rd10+4];
      |	shl.b32 	%r11, %r8, 2;
      |	cvt.u64.u32 	%rd11, %r11;
      |	and.b64  	%rd12, %rd11, 124;
      |	add.s64 	%rd13, %rd7, %rd12;
      |	ld.global.u32 	%r5, [%rd13];
      |	add.s64 	%rd14, %rd6, %rd9;
      |	ld.global.u32 	%r1, [%rd14];
      |	ld.global.u32 	%r2, [%rd14+4];
      |	// begin inline asm
      |	mma.sync.aligned.m16n8k8.row.col.f16.f16.f16.f16 { %r1, %r2 }, { %r3, %r4 }, { %r5 }, { %r1, %r2 };
      |	// end inline asm
      |	add.s64 	%rd15, %rd5, %rd9;
      |	st.global.u32 	[%rd15], %r1;
      |	st.global.u32 	[%rd15+4], %r2;
      |	ret;
      |
      |}
      |	// .globl	mma_f16_acc_nonalias_1688
      |.visible .entry mma_f16_acc_nonalias_1688(
      |	.param .u64 mma_f16_acc_nonalias_1688_param_0,
      |	.param .u64 mma_f16_acc_nonalias_1688_param_1,
      |	.param .u64 mma_f16_acc_nonalias_1688_param_2,
      |	.param .u64 mma_f16_acc_nonalias_1688_param_3
      |)
      |{
      |	.reg .b32 	%r<12>;
      |	.reg .b64 	%rd<16>;
      |
      |
      |	ld.param.u64 	%rd1, [mma_f16_acc_nonalias_1688_param_0];
      |	ld.param.u64 	%rd2, [mma_f16_acc_nonalias_1688_param_1];
      |	ld.param.u64 	%rd3, [mma_f16_acc_nonalias_1688_param_2];
      |	ld.param.u64 	%rd4, [mma_f16_acc_nonalias_1688_param_3];
      |	cvta.to.global.u64 	%rd5, %rd1;
      |	cvta.to.global.u64 	%rd6, %rd4;
      |	cvta.to.global.u64 	%rd7, %rd3;
      |	cvta.to.global.u64 	%rd8, %rd2;
      |	mov.u32 	%r8, %tid.x;
      |	shl.b32 	%r9, %r8, 1;
      |	and.b32  	%r10, %r9, 62;
      |	mul.wide.u32 	%rd9, %r10, 4;
      |	add.s64 	%rd10, %rd8, %rd9;
      |	ld.global.u32 	%r3, [%rd10];
      |	ld.global.u32 	%r4, [%rd10+4];
      |	shl.b32 	%r11, %r8, 2;
      |	cvt.u64.u32 	%rd11, %r11;
      |	and.b64  	%rd12, %rd11, 124;
      |	add.s64 	%rd13, %rd7, %rd12;
      |	ld.global.u32 	%r5, [%rd13];
      |	add.s64 	%rd14, %rd6, %rd9;
      |	ld.global.u32 	%r6, [%rd14];
      |	ld.global.u32 	%r7, [%rd14+4];
      |	// begin inline asm
      |	mma.sync.aligned.m16n8k8.row.col.f16.f16.f16.f16 { %r1, %r2 }, { %r3, %r4 }, { %r5 }, { %r6, %r7 };
      |	// end inline asm
      |	add.s64 	%rd15, %rd5, %rd9;
      |	st.global.u32 	[%rd15], %r1;
      |	st.global.u32 	[%rd15+4], %r2;
      |	st.global.u32 	[%rd15+256], %r6;
      |	st.global.u32 	[%rd15+260], %r7;
      |	ret;
      |
      |}
      |""".stripMargin

  val ldsmHmma1688TransCuda: String =
    """#include <cuda_fp16.h>
      |#include <stdint.h>
      |
      |extern "C" __global__ void ldmatrix_trans_mma_f16(uint32_t *out) {
      |  __shared__ __align__(16) __half a[16 * 8];
      |  __shared__ __align__(16) __half b[8 * 8];
      |  const unsigned lane = threadIdx.x;
      |  const unsigned group = lane / 4;
      |  const unsigned thread = lane % 4;
      |  #pragma unroll
      |  for (unsigned i = 0; i < 4; ++i) {
      |    const unsigned row = group + (i / 2) * 8;
      |    const unsigned col = thread * 2 + (i % 2);
      |    a[row * 8 + col] = __float2half(1.0f + ((row * 2 + col * 5) % 3));
      |  }
      |  #pragma unroll
      |  for (unsigned i = 0; i < 2; ++i) {
      |    const unsigned row = group;
      |    const unsigned col = thread * 2 + i;
      |    if (row < 8) b[row * 8 + col] = __float2half(1.0f + ((row * 5 + col * 7) % 3));
      |  }
      |  __syncthreads();
      |  const unsigned row8 = lane < 8 ? lane : (lane < 16 ? lane : 0);
      |  const uintptr_t ap = __cvta_generic_to_shared(a + row8 * 8);
      |  const uintptr_t bp = __cvta_generic_to_shared(b + (lane < 8 ? lane * 8 : 0));
      |  uint32_t a0, a1, b0, c0 = 0, c1 = 0;
      |  asm volatile(
      |      "ldmatrix.sync.aligned.m8n8.x2.shared.b16 {%0,%1},[%5];\n"
      |      "ldmatrix.sync.aligned.m8n8.x1.trans.shared.b16 {%2},[%6];\n"
      |      "mma.sync.aligned.m16n8k8.row.col.f16.f16.f16.f16 {%3,%4},{%0,%1},{%2},{%3,%4};\n"
      |      : "=r"(a0), "=r"(a1), "=r"(b0), "+r"(c0), "+r"(c1) : "l"(ap), "l"(bp));
      |  out[lane * 2] = c0;
      |  out[lane * 2 + 1] = c1;
      |}
      |""".stripMargin

  val ldsmPtx: String =
    """.version 8.0
      |.target sm_89
      |.address_size 64
      |.visible .entry matrix_load(.param .u64 out, .param .u32 bias) {
      |.shared .align 16 .b16 tile[264];
      |.reg .u32 %r<24>;
      |.reg .u64 %rd<3>;
      |.reg .b16 %h;
      |ld.param.u64 %rd0,[out];
      |ld.param.u32 %r0,[bias];
      |mov.u32 %r1,%tid.x;
      |mov.u32 %r2,tile;
      |mad.lo.u32 %r3,%r1,16,%r2;
      |add.u32 %r3,%r3,%r0;
      |mul.lo.u32 %r4,%r1,8;
      |add.u32 %r5,%r4,1;
      |cvt.u16.u32 %h,%r5;
      |st.shared.b16 [%r3+0],%h;
      |add.u32 %r5,%r4,2;
      |cvt.u16.u32 %h,%r5;
      |st.shared.b16 [%r3+2],%h;
      |add.u32 %r5,%r4,3;
      |cvt.u16.u32 %h,%r5;
      |st.shared.b16 [%r3+4],%h;
      |add.u32 %r5,%r4,4;
      |cvt.u16.u32 %h,%r5;
      |st.shared.b16 [%r3+6],%h;
      |add.u32 %r5,%r4,5;
      |cvt.u16.u32 %h,%r5;
      |st.shared.b16 [%r3+8],%h;
      |add.u32 %r5,%r4,6;
      |cvt.u16.u32 %h,%r5;
      |st.shared.b16 [%r3+10],%h;
      |add.u32 %r5,%r4,7;
      |cvt.u16.u32 %h,%r5;
      |st.shared.b16 [%r3+12],%h;
      |add.u32 %r5,%r4,8;
      |cvt.u16.u32 %h,%r5;
      |st.shared.b16 [%r3+14],%h;
      |bar.sync 0;
      |ldmatrix.sync.aligned.m8n8.x1.shared.b16 {%r6},[%r3];
      |ldmatrix.sync.aligned.m8n8.x2.shared.b16 {%r7,%r8},[%r3];
      |ldmatrix.sync.aligned.m8n8.x4.shared.b16 {%r9,%r10,%r11,%r12},[%r3];
      |ldmatrix.sync.aligned.m8n8.x4.trans.shared.b16 {%r13,%r14,%r15,%r16},[%r3];
      |mul.wide.u32 %rd1,%r1,44;
      |add.u64 %rd1,%rd1,%rd0;
      |st.global.u32 [%rd1+0],%r6;
      |st.global.u32 [%rd1+4],%r7;
      |st.global.u32 [%rd1+8],%r8;
      |st.global.u32 [%rd1+12],%r9;
      |st.global.u32 [%rd1+16],%r10;
      |st.global.u32 [%rd1+20],%r11;
      |st.global.u32 [%rd1+24],%r12;
      |st.global.u32 [%rd1+28],%r13;
      |st.global.u32 [%rd1+32],%r14;
      |st.global.u32 [%rd1+36],%r15;
      |st.global.u32 [%rd1+40],%r16;
      |ret;
      |}
      |""".stripMargin

  val tensorCuda: String =
    """#include <stdexcept>
      |#include <string>
      |#include <vector>
      |
      |#include <mma.h>
      |
      |#include <catch2/catch_test_macros.hpp>
      |#include <cuda_fp16.h>
      |#include <cuda_runtime.h>
      |
      |#include "test_support.hpp"
      |
      |namespace cuda_tensor {
      |
      |void check_tensor(cudaError_t result) {
      |  if (result != cudaSuccess) throw std::runtime_error(std::string("CUDA tensor test: ") + cudaGetErrorString(result));
      |}
      |
      |__global__ void tensor_gemm(const half *a, const half *b, float *c) {
      |#if __CUDA_ARCH__ >= 700
      |  using namespace nvcuda;
      |  const unsigned tile = blockIdx.x * 2 + threadIdx.x / 32;
      |  wmma::fragment<wmma::matrix_a, 16, 16, 16, half, wmma::row_major> af;
      |  wmma::fragment<wmma::matrix_b, 16, 16, 16, half, wmma::col_major> bf;
      |  wmma::fragment<wmma::accumulator, 16, 16, 16, float> cf;
      |  wmma::load_matrix_sync(af, a + tile * 512, 32);
      |  wmma::load_matrix_sync(bf, b + tile * 512, 32);
      |  wmma::load_matrix_sync(cf, c + tile * 512, 32, wmma::mem_row_major);
      |  wmma::mma_sync(cf, af, bf, cf);
      |  for (int i = 0; i < cf.num_elements; ++i)
      |    cf.x[i] *= 2.0f;
      |  wmma::store_matrix_sync(c + tile * 512, cf, 32, wmma::mem_row_major);
      |#endif
      |}
      |
      |TEST_CASE("runtime/cuda/tensor-wmma-gemm", "[unified][runtime][tensor][compact]") {
      |  cudaDeviceProp properties{};
      |  check_tensor(cudaGetDeviceProperties(&properties, 0));
      |  if (properties.major < 7) SKIP("WMMA requires compute capability 7.0");
      |
      |  constexpr int tiles = 4;
      |  constexpr int stride = 32;
      |  constexpr int tile_size = 16 * stride;
      |  std::vector<half> a(tiles * tile_size, __float2half(0.0f));
      |  std::vector<half> b(tiles * tile_size, __float2half(0.0f));
      |  std::vector<float> c(tiles * tile_size, -1234.0f);
      |  for (int tile = 0; tile < tiles; ++tile)
      |    for (int row = 0; row < 16; ++row)
      |      for (int col = 0; col < 16; ++col) {
      |        a[tile * tile_size + row * stride + col] = __float2half(static_cast<float>((row + 3 * col + tile) % 7 - 3));
      |        b[tile * tile_size + col * stride + row] = __float2half(static_cast<float>((2 * row + col) % 5 - 2));
      |        c[tile * tile_size + row * stride + col] = static_cast<float>(row - col);
      |      }
      |  auto expected = c;
      |  for (int tile = 0; tile < tiles; ++tile)
      |    for (int row = 0; row < 16; ++row)
      |      for (int col = 0; col < 16; ++col) {
      |        auto &result = expected[tile * tile_size + row * stride + col];
      |        for (int k = 0; k < 16; ++k)
      |          result += __half2float(a[tile * tile_size + row * stride + k]) * __half2float(b[tile * tile_size + col * stride + k]);
      |        result *= 2.0f;
      |      }
      |
      |  unified::test_runtime::Cleanup cleanup;
      |  half *da = nullptr;
      |  half *db = nullptr;
      |  float *dc = nullptr;
      |  check_tensor(cudaMalloc(reinterpret_cast<void **>(&da), a.size() * sizeof(half)));
      |  cleanup.add([=] { cudaFree(da); });
      |  check_tensor(cudaMalloc(reinterpret_cast<void **>(&db), b.size() * sizeof(half)));
      |  cleanup.add([=] { cudaFree(db); });
      |  check_tensor(cudaMalloc(reinterpret_cast<void **>(&dc), c.size() * sizeof(float)));
      |  cleanup.add([=] { cudaFree(dc); });
      |  check_tensor(cudaMemcpy(da, a.data(), a.size() * sizeof(half), cudaMemcpyHostToDevice));
      |  check_tensor(cudaMemcpy(db, b.data(), b.size() * sizeof(half), cudaMemcpyHostToDevice));
      |  check_tensor(cudaMemcpy(dc, c.data(), c.size() * sizeof(float), cudaMemcpyHostToDevice));
      |  tensor_gemm<<<2, 64>>>(da, db, dc);
      |  check_tensor(cudaGetLastError());
      |  check_tensor(cudaDeviceSynchronize());
      |  check_tensor(cudaMemcpy(c.data(), dc, c.size() * sizeof(float), cudaMemcpyDeviceToHost));
      |  REQUIRE(c == expected);
      |}
      |
      |} // namespace cuda_tensor
      |""".stripMargin

  val listings: Map[String, String] = Map(
    "dmmaSm80" ->
      """//--------------------- .text.double_mma          --------------------------
        |	.section	.text.double_mma,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=16"
        |	.align	128
        |        .global         double_mma
        |        .type           double_mma,@function
        |        .size           double_mma,(.L_x_1 - double_mma)
        |        .other          double_mma,@"STO_CUDA_ENTRY STV_DEFAULT"
        |double_mma:
        |.text.double_mma:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R13, SR_LANEID ;
        |        /*0020*/                   UMOV UR4, 0x0 ;
        |        /*0030*/                   IMAD.MOV.U32 R8, RZ, RZ, 0x0 ;
        |        /*0040*/                   UMOV UR5, 0x401c0000 ;
        |        /*0050*/                   MOV R9, 0x40140000 ;
        |        /*0060*/                   DADD R10, -RZ, -UR4 ;
        |        /*0070*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0080*/                   IADD3 R0, R13.reuse, 0x1, RZ ;
        |        /*0090*/                   LEA R12, R13, 0x1, 0x1 ;
        |        /*00a0*/                   I2F.F64.U32 R2, R0 ;
        |        /*00b0*/                   I2F.F64.U32 R4, R12 ;
        |        /*00c0*/                   DMMA.884 R4, R2, R4, R8 ;
        |        /*00d0*/                   MOV R2, 0x10 ;
        |        /*00e0*/                   IMAD.WIDE.U32 R2, R13, R2, c[0x0][0x160] ;
        |        /*00f0*/                   @!UPT UIADD3 URZ, URZ, URZ, URZ ;
        |        /*0100*/                   STG.E.64 [R2.64], R4 ;
        |        /*0110*/                   STG.E.64 [R2.64+0x8], R6 ;
        |        /*0120*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hmma1688F16AccAliasSm75" ->
      """//--------------------- .text.mma_f16_acc_alias_1688 --------------------------
        |	.section	.text.mma_f16_acc_alias_1688,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         mma_f16_acc_alias_1688
        |        .type           mma_f16_acc_alias_1688,@function
        |        .size           mma_f16_acc_alias_1688,(.L_x_1 - mma_f16_acc_alias_1688)
        |        .other          mma_f16_acc_alias_1688,@"STO_CUDA_ENTRY STV_DEFAULT"
        |mma_f16_acc_alias_1688:
        |.text.mma_f16_acc_alias_1688:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R6, SR_TID.X ;
        |        /*0020*/                   MOV R9, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x170] ;
        |        /*0040*/                   SHF.L.U32 R0, R6.reuse, 0x1, RZ ;
        |        /*0050*/                   SHF.L.U32 R6, R6, 0x2, RZ ;
        |        /*0060*/                   LOP3.LUT R0, R0, 0x3e, RZ, 0xc0, !PT ;
        |        /*0070*/                   LOP3.LUT R8, R6, 0x7c, RZ, 0xc0, !PT ;
        |        /*0080*/                   IMAD.WIDE.U32 R2, R0, R9, c[0x0][0x168] ;
        |        /*0090*/                   IMAD.WIDE.U32 R4, R0.reuse, R9.reuse, c[0x0][0x178] ;
        |        /*00a0*/                   LDG.E.SYS R8, [R8.U32+UR4] ;
        |        /*00b0*/                   LDG.E.SYS R6, [R2] ;
        |        /*00c0*/                   LDG.E.SYS R7, [R2+0x4] ;
        |        /*00d0*/                   LDG.E.SYS R10, [R4] ;
        |        /*00e0*/                   LDG.E.SYS R11, [R4+0x4] ;
        |        /*00f0*/                   HMMA.1688.F16 R10, R6, R8, R10 ;
        |        /*0100*/                   IMAD.WIDE.U32 R6, R0, R9, c[0x0][0x160] ;
        |        /*0110*/                   STG.E.SYS [R6], R10 ;
        |        /*0120*/                   STG.E.SYS [R6+0x4], R11 ;
        |        /*0130*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hmma1688F16AccAliasSm89" ->
      """//--------------------- .text.mma_f16_acc_alias_1688 --------------------------
        |	.section	.text.mma_f16_acc_alias_1688,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=16"
        |	.align	128
        |        .global         mma_f16_acc_alias_1688
        |        .type           mma_f16_acc_alias_1688,@function
        |        .size           mma_f16_acc_alias_1688,(.L_x_1 - mma_f16_acc_alias_1688)
        |        .other          mma_f16_acc_alias_1688,@"STO_CUDA_ENTRY STV_DEFAULT"
        |mma_f16_acc_alias_1688:
        |.text.mma_f16_acc_alias_1688:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   MOV R13, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   SHF.L.U32 R2, R0.reuse, 0x2, RZ ;
        |        /*0050*/                   IMAD.SHL.U32 R0, R0, 0x2, RZ ;
        |        /*0060*/                   LOP3.LUT R2, R2, 0x7c, RZ, 0xc0, !PT ;
        |        /*0070*/                   LOP3.LUT R0, R0, 0x3e, RZ, 0xc0, !PT ;
        |        /*0080*/                   IADD3 R8, P0, R2, c[0x0][0x170], RZ ;
        |        /*0090*/                   IMAD.WIDE.U32 R2, R0, R13, c[0x0][0x168] ;
        |        /*00a0*/                   IMAD.X R9, RZ, RZ, c[0x0][0x174], P0 ;
        |        /*00b0*/                   LDG.E R6, [R2.64] ;
        |        /*00c0*/                   IMAD.WIDE.U32 R4, R0, R13, c[0x0][0x178] ;
        |        /*00d0*/                   LDG.E R8, [R8.64] ;
        |        /*00e0*/                   LDG.E R7, [R2.64+0x4] ;
        |        /*00f0*/                   LDG.E R10, [R4.64] ;
        |        /*0100*/                   LDG.E R11, [R4.64+0x4] ;
        |        /*0110*/                   HMMA.1688.F16 R10, R6, R8, R10 ;
        |        /*0120*/                   IMAD.WIDE.U32 R6, R0, R13, c[0x0][0x160] ;
        |        /*0130*/                   STG.E [R6.64], R10 ;
        |        /*0140*/                   STG.E [R6.64+0x4], R11 ;
        |        /*0150*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hmma1688F16AccNonaliasSm75" ->
      """//--------------------- .text.mma_f16_acc_nonalias_1688 --------------------------
        |	.section	.text.mma_f16_acc_nonalias_1688,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=16"
        |	.align	128
        |        .global         mma_f16_acc_nonalias_1688
        |        .type           mma_f16_acc_nonalias_1688,@function
        |        .size           mma_f16_acc_nonalias_1688,(.L_x_1 - mma_f16_acc_nonalias_1688)
        |        .other          mma_f16_acc_nonalias_1688,@"STO_CUDA_ENTRY STV_DEFAULT"
        |mma_f16_acc_nonalias_1688:
        |.text.mma_f16_acc_nonalias_1688:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R6, SR_TID.X ;
        |        /*0020*/                   MOV R13, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x170] ;
        |        /*0040*/                   SHF.L.U32 R0, R6.reuse, 0x1, RZ ;
        |        /*0050*/                   SHF.L.U32 R6, R6, 0x2, RZ ;
        |        /*0060*/                   LOP3.LUT R0, R0, 0x3e, RZ, 0xc0, !PT ;
        |        /*0070*/                   LOP3.LUT R12, R6, 0x7c, RZ, 0xc0, !PT ;
        |        /*0080*/                   IMAD.WIDE.U32 R2, R0, R13, c[0x0][0x168] ;
        |        /*0090*/                   IMAD.WIDE.U32 R4, R0.reuse, R13.reuse, c[0x0][0x178] ;
        |        /*00a0*/                   LDG.E.SYS R8, [R12.U32+UR4] ;
        |        /*00b0*/                   LDG.E.SYS R6, [R2] ;
        |        /*00c0*/                   LDG.E.SYS R7, [R2+0x4] ;
        |        /*00d0*/                   LDG.E.SYS R10, [R4] ;
        |        /*00e0*/                   LDG.E.SYS R11, [R4+0x4] ;
        |        /*00f0*/                   HMMA.1688.F16 R8, R6, R8, R10 ;
        |        /*0100*/                   IMAD.WIDE.U32 R6, R0, R13, c[0x0][0x160] ;
        |        /*0110*/                   STG.E.SYS [R6+0x100], R10 ;
        |        /*0120*/                   STG.E.SYS [R6+0x104], R11 ;
        |        /*0130*/                   STG.E.SYS [R6], R8 ;
        |        /*0140*/                   STG.E.SYS [R6+0x4], R9 ;
        |        /*0150*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hmma1688F16AccNonaliasSm89" ->
      """//--------------------- .text.mma_f16_acc_nonalias_1688 --------------------------
        |	.section	.text.mma_f16_acc_nonalias_1688,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=18"
        |	.align	128
        |        .global         mma_f16_acc_nonalias_1688
        |        .type           mma_f16_acc_nonalias_1688,@function
        |        .size           mma_f16_acc_nonalias_1688,(.L_x_1 - mma_f16_acc_nonalias_1688)
        |        .other          mma_f16_acc_nonalias_1688,@"STO_CUDA_ENTRY STV_DEFAULT"
        |mma_f16_acc_nonalias_1688:
        |.text.mma_f16_acc_nonalias_1688:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   MOV R15, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   SHF.L.U32 R2, R0.reuse, 0x2, RZ ;
        |        /*0050*/                   IMAD.SHL.U32 R0, R0, 0x2, RZ ;
        |        /*0060*/                   LOP3.LUT R2, R2, 0x7c, RZ, 0xc0, !PT ;
        |        /*0070*/                   LOP3.LUT R0, R0, 0x3e, RZ, 0xc0, !PT ;
        |        /*0080*/                   IADD3 R8, P0, R2, c[0x0][0x170], RZ ;
        |        /*0090*/                   IMAD.WIDE.U32 R2, R0, R15, c[0x0][0x168] ;
        |        /*00a0*/                   IMAD.X R9, RZ, RZ, c[0x0][0x174], P0 ;
        |        /*00b0*/                   LDG.E R6, [R2.64] ;
        |        /*00c0*/                   IMAD.WIDE.U32 R4, R0, R15, c[0x0][0x178] ;
        |        /*00d0*/                   LDG.E R8, [R8.64] ;
        |        /*00e0*/                   LDG.E R7, [R2.64+0x4] ;
        |        /*00f0*/                   LDG.E R10, [R4.64] ;
        |        /*0100*/                   LDG.E R11, [R4.64+0x4] ;
        |        /*0110*/                   HMMA.1688.F16 R12, R6, R8, R10 ;
        |        /*0120*/                   IMAD.WIDE.U32 R6, R0, R15, c[0x0][0x160] ;
        |        /*0130*/                   STG.E [R6.64+0x100], R10 ;
        |        /*0140*/                   STG.E [R6.64+0x104], R11 ;
        |        /*0150*/                   STG.E [R6.64], R12 ;
        |        /*0160*/                   STG.E [R6.64+0x4], R13 ;
        |        /*0170*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hmma1688F16AccShuffleAliasSm75" ->
      """//--------------------- .text.mma_f16_acc_alias_1688 --------------------------
        |	.section	.text.mma_f16_acc_alias_1688,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=16"
        |	.align	128
        |        .global         mma_f16_acc_alias_1688
        |        .type           mma_f16_acc_alias_1688,@function
        |        .size           mma_f16_acc_alias_1688,(.L_x_1 - mma_f16_acc_alias_1688)
        |        .other          mma_f16_acc_alias_1688,@"STO_CUDA_ENTRY STV_DEFAULT"
        |mma_f16_acc_alias_1688:
        |.text.mma_f16_acc_alias_1688:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0020*/                   IMAD.MOV.U32 R13, RZ, RZ, 0x4 ;
        |        /*0030*/                   SHF.L.U32 R0, R2, 0x2, RZ ;
        |        /*0040*/                   LOP3.LUT R0, R0, 0x7c, RZ, 0xc0, !PT ;
        |        /*0050*/                   IADD3 R11, P0, R0, c[0x0][0x170], RZ ;
        |        /*0060*/                   SHF.L.U32 R0, R2, 0x1, RZ ;
        |        /*0070*/                   IMAD.X R5, RZ, RZ, c[0x0][0x174], P0 ;
        |        /*0080*/                   LOP3.LUT R0, R0, 0x3e, RZ, 0xc0, !PT ;
        |        /*0090*/                   SHFL.BFLY PT, R4, R11, RZ, 0x1f ;
        |        /*00a0*/                   IMAD.WIDE.U32 R2, R0.reuse, R13.reuse, c[0x0][0x168] ;
        |        /*00b0*/                   SHFL.BFLY PT, R5, R5, RZ, 0x1f ;
        |        /*00c0*/                   IMAD.WIDE.U32 R6, R0, R13, c[0x0][0x178] ;
        |        /*00d0*/                   LDG.E.SYS R8, [R2] ;
        |        /*00e0*/                   LDG.E.SYS R9, [R2+0x4] ;
        |        /*00f0*/                   LDG.E.SYS R10, [R6] ;
        |        /*0100*/                   LDG.E.SYS R11, [R6+0x4] ;
        |        /*0110*/                   LDG.E.SYS R4, [R4] ;
        |        /*0120*/                   HMMA.1688.F16 R10, R8, R4, R10 ;
        |        /*0130*/                   IMAD.WIDE.U32 R8, R0, R13, c[0x0][0x160] ;
        |        /*0140*/                   STG.E.SYS [R8], R10 ;
        |        /*0150*/                   STG.E.SYS [R8+0x4], R11 ;
        |        /*0160*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hmma1688F16AccShuffleNonaliasSm75" ->
      """//--------------------- .text.mma_f16_acc_nonalias_1688 --------------------------
        |	.section	.text.mma_f16_acc_nonalias_1688,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=18"
        |	.align	128
        |        .global         mma_f16_acc_nonalias_1688
        |        .type           mma_f16_acc_nonalias_1688,@function
        |        .size           mma_f16_acc_nonalias_1688,(.L_x_1 - mma_f16_acc_nonalias_1688)
        |        .other          mma_f16_acc_nonalias_1688,@"STO_CUDA_ENTRY STV_DEFAULT"
        |mma_f16_acc_nonalias_1688:
        |.text.mma_f16_acc_nonalias_1688:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0020*/                   IMAD.MOV.U32 R15, RZ, RZ, 0x4 ;
        |        /*0030*/                   SHF.L.U32 R0, R2, 0x2, RZ ;
        |        /*0040*/                   LOP3.LUT R0, R0, 0x7c, RZ, 0xc0, !PT ;
        |        /*0050*/                   IADD3 R11, P0, R0, c[0x0][0x170], RZ ;
        |        /*0060*/                   SHF.L.U32 R0, R2, 0x1, RZ ;
        |        /*0070*/                   IMAD.X R5, RZ, RZ, c[0x0][0x174], P0 ;
        |        /*0080*/                   LOP3.LUT R0, R0, 0x3e, RZ, 0xc0, !PT ;
        |        /*0090*/                   SHFL.BFLY PT, R4, R11, RZ, 0x1f ;
        |        /*00a0*/                   IMAD.WIDE.U32 R2, R0.reuse, R15.reuse, c[0x0][0x168] ;
        |        /*00b0*/                   SHFL.BFLY PT, R5, R5, RZ, 0x1f ;
        |        /*00c0*/                   IMAD.WIDE.U32 R6, R0, R15, c[0x0][0x178] ;
        |        /*00d0*/                   LDG.E.SYS R8, [R2] ;
        |        /*00e0*/                   LDG.E.SYS R9, [R2+0x4] ;
        |        /*00f0*/                   LDG.E.SYS R10, [R6] ;
        |        /*0100*/                   LDG.E.SYS R11, [R6+0x4] ;
        |        /*0110*/                   LDG.E.SYS R4, [R4] ;
        |        /*0120*/                   HMMA.1688.F16 R12, R8, R4, R10 ;
        |        /*0130*/                   IMAD.WIDE.U32 R8, R0, R15, c[0x0][0x160] ;
        |        /*0140*/                   STG.E.SYS [R8+0x100], R10 ;
        |        /*0150*/                   STG.E.SYS [R8+0x104], R11 ;
        |        /*0160*/                   STG.E.SYS [R8], R12 ;
        |        /*0170*/                   STG.E.SYS [R8+0x4], R13 ;
        |        /*0180*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hmma884F16ColColSm70" ->
      """//--------------------- .text.hmma884_f16_col_col_probe --------------------------
        |	.section	.text.hmma884_f16_col_col_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=20"
        |	.align	128
        |        .global         hmma884_f16_col_col_probe
        |        .type           hmma884_f16_col_col_probe,@function
        |        .size           hmma884_f16_col_col_probe,(.L_x_1 - hmma884_f16_col_col_probe)
        |        .other          hmma884_f16_col_col_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmma884_f16_col_col_probe:
        |.text.hmma884_f16_col_col_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                          /* 0x00000a0000017a02 */
        |                                                                                                   /* 0x000fc40000000f00 */
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                                   /* 0x000000fffffff389 */
        |                                                                                                   /* 0x000fe200000e00ff */
        |        /*0020*/                   S2R R0, SR_TID.X ;                                              /* 0x0000000000007919 */
        |                                                                                                   /* 0x000e220000002100 */
        |        /*0030*/                   MOV R5, 0x8 ;                                                   /* 0x0000000800057802 */
        |                                                                                                   /* 0x000fe40000000f00 */
        |        /*0040*/                   MOV R17, 0x10 ;                                                 /* 0x0000001000117802 */
        |                                                                                                   /* 0x000fc60000000f00 */
        |        /*0050*/                   IMAD.WIDE.U32 R2, R0, R5, c[0x0][0x168] ;                       /* 0x00005a0000027625 */
        |                                                                                                   /* 0x001fc800078e0005 */
        |        /*0060*/                   IMAD.WIDE.U32 R4, R0, R5, c[0x0][0x170] ;                       /* 0x00005c0000047625 */
        |                                                                                                   /* 0x000fc800078e0005 */
        |        /*0070*/                   IMAD.WIDE.U32 R6, R0.reuse, R17.reuse, c[0x0][0x178] ;          /* 0x00005e0000067625 */
        |                                                                                                   /* 0x0c0fe200078e0011 */
        |        /*0080*/                   LDG.E.SYS R8, [R2] ;                                            /* 0x0000000002087381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0090*/                   LDG.E.SYS R9, [R2+0x4] ;                                        /* 0x0000040002097381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00a0*/                   LDG.E.SYS R10, [R4] ;                                           /* 0x00000000040a7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00b0*/                   LDG.E.SYS R11, [R4+0x4] ;                                       /* 0x00000400040b7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00c0*/                   LDG.E.SYS R12, [R6] ;                                           /* 0x00000000060c7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00d0*/                   LDG.E.SYS R13, [R6+0x4] ;                                       /* 0x00000400060d7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00e0*/                   LDG.E.SYS R14, [R6+0x8] ;                                       /* 0x00000800060e7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00f0*/                   LDG.E.SYS R15, [R6+0xc] ;                                       /* 0x00000c00060f7381 */
        |                                                                                                   /* 0x000ea400001ee900 */
        |        /*0100*/                   HMMA.884.F16.F16.STEP0 R12, R8.reuse.COL, R10.reuse.COL, R12 ;  /* 0x0000000a080c7236 */
        |                                                                                                   /* 0x0c4f68000000060c */
        |        /*0110*/                   HMMA.884.F16.F16.STEP1 R14, R8.COL, R10.COL, R14 ;              /* 0x0000000a080e7236 */
        |                                                                                                   /* 0x000b64000000860e */
        |        /*0120*/                   IMAD.WIDE.U32 R8, R0, R17, c[0x0][0x160] ;                      /* 0x0000580000087625 */
        |                                                                                                   /* 0x020fd000078e0011 */
        |        /*0130*/                   STG.E.SYS [R8], R12 ;                                           /* 0x0000000c08007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0140*/                   STG.E.SYS [R8+0x4], R13 ;                                       /* 0x0000040d08007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0150*/                   STG.E.SYS [R8+0x8], R14 ;                                       /* 0x0000080e08007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0160*/                   STG.E.SYS [R8+0xc], R15 ;                                       /* 0x00000c0f08007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*0170*/                   EXIT ;                                                          /* 0x000000000000794d */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hmma884F16ColRowSm70" ->
      """//--------------------- .text.hmma884_f16_col_row_probe --------------------------
        |	.section	.text.hmma884_f16_col_row_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=20"
        |	.align	128
        |        .global         hmma884_f16_col_row_probe
        |        .type           hmma884_f16_col_row_probe,@function
        |        .size           hmma884_f16_col_row_probe,(.L_x_1 - hmma884_f16_col_row_probe)
        |        .other          hmma884_f16_col_row_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmma884_f16_col_row_probe:
        |.text.hmma884_f16_col_row_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                          /* 0x00000a0000017a02 */
        |                                                                                                   /* 0x000fc40000000f00 */
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                                   /* 0x000000fffffff389 */
        |                                                                                                   /* 0x000fe200000e00ff */
        |        /*0020*/                   S2R R0, SR_TID.X ;                                              /* 0x0000000000007919 */
        |                                                                                                   /* 0x000e220000002100 */
        |        /*0030*/                   MOV R5, 0x8 ;                                                   /* 0x0000000800057802 */
        |                                                                                                   /* 0x000fe40000000f00 */
        |        /*0040*/                   MOV R17, 0x10 ;                                                 /* 0x0000001000117802 */
        |                                                                                                   /* 0x000fc60000000f00 */
        |        /*0050*/                   IMAD.WIDE.U32 R2, R0, R5, c[0x0][0x168] ;                       /* 0x00005a0000027625 */
        |                                                                                                   /* 0x001fc800078e0005 */
        |        /*0060*/                   IMAD.WIDE.U32 R4, R0, R5, c[0x0][0x170] ;                       /* 0x00005c0000047625 */
        |                                                                                                   /* 0x000fc800078e0005 */
        |        /*0070*/                   IMAD.WIDE.U32 R6, R0.reuse, R17.reuse, c[0x0][0x178] ;          /* 0x00005e0000067625 */
        |                                                                                                   /* 0x0c0fe200078e0011 */
        |        /*0080*/                   LDG.E.SYS R8, [R2] ;                                            /* 0x0000000002087381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0090*/                   LDG.E.SYS R9, [R2+0x4] ;                                        /* 0x0000040002097381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00a0*/                   LDG.E.SYS R10, [R4] ;                                           /* 0x00000000040a7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00b0*/                   LDG.E.SYS R11, [R4+0x4] ;                                       /* 0x00000400040b7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00c0*/                   LDG.E.SYS R12, [R6] ;                                           /* 0x00000000060c7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00d0*/                   LDG.E.SYS R13, [R6+0x4] ;                                       /* 0x00000400060d7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00e0*/                   LDG.E.SYS R14, [R6+0x8] ;                                       /* 0x00000800060e7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00f0*/                   LDG.E.SYS R15, [R6+0xc] ;                                       /* 0x00000c00060f7381 */
        |                                                                                                   /* 0x000ea400001ee900 */
        |        /*0100*/                   HMMA.884.F16.F16.STEP0 R12, R8.reuse.COL, R10.reuse.ROW, R12 ;  /* 0x0000000a080c7236 */
        |                                                                                                   /* 0x0c4f68000000020c */
        |        /*0110*/                   HMMA.884.F16.F16.STEP1 R14, R8.COL, R10.ROW, R14 ;              /* 0x0000000a080e7236 */
        |                                                                                                   /* 0x000b64000000820e */
        |        /*0120*/                   IMAD.WIDE.U32 R8, R0, R17, c[0x0][0x160] ;                      /* 0x0000580000087625 */
        |                                                                                                   /* 0x020fd000078e0011 */
        |        /*0130*/                   STG.E.SYS [R8], R12 ;                                           /* 0x0000000c08007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0140*/                   STG.E.SYS [R8+0x4], R13 ;                                       /* 0x0000040d08007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0150*/                   STG.E.SYS [R8+0x8], R14 ;                                       /* 0x0000080e08007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0160*/                   STG.E.SYS [R8+0xc], R15 ;                                       /* 0x00000c0f08007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*0170*/                   EXIT ;                                                          /* 0x000000000000794d */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hmma884F16RowColSm70" ->
      """//--------------------- .text.hmma884_f16_row_col_probe --------------------------
        |	.section	.text.hmma884_f16_row_col_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=20"
        |	.align	128
        |        .global         hmma884_f16_row_col_probe
        |        .type           hmma884_f16_row_col_probe,@function
        |        .size           hmma884_f16_row_col_probe,(.L_x_1 - hmma884_f16_row_col_probe)
        |        .other          hmma884_f16_row_col_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmma884_f16_row_col_probe:
        |.text.hmma884_f16_row_col_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                          /* 0x00000a0000017a02 */
        |                                                                                                   /* 0x000fc40000000f00 */
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                                   /* 0x000000fffffff389 */
        |                                                                                                   /* 0x000fe200000e00ff */
        |        /*0020*/                   S2R R0, SR_TID.X ;                                              /* 0x0000000000007919 */
        |                                                                                                   /* 0x000e220000002100 */
        |        /*0030*/                   MOV R5, 0x8 ;                                                   /* 0x0000000800057802 */
        |                                                                                                   /* 0x000fe40000000f00 */
        |        /*0040*/                   MOV R17, 0x10 ;                                                 /* 0x0000001000117802 */
        |                                                                                                   /* 0x000fc60000000f00 */
        |        /*0050*/                   IMAD.WIDE.U32 R2, R0, R5, c[0x0][0x168] ;                       /* 0x00005a0000027625 */
        |                                                                                                   /* 0x001fc800078e0005 */
        |        /*0060*/                   IMAD.WIDE.U32 R4, R0, R5, c[0x0][0x170] ;                       /* 0x00005c0000047625 */
        |                                                                                                   /* 0x000fc800078e0005 */
        |        /*0070*/                   IMAD.WIDE.U32 R6, R0.reuse, R17.reuse, c[0x0][0x178] ;          /* 0x00005e0000067625 */
        |                                                                                                   /* 0x0c0fe200078e0011 */
        |        /*0080*/                   LDG.E.SYS R8, [R2] ;                                            /* 0x0000000002087381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0090*/                   LDG.E.SYS R9, [R2+0x4] ;                                        /* 0x0000040002097381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00a0*/                   LDG.E.SYS R10, [R4] ;                                           /* 0x00000000040a7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00b0*/                   LDG.E.SYS R11, [R4+0x4] ;                                       /* 0x00000400040b7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00c0*/                   LDG.E.SYS R12, [R6] ;                                           /* 0x00000000060c7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00d0*/                   LDG.E.SYS R13, [R6+0x4] ;                                       /* 0x00000400060d7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00e0*/                   LDG.E.SYS R14, [R6+0x8] ;                                       /* 0x00000800060e7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00f0*/                   LDG.E.SYS R15, [R6+0xc] ;                                       /* 0x00000c00060f7381 */
        |                                                                                                   /* 0x000ea400001ee900 */
        |        /*0100*/                   HMMA.884.F16.F16.STEP0 R12, R8.reuse.ROW, R10.reuse.COL, R12 ;  /* 0x0000000a080c7236 */
        |                                                                                                   /* 0x0c4f68000000040c */
        |        /*0110*/                   HMMA.884.F16.F16.STEP1 R14, R8.ROW, R10.COL, R14 ;              /* 0x0000000a080e7236 */
        |                                                                                                   /* 0x000b64000000840e */
        |        /*0120*/                   IMAD.WIDE.U32 R8, R0, R17, c[0x0][0x160] ;                      /* 0x0000580000087625 */
        |                                                                                                   /* 0x020fd000078e0011 */
        |        /*0130*/                   STG.E.SYS [R8], R12 ;                                           /* 0x0000000c08007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0140*/                   STG.E.SYS [R8+0x4], R13 ;                                       /* 0x0000040d08007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0150*/                   STG.E.SYS [R8+0x8], R14 ;                                       /* 0x0000080e08007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0160*/                   STG.E.SYS [R8+0xc], R15 ;                                       /* 0x00000c0f08007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*0170*/                   EXIT ;                                                          /* 0x000000000000794d */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hmma884F16RowRowSm70" ->
      """//--------------------- .text.hmma884_f16_row_row_probe --------------------------
        |	.section	.text.hmma884_f16_row_row_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=20"
        |	.align	128
        |        .global         hmma884_f16_row_row_probe
        |        .type           hmma884_f16_row_row_probe,@function
        |        .size           hmma884_f16_row_row_probe,(.L_x_1 - hmma884_f16_row_row_probe)
        |        .other          hmma884_f16_row_row_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmma884_f16_row_row_probe:
        |.text.hmma884_f16_row_row_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                          /* 0x00000a0000017a02 */
        |                                                                                                   /* 0x000fc40000000f00 */
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                                   /* 0x000000fffffff389 */
        |                                                                                                   /* 0x000fe200000e00ff */
        |        /*0020*/                   S2R R0, SR_TID.X ;                                              /* 0x0000000000007919 */
        |                                                                                                   /* 0x000e220000002100 */
        |        /*0030*/                   MOV R5, 0x8 ;                                                   /* 0x0000000800057802 */
        |                                                                                                   /* 0x000fe40000000f00 */
        |        /*0040*/                   MOV R17, 0x10 ;                                                 /* 0x0000001000117802 */
        |                                                                                                   /* 0x000fc60000000f00 */
        |        /*0050*/                   IMAD.WIDE.U32 R2, R0, R5, c[0x0][0x168] ;                       /* 0x00005a0000027625 */
        |                                                                                                   /* 0x001fc800078e0005 */
        |        /*0060*/                   IMAD.WIDE.U32 R4, R0, R5, c[0x0][0x170] ;                       /* 0x00005c0000047625 */
        |                                                                                                   /* 0x000fc800078e0005 */
        |        /*0070*/                   IMAD.WIDE.U32 R6, R0.reuse, R17.reuse, c[0x0][0x178] ;          /* 0x00005e0000067625 */
        |                                                                                                   /* 0x0c0fe200078e0011 */
        |        /*0080*/                   LDG.E.SYS R8, [R2] ;                                            /* 0x0000000002087381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0090*/                   LDG.E.SYS R9, [R2+0x4] ;                                        /* 0x0000040002097381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00a0*/                   LDG.E.SYS R10, [R4] ;                                           /* 0x00000000040a7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00b0*/                   LDG.E.SYS R11, [R4+0x4] ;                                       /* 0x00000400040b7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00c0*/                   LDG.E.SYS R12, [R6] ;                                           /* 0x00000000060c7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00d0*/                   LDG.E.SYS R13, [R6+0x4] ;                                       /* 0x00000400060d7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00e0*/                   LDG.E.SYS R14, [R6+0x8] ;                                       /* 0x00000800060e7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00f0*/                   LDG.E.SYS R15, [R6+0xc] ;                                       /* 0x00000c00060f7381 */
        |                                                                                                   /* 0x000ea400001ee900 */
        |        /*0100*/                   HMMA.884.F16.F16.STEP0 R12, R8.reuse.ROW, R10.reuse.ROW, R12 ;  /* 0x0000000a080c7236 */
        |                                                                                                   /* 0x0c4f68000000000c */
        |        /*0110*/                   HMMA.884.F16.F16.STEP1 R14, R8.ROW, R10.ROW, R14 ;              /* 0x0000000a080e7236 */
        |                                                                                                   /* 0x000b64000000800e */
        |        /*0120*/                   IMAD.WIDE.U32 R8, R0, R17, c[0x0][0x160] ;                      /* 0x0000580000087625 */
        |                                                                                                   /* 0x020fd000078e0011 */
        |        /*0130*/                   STG.E.SYS [R8], R12 ;                                           /* 0x0000000c08007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0140*/                   STG.E.SYS [R8+0x4], R13 ;                                       /* 0x0000040d08007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0150*/                   STG.E.SYS [R8+0x8], R14 ;                                       /* 0x0000080e08007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0160*/                   STG.E.SYS [R8+0xc], R15 ;                                       /* 0x00000c0f08007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*0170*/                   EXIT ;                                                          /* 0x000000000000794d */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hmma884F32ColColSm70" ->
      """//--------------------- .text.hmma884_f32_col_col_probe --------------------------
        |	.section	.text.hmma884_f32_col_col_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=24"
        |	.align	128
        |        .global         hmma884_f32_col_col_probe
        |        .type           hmma884_f32_col_col_probe,@function
        |        .size           hmma884_f32_col_col_probe,(.L_x_1 - hmma884_f32_col_col_probe)
        |        .other          hmma884_f32_col_col_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmma884_f32_col_col_probe:
        |.text.hmma884_f32_col_col_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                          /* 0x00000a0000017a02 */
        |                                                                                                   /* 0x000fc40000000f00 */
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                                   /* 0x000000fffffff389 */
        |                                                                                                   /* 0x000fe200000e00ff */
        |        /*0020*/                   S2R R0, SR_TID.X ;                                              /* 0x0000000000007919 */
        |                                                                                                   /* 0x000e220000002100 */
        |        /*0030*/                   MOV R15, 0x8 ;                                                  /* 0x00000008000f7802 */
        |                                                                                                   /* 0x000fe40000000f00 */
        |        /*0040*/                   MOV R21, 0x20 ;                                                 /* 0x0000002000157802 */
        |                                                                                                   /* 0x000fc60000000f00 */
        |        /*0050*/                   IMAD.WIDE.U32 R12, R0, R15, c[0x0][0x168] ;                     /* 0x00005a00000c7625 */
        |                                                                                                   /* 0x001fc800078e000f */
        |        /*0060*/                   IMAD.WIDE.U32 R14, R0, R15, c[0x0][0x170] ;                     /* 0x00005c00000e7625 */
        |                                                                                                   /* 0x000fc800078e000f */
        |        /*0070*/                   IMAD.WIDE.U32 R16, R0.reuse, R21.reuse, c[0x0][0x178] ;         /* 0x00005e0000107625 */
        |                                                                                                   /* 0x0c0fe200078e0015 */
        |        /*0080*/                   LDG.E.SYS R2, [R12] ;                                           /* 0x000000000c027381 */
        |                                                                                                   /* 0x0000a800001ee900 */
        |        /*0090*/                   LDG.E.SYS R3, [R12+0x4] ;                                       /* 0x000004000c037381 */
        |                                                                                                   /* 0x0000a800001ee900 */
        |        /*00a0*/                   LDG.E.SYS R18, [R14] ;                                          /* 0x000000000e127381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00b0*/                   LDG.E.SYS R19, [R14+0x4] ;                                      /* 0x000004000e137381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00c0*/                   LDG.E.SYS R8, [R16] ;                                           /* 0x0000000010087381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00d0*/                   LDG.E.SYS R9, [R16+0x4] ;                                       /* 0x0000040010097381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00e0*/                   LDG.E.SYS R10, [R16+0x8] ;                                      /* 0x00000800100a7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00f0*/                   LDG.E.SYS R11, [R16+0xc] ;                                      /* 0x00000c00100b7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0100*/                   LDG.E.SYS R4, [R16+0x10] ;                                      /* 0x0000100010047381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0110*/                   LDG.E.SYS R5, [R16+0x14] ;                                      /* 0x0000140010057381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0120*/                   LDG.E.SYS R6, [R16+0x18] ;                                      /* 0x0000180010067381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0130*/                   LDG.E.SYS R7, [R16+0x1c] ;                                      /* 0x00001c0010077381 */
        |                                                                                                   /* 0x000ea200001ee900 */
        |        /*0140*/                   IMAD.WIDE.U32 R12, R0, R21, c[0x0][0x160] ;                     /* 0x00005800000c7625 */
        |                                                                                                   /* 0x001fe200078e0015 */
        |        /*0150*/                   HMMA.884.F32.F32.STEP0 R8, R2.reuse.COL, R18.reuse.COL, R8 ;    /* 0x0000001202087236 */
        |                                                                                                   /* 0x0c4f640000005608 */
        |        /*0160*/                   HMMA.884.F32.F32.STEP1 R10, R2.reuse.COL, R18.reuse.COL, R10 ;  /* 0x00000012020a7236 */
        |                                                                                                   /* 0x0c0f70000000d60a */
        |        /*0170*/                   STG.E.SYS [R12], R8 ;                                           /* 0x000000080c007386 */
        |                                                                                                   /* 0x020fe8000010e900 */
        |        /*0180*/                   STG.E.SYS [R12+0x4], R9 ;                                       /* 0x000004090c007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*0190*/                   HMMA.884.F32.F32.STEP2 R4, R2.reuse.COL, R18.reuse.COL, R4 ;    /* 0x0000001202047236 */
        |                                                                                                   /* 0x0c0f660000015604 */
        |        /*01a0*/                   STG.E.SYS [R12+0x8], R10 ;                                      /* 0x0000080a0c007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*01b0*/                   STG.E.SYS [R12+0xc], R11 ;                                      /* 0x00000c0b0c007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*01c0*/                   HMMA.884.F32.F32.STEP3 R6, R2.COL, R18.COL, R6 ;                /* 0x0000001202067236 */
        |                                                                                                   /* 0x000f66000001d606 */
        |        /*01d0*/                   STG.E.SYS [R12+0x10], R4 ;                                      /* 0x000010040c007386 */
        |                                                                                                   /* 0x020fe8000010e900 */
        |        /*01e0*/                   STG.E.SYS [R12+0x14], R5 ;                                      /* 0x000014050c007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*01f0*/                   STG.E.SYS [R12+0x18], R6 ;                                      /* 0x000018060c007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0200*/                   STG.E.SYS [R12+0x1c], R7 ;                                      /* 0x00001c070c007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*0210*/                   EXIT ;                                                          /* 0x000000000000794d */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hmma884F32ColRowSm70" ->
      """//--------------------- .text.hmma884_f32_col_row_probe --------------------------
        |	.section	.text.hmma884_f32_col_row_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=24"
        |	.align	128
        |        .global         hmma884_f32_col_row_probe
        |        .type           hmma884_f32_col_row_probe,@function
        |        .size           hmma884_f32_col_row_probe,(.L_x_1 - hmma884_f32_col_row_probe)
        |        .other          hmma884_f32_col_row_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmma884_f32_col_row_probe:
        |.text.hmma884_f32_col_row_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                          /* 0x00000a0000017a02 */
        |                                                                                                   /* 0x000fc40000000f00 */
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                                   /* 0x000000fffffff389 */
        |                                                                                                   /* 0x000fe200000e00ff */
        |        /*0020*/                   S2R R0, SR_TID.X ;                                              /* 0x0000000000007919 */
        |                                                                                                   /* 0x000e220000002100 */
        |        /*0030*/                   MOV R15, 0x8 ;                                                  /* 0x00000008000f7802 */
        |                                                                                                   /* 0x000fe40000000f00 */
        |        /*0040*/                   MOV R21, 0x20 ;                                                 /* 0x0000002000157802 */
        |                                                                                                   /* 0x000fc60000000f00 */
        |        /*0050*/                   IMAD.WIDE.U32 R12, R0, R15, c[0x0][0x168] ;                     /* 0x00005a00000c7625 */
        |                                                                                                   /* 0x001fc800078e000f */
        |        /*0060*/                   IMAD.WIDE.U32 R14, R0, R15, c[0x0][0x170] ;                     /* 0x00005c00000e7625 */
        |                                                                                                   /* 0x000fc800078e000f */
        |        /*0070*/                   IMAD.WIDE.U32 R16, R0.reuse, R21.reuse, c[0x0][0x178] ;         /* 0x00005e0000107625 */
        |                                                                                                   /* 0x0c0fe200078e0015 */
        |        /*0080*/                   LDG.E.SYS R2, [R12] ;                                           /* 0x000000000c027381 */
        |                                                                                                   /* 0x0000a800001ee900 */
        |        /*0090*/                   LDG.E.SYS R3, [R12+0x4] ;                                       /* 0x000004000c037381 */
        |                                                                                                   /* 0x0000a800001ee900 */
        |        /*00a0*/                   LDG.E.SYS R18, [R14] ;                                          /* 0x000000000e127381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00b0*/                   LDG.E.SYS R19, [R14+0x4] ;                                      /* 0x000004000e137381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00c0*/                   LDG.E.SYS R8, [R16] ;                                           /* 0x0000000010087381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00d0*/                   LDG.E.SYS R9, [R16+0x4] ;                                       /* 0x0000040010097381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00e0*/                   LDG.E.SYS R10, [R16+0x8] ;                                      /* 0x00000800100a7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00f0*/                   LDG.E.SYS R11, [R16+0xc] ;                                      /* 0x00000c00100b7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0100*/                   LDG.E.SYS R4, [R16+0x10] ;                                      /* 0x0000100010047381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0110*/                   LDG.E.SYS R5, [R16+0x14] ;                                      /* 0x0000140010057381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0120*/                   LDG.E.SYS R6, [R16+0x18] ;                                      /* 0x0000180010067381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0130*/                   LDG.E.SYS R7, [R16+0x1c] ;                                      /* 0x00001c0010077381 */
        |                                                                                                   /* 0x000ea200001ee900 */
        |        /*0140*/                   IMAD.WIDE.U32 R12, R0, R21, c[0x0][0x160] ;                     /* 0x00005800000c7625 */
        |                                                                                                   /* 0x001fe200078e0015 */
        |        /*0150*/                   HMMA.884.F32.F32.STEP0 R8, R2.reuse.COL, R18.reuse.ROW, R8 ;    /* 0x0000001202087236 */
        |                                                                                                   /* 0x0c4f640000005208 */
        |        /*0160*/                   HMMA.884.F32.F32.STEP1 R10, R2.reuse.COL, R18.reuse.ROW, R10 ;  /* 0x00000012020a7236 */
        |                                                                                                   /* 0x0c0f70000000d20a */
        |        /*0170*/                   STG.E.SYS [R12], R8 ;                                           /* 0x000000080c007386 */
        |                                                                                                   /* 0x020fe8000010e900 */
        |        /*0180*/                   STG.E.SYS [R12+0x4], R9 ;                                       /* 0x000004090c007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*0190*/                   HMMA.884.F32.F32.STEP2 R4, R2.reuse.COL, R18.reuse.ROW, R4 ;    /* 0x0000001202047236 */
        |                                                                                                   /* 0x0c0f660000015204 */
        |        /*01a0*/                   STG.E.SYS [R12+0x8], R10 ;                                      /* 0x0000080a0c007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*01b0*/                   STG.E.SYS [R12+0xc], R11 ;                                      /* 0x00000c0b0c007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*01c0*/                   HMMA.884.F32.F32.STEP3 R6, R2.COL, R18.ROW, R6 ;                /* 0x0000001202067236 */
        |                                                                                                   /* 0x000f66000001d206 */
        |        /*01d0*/                   STG.E.SYS [R12+0x10], R4 ;                                      /* 0x000010040c007386 */
        |                                                                                                   /* 0x020fe8000010e900 */
        |        /*01e0*/                   STG.E.SYS [R12+0x14], R5 ;                                      /* 0x000014050c007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*01f0*/                   STG.E.SYS [R12+0x18], R6 ;                                      /* 0x000018060c007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0200*/                   STG.E.SYS [R12+0x1c], R7 ;                                      /* 0x00001c070c007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*0210*/                   EXIT ;                                                          /* 0x000000000000794d */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hmma884F32RowColSm70" ->
      """//--------------------- .text.hmma884_f32_row_col_probe --------------------------
        |	.section	.text.hmma884_f32_row_col_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=24"
        |	.align	128
        |        .global         hmma884_f32_row_col_probe
        |        .type           hmma884_f32_row_col_probe,@function
        |        .size           hmma884_f32_row_col_probe,(.L_x_1 - hmma884_f32_row_col_probe)
        |        .other          hmma884_f32_row_col_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmma884_f32_row_col_probe:
        |.text.hmma884_f32_row_col_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                          /* 0x00000a0000017a02 */
        |                                                                                                   /* 0x000fc40000000f00 */
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                                   /* 0x000000fffffff389 */
        |                                                                                                   /* 0x000fe200000e00ff */
        |        /*0020*/                   S2R R0, SR_TID.X ;                                              /* 0x0000000000007919 */
        |                                                                                                   /* 0x000e220000002100 */
        |        /*0030*/                   MOV R15, 0x8 ;                                                  /* 0x00000008000f7802 */
        |                                                                                                   /* 0x000fe40000000f00 */
        |        /*0040*/                   MOV R21, 0x20 ;                                                 /* 0x0000002000157802 */
        |                                                                                                   /* 0x000fc60000000f00 */
        |        /*0050*/                   IMAD.WIDE.U32 R12, R0, R15, c[0x0][0x168] ;                     /* 0x00005a00000c7625 */
        |                                                                                                   /* 0x001fc800078e000f */
        |        /*0060*/                   IMAD.WIDE.U32 R14, R0, R15, c[0x0][0x170] ;                     /* 0x00005c00000e7625 */
        |                                                                                                   /* 0x000fc800078e000f */
        |        /*0070*/                   IMAD.WIDE.U32 R16, R0.reuse, R21.reuse, c[0x0][0x178] ;         /* 0x00005e0000107625 */
        |                                                                                                   /* 0x0c0fe200078e0015 */
        |        /*0080*/                   LDG.E.SYS R2, [R12] ;                                           /* 0x000000000c027381 */
        |                                                                                                   /* 0x0000a800001ee900 */
        |        /*0090*/                   LDG.E.SYS R3, [R12+0x4] ;                                       /* 0x000004000c037381 */
        |                                                                                                   /* 0x0000a800001ee900 */
        |        /*00a0*/                   LDG.E.SYS R18, [R14] ;                                          /* 0x000000000e127381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00b0*/                   LDG.E.SYS R19, [R14+0x4] ;                                      /* 0x000004000e137381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00c0*/                   LDG.E.SYS R8, [R16] ;                                           /* 0x0000000010087381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00d0*/                   LDG.E.SYS R9, [R16+0x4] ;                                       /* 0x0000040010097381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00e0*/                   LDG.E.SYS R10, [R16+0x8] ;                                      /* 0x00000800100a7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00f0*/                   LDG.E.SYS R11, [R16+0xc] ;                                      /* 0x00000c00100b7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0100*/                   LDG.E.SYS R4, [R16+0x10] ;                                      /* 0x0000100010047381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0110*/                   LDG.E.SYS R5, [R16+0x14] ;                                      /* 0x0000140010057381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0120*/                   LDG.E.SYS R6, [R16+0x18] ;                                      /* 0x0000180010067381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0130*/                   LDG.E.SYS R7, [R16+0x1c] ;                                      /* 0x00001c0010077381 */
        |                                                                                                   /* 0x000ea200001ee900 */
        |        /*0140*/                   IMAD.WIDE.U32 R12, R0, R21, c[0x0][0x160] ;                     /* 0x00005800000c7625 */
        |                                                                                                   /* 0x001fe200078e0015 */
        |        /*0150*/                   HMMA.884.F32.F32.STEP0 R8, R2.reuse.ROW, R18.reuse.COL, R8 ;    /* 0x0000001202087236 */
        |                                                                                                   /* 0x0c4f640000005408 */
        |        /*0160*/                   HMMA.884.F32.F32.STEP1 R10, R2.reuse.ROW, R18.reuse.COL, R10 ;  /* 0x00000012020a7236 */
        |                                                                                                   /* 0x0c0f70000000d40a */
        |        /*0170*/                   STG.E.SYS [R12], R8 ;                                           /* 0x000000080c007386 */
        |                                                                                                   /* 0x020fe8000010e900 */
        |        /*0180*/                   STG.E.SYS [R12+0x4], R9 ;                                       /* 0x000004090c007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*0190*/                   HMMA.884.F32.F32.STEP2 R4, R2.reuse.ROW, R18.reuse.COL, R4 ;    /* 0x0000001202047236 */
        |                                                                                                   /* 0x0c0f660000015404 */
        |        /*01a0*/                   STG.E.SYS [R12+0x8], R10 ;                                      /* 0x0000080a0c007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*01b0*/                   STG.E.SYS [R12+0xc], R11 ;                                      /* 0x00000c0b0c007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*01c0*/                   HMMA.884.F32.F32.STEP3 R6, R2.ROW, R18.COL, R6 ;                /* 0x0000001202067236 */
        |                                                                                                   /* 0x000f66000001d406 */
        |        /*01d0*/                   STG.E.SYS [R12+0x10], R4 ;                                      /* 0x000010040c007386 */
        |                                                                                                   /* 0x020fe8000010e900 */
        |        /*01e0*/                   STG.E.SYS [R12+0x14], R5 ;                                      /* 0x000014050c007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*01f0*/                   STG.E.SYS [R12+0x18], R6 ;                                      /* 0x000018060c007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0200*/                   STG.E.SYS [R12+0x1c], R7 ;                                      /* 0x00001c070c007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*0210*/                   EXIT ;                                                          /* 0x000000000000794d */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hmma884F32RowRowSm70" ->
      """//--------------------- .text.hmma884_f32_row_row_probe --------------------------
        |	.section	.text.hmma884_f32_row_row_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=24"
        |	.align	128
        |        .global         hmma884_f32_row_row_probe
        |        .type           hmma884_f32_row_row_probe,@function
        |        .size           hmma884_f32_row_row_probe,(.L_x_1 - hmma884_f32_row_row_probe)
        |        .other          hmma884_f32_row_row_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmma884_f32_row_row_probe:
        |.text.hmma884_f32_row_row_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                          /* 0x00000a0000017a02 */
        |                                                                                                   /* 0x000fc40000000f00 */
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                                   /* 0x000000fffffff389 */
        |                                                                                                   /* 0x000fe200000e00ff */
        |        /*0020*/                   S2R R0, SR_TID.X ;                                              /* 0x0000000000007919 */
        |                                                                                                   /* 0x000e220000002100 */
        |        /*0030*/                   MOV R15, 0x8 ;                                                  /* 0x00000008000f7802 */
        |                                                                                                   /* 0x000fe40000000f00 */
        |        /*0040*/                   MOV R21, 0x20 ;                                                 /* 0x0000002000157802 */
        |                                                                                                   /* 0x000fc60000000f00 */
        |        /*0050*/                   IMAD.WIDE.U32 R12, R0, R15, c[0x0][0x168] ;                     /* 0x00005a00000c7625 */
        |                                                                                                   /* 0x001fc800078e000f */
        |        /*0060*/                   IMAD.WIDE.U32 R14, R0, R15, c[0x0][0x170] ;                     /* 0x00005c00000e7625 */
        |                                                                                                   /* 0x000fc800078e000f */
        |        /*0070*/                   IMAD.WIDE.U32 R16, R0.reuse, R21.reuse, c[0x0][0x178] ;         /* 0x00005e0000107625 */
        |                                                                                                   /* 0x0c0fe200078e0015 */
        |        /*0080*/                   LDG.E.SYS R2, [R12] ;                                           /* 0x000000000c027381 */
        |                                                                                                   /* 0x0000a800001ee900 */
        |        /*0090*/                   LDG.E.SYS R3, [R12+0x4] ;                                       /* 0x000004000c037381 */
        |                                                                                                   /* 0x0000a800001ee900 */
        |        /*00a0*/                   LDG.E.SYS R18, [R14] ;                                          /* 0x000000000e127381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00b0*/                   LDG.E.SYS R19, [R14+0x4] ;                                      /* 0x000004000e137381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00c0*/                   LDG.E.SYS R8, [R16] ;                                           /* 0x0000000010087381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00d0*/                   LDG.E.SYS R9, [R16+0x4] ;                                       /* 0x0000040010097381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00e0*/                   LDG.E.SYS R10, [R16+0x8] ;                                      /* 0x00000800100a7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*00f0*/                   LDG.E.SYS R11, [R16+0xc] ;                                      /* 0x00000c00100b7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0100*/                   LDG.E.SYS R4, [R16+0x10] ;                                      /* 0x0000100010047381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0110*/                   LDG.E.SYS R5, [R16+0x14] ;                                      /* 0x0000140010057381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0120*/                   LDG.E.SYS R6, [R16+0x18] ;                                      /* 0x0000180010067381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0130*/                   LDG.E.SYS R7, [R16+0x1c] ;                                      /* 0x00001c0010077381 */
        |                                                                                                   /* 0x000ea200001ee900 */
        |        /*0140*/                   IMAD.WIDE.U32 R12, R0, R21, c[0x0][0x160] ;                     /* 0x00005800000c7625 */
        |                                                                                                   /* 0x001fe200078e0015 */
        |        /*0150*/                   HMMA.884.F32.F32.STEP0 R8, R2.reuse.ROW, R18.reuse.ROW, R8 ;    /* 0x0000001202087236 */
        |                                                                                                   /* 0x0c4f640000005008 */
        |        /*0160*/                   HMMA.884.F32.F32.STEP1 R10, R2.reuse.ROW, R18.reuse.ROW, R10 ;  /* 0x00000012020a7236 */
        |                                                                                                   /* 0x0c0f70000000d00a */
        |        /*0170*/                   STG.E.SYS [R12], R8 ;                                           /* 0x000000080c007386 */
        |                                                                                                   /* 0x020fe8000010e900 */
        |        /*0180*/                   STG.E.SYS [R12+0x4], R9 ;                                       /* 0x000004090c007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*0190*/                   HMMA.884.F32.F32.STEP2 R4, R2.reuse.ROW, R18.reuse.ROW, R4 ;    /* 0x0000001202047236 */
        |                                                                                                   /* 0x0c0f660000015004 */
        |        /*01a0*/                   STG.E.SYS [R12+0x8], R10 ;                                      /* 0x0000080a0c007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*01b0*/                   STG.E.SYS [R12+0xc], R11 ;                                      /* 0x00000c0b0c007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*01c0*/                   HMMA.884.F32.F32.STEP3 R6, R2.ROW, R18.ROW, R6 ;                /* 0x0000001202067236 */
        |                                                                                                   /* 0x000f66000001d006 */
        |        /*01d0*/                   STG.E.SYS [R12+0x10], R4 ;                                      /* 0x000010040c007386 */
        |                                                                                                   /* 0x020fe8000010e900 */
        |        /*01e0*/                   STG.E.SYS [R12+0x14], R5 ;                                      /* 0x000014050c007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*01f0*/                   STG.E.SYS [R12+0x18], R6 ;                                      /* 0x000018060c007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0200*/                   STG.E.SYS [R12+0x1c], R7 ;                                      /* 0x00001c070c007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*0210*/                   EXIT ;                                                          /* 0x000000000000794d */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hmma884F32ZeroSm70" ->
      """//--------------------- .text.hmma884_f32_probe   --------------------------
        |	.section	.text.hmma884_f32_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=22"
        |	.align	128
        |        .global         hmma884_f32_probe
        |        .type           hmma884_f32_probe,@function
        |        .size           hmma884_f32_probe,(.L_x_1 - hmma884_f32_probe)
        |        .other          hmma884_f32_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmma884_f32_probe:
        |.text.hmma884_f32_probe:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                                          /* 0x00000a0000017a02 */
        |                                                                                                   /* 0x000fc40000000f00 */
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                                   /* 0x000000fffffff389 */
        |                                                                                                   /* 0x000fe200000e00ff */
        |        /*0020*/                   S2R R10, SR_TID.X ;                                             /* 0x00000000000a7919 */
        |                                                                                                   /* 0x000e220000002100 */
        |        /*0030*/                   MOV R9, 0x8 ;                                                   /* 0x0000000800097802 */
        |                                                                                                   /* 0x000fca0000000f00 */
        |        /*0040*/                   IMAD.WIDE.U32 R2, R10, R9, c[0x0][0x168] ;                      /* 0x00005a000a027625 */
        |                                                                                                   /* 0x001fc800078e0009 */
        |        /*0050*/                   IMAD.WIDE.U32 R8, R10, R9, c[0x0][0x170] ;                      /* 0x00005c000a087625 */
        |                                                                                                   /* 0x000fc800078e0009 */
        |        /*0060*/                   LDG.E.SYS R12, [R2] ;                                           /* 0x00000000020c7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0070*/                   LDG.E.SYS R13, [R2+0x4] ;                                       /* 0x00000400020d7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0080*/                   LDG.E.SYS R14, [R8] ;                                           /* 0x00000000080e7381 */
        |                                                                                                   /* 0x000ea800001ee900 */
        |        /*0090*/                   LDG.E.SYS R15, [R8+0x4] ;                                       /* 0x00000400080f7381 */
        |                                                                                                   /* 0x000ea200001ee900 */
        |        /*00a0*/                   MOV R11, 0x20 ;                                                 /* 0x00000020000b7802 */
        |                                                                                                   /* 0x000fca0000000f00 */
        |        /*00b0*/                   IMAD.WIDE.U32 R10, R10, R11, c[0x0][0x160] ;                    /* 0x000058000a0a7625 */
        |                                                                                                   /* 0x000fe200078e000b */
        |        /*00c0*/                   HMMA.884.F32.F32.STEP0 R4, R12.reuse.ROW, R14.reuse.COL, RZ ;   /* 0x0000000e0c047236 */
        |                                                                                                   /* 0x0c4f6400000054ff */
        |        /*00d0*/                   HMMA.884.F32.F32.STEP1 R6, R12.reuse.ROW, R14.reuse.COL, RZ ;   /* 0x0000000e0c067236 */
        |                                                                                                   /* 0x0c0f64000000d4ff */
        |        /*00e0*/                   HMMA.884.F32.F32.STEP2 R16, R12.reuse.ROW, R14.reuse.COL, RZ ;  /* 0x0000000e0c107236 */
        |                                                                                                   /* 0x0c0f6400000154ff */
        |        /*00f0*/                   HMMA.884.F32.F32.STEP3 R18, R12.ROW, R14.COL, RZ ;              /* 0x0000000e0c127236 */
        |                                                                                                   /* 0x000f68000001d4ff */
        |        /*0100*/                   STG.E.SYS [R10], R4 ;                                           /* 0x000000040a007386 */
        |                                                                                                   /* 0x020fe8000010e900 */
        |        /*0110*/                   STG.E.SYS [R10+0x4], R5 ;                                       /* 0x000004050a007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0120*/                   STG.E.SYS [R10+0x8], R6 ;                                       /* 0x000008060a007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0130*/                   STG.E.SYS [R10+0xc], R7 ;                                       /* 0x00000c070a007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0140*/                   STG.E.SYS [R10+0x10], R16 ;                                     /* 0x000010100a007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0150*/                   STG.E.SYS [R10+0x14], R17 ;                                     /* 0x000014110a007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0160*/                   STG.E.SYS [R10+0x18], R18 ;                                     /* 0x000018120a007386 */
        |                                                                                                   /* 0x000fe8000010e900 */
        |        /*0170*/                   STG.E.SYS [R10+0x1c], R19 ;                                     /* 0x00001c130a007386 */
        |                                                                                                   /* 0x000fe2000010e900 */
        |        /*0180*/                   EXIT ;                                                          /* 0x000000000000794d */
        |                                                                                                   /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "hmmaF16AccAliasSm89" ->
      """//--------------------- .text.mma_f16_acc_alias   --------------------------
        |	.section	.text.mma_f16_acc_alias,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=20"
        |	.align	128
        |        .global         mma_f16_acc_alias
        |        .type           mma_f16_acc_alias,@function
        |        .size           mma_f16_acc_alias,(.L_x_1 - mma_f16_acc_alias)
        |        .other          mma_f16_acc_alias,@"STO_CUDA_ENTRY STV_DEFAULT"
        |mma_f16_acc_alias:
        |.text.mma_f16_acc_alias:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   MOV R13, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   SHF.L.U32 R2, R0.reuse, 0x1, RZ ;
        |        /*0050*/                   SHF.L.U32 R0, R0, 0x2, RZ ;
        |        /*0060*/                   LOP3.LUT R12, R2, 0x3e, RZ, 0xc0, !PT ;
        |        /*0070*/                   LOP3.LUT R0, R0, 0x7c, RZ, 0xc0, !PT ;
        |        /*0080*/                   IMAD.WIDE.U32 R4, R12, R13, c[0x0][0x170] ;
        |        /*0090*/                   IMAD.WIDE.U32 R6, R12, R13.reuse, c[0x0][0x178] ;
        |        /*00a0*/                   LDG.E R14, [R4.64] ;
        |        /*00b0*/                   IMAD.WIDE.U32 R2, R0, R13.reuse, c[0x0][0x168] ;
        |        /*00c0*/                   LDG.E R15, [R4.64+0x4] ;
        |        /*00d0*/                   LDG.E R16, [R6.64] ;
        |        /*00e0*/                   LDG.E R17, [R6.64+0x4] ;
        |        /*00f0*/                   LDG.E R8, [R2.64] ;
        |        /*0100*/                   LDG.E R9, [R2.64+0x4] ;
        |        /*0110*/                   LDG.E R10, [R2.64+0x8] ;
        |        /*0120*/                   LDG.E R11, [R2.64+0xc] ;
        |        /*0130*/                   HMMA.16816.F16 R10, R8, R14, R16 ;
        |        /*0140*/                   IMAD.WIDE.U32 R8, R12, R13, c[0x0][0x160] ;
        |        /*0150*/                   @!UPT UIADD3 URZ, URZ, URZ, URZ ;
        |        /*0160*/                   STG.E [R8.64], R10 ;
        |        /*0170*/                   STG.E [R8.64+0x4], R11 ;
        |        /*0180*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hmmaF16AccNonaliasSm89" ->
      """//--------------------- .text.mma_f16_acc_nonalias --------------------------
        |	.section	.text.mma_f16_acc_nonalias,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=20"
        |	.align	128
        |        .global         mma_f16_acc_nonalias
        |        .type           mma_f16_acc_nonalias,@function
        |        .size           mma_f16_acc_nonalias,(.L_x_1 - mma_f16_acc_nonalias)
        |        .other          mma_f16_acc_nonalias,@"STO_CUDA_ENTRY STV_DEFAULT"
        |mma_f16_acc_nonalias:
        |.text.mma_f16_acc_nonalias:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   MOV R13, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   SHF.L.U32 R2, R0.reuse, 0x1, RZ ;
        |        /*0050*/                   SHF.L.U32 R0, R0, 0x2, RZ ;
        |        /*0060*/                   LOP3.LUT R12, R2, 0x3e, RZ, 0xc0, !PT ;
        |        /*0070*/                   LOP3.LUT R0, R0, 0x7c, RZ, 0xc0, !PT ;
        |        /*0080*/                   IMAD.WIDE.U32 R4, R12, R13, c[0x0][0x170] ;
        |        /*0090*/                   IMAD.WIDE.U32 R6, R12, R13.reuse, c[0x0][0x178] ;
        |        /*00a0*/                   LDG.E R14, [R4.64] ;
        |        /*00b0*/                   IMAD.WIDE.U32 R2, R0, R13.reuse, c[0x0][0x168] ;
        |        /*00c0*/                   LDG.E R15, [R4.64+0x4] ;
        |        /*00d0*/                   LDG.E R16, [R6.64] ;
        |        /*00e0*/                   LDG.E R17, [R6.64+0x4] ;
        |        /*00f0*/                   LDG.E R8, [R2.64] ;
        |        /*0100*/                   LDG.E R9, [R2.64+0x4] ;
        |        /*0110*/                   LDG.E R10, [R2.64+0x8] ;
        |        /*0120*/                   LDG.E R11, [R2.64+0xc] ;
        |        /*0130*/                   HMMA.16816.F16 R10, R8, R14, R16 ;
        |        /*0140*/                   IMAD.WIDE.U32 R8, R12, R13, c[0x0][0x160] ;
        |        /*0150*/                   STG.E [R8.64+0x100], R16 ;
        |        /*0160*/                   STG.E [R8.64+0x104], R17 ;
        |        /*0170*/                   STG.E [R8.64], R10 ;
        |        /*0180*/                   STG.E [R8.64+0x4], R11 ;
        |        /*0190*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "ldsmHmma1688TransSm80" ->
      """//--------------------- .text.ldmatrix_trans_mma_f16 --------------------------
        |	.section	.text.ldmatrix_trans_mma_f16,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=18"
        |	.align	128
        |        .global         ldmatrix_trans_mma_f16
        |        .type           ldmatrix_trans_mma_f16,@function
        |        .size           ldmatrix_trans_mma_f16,(.L_x_3 - ldmatrix_trans_mma_f16)
        |        .other          ldmatrix_trans_mma_f16,@"STO_CUDA_ENTRY STV_DEFAULT"
        |ldmatrix_trans_mma_f16:
        |.text.ldmatrix_trans_mma_f16:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;                          /* 0x00000a0000017a02 */
        |                                                                                   /* 0x000fe40000000f00 */
        |        /*0010*/                   S2R R6, SR_TID.X ;                              /* 0x0000000000067919 */
        |                                                                                   /* 0x000e220000002100 */
        |        /*0020*/                   BSSY B0, `(.L_x_0) ;                            /* 0x0000039000007945 */
        |                                                                                   /* 0x000fe20003800000 */
        |        /*0030*/                   SHF.L.U32 R0, R6, 0x1, RZ ;                     /* 0x0000000106007819 */
        |                                                                                   /* 0x001fe400000006ff */
        |        /*0040*/                   SHF.R.U32.HI R2, RZ, 0x2, R6 ;                  /* 0x00000002ff027819 */
        |                                                                                   /* 0x000fe40000011606 */
        |        /*0050*/                   LOP3.LUT R5, R0, 0x6, RZ, 0xc0, !PT ;           /* 0x0000000600057812 */
        |                                                                                   /* 0x000fe400078ec0ff */
        |        /*0060*/                   LOP3.LUT R3, R6, 0x3, RZ, 0xc0, !PT ;           /* 0x0000000306037812 */
        |                                                                                   /* 0x000fe400078ec0ff */
        |        /*0070*/                   SHF.L.U32 R7, R2, 0x1, RZ ;                     /* 0x0000000102077819 */
        |                                                                                   /* 0x000fc400000006ff */
        |        /*0080*/                   LOP3.LUT R4, R5, 0x1, RZ, 0xfc, !PT ;           /* 0x0000000105047812 */
        |                                                                                   /* 0x000fe400078efcff */
        |        /*0090*/                   ISETP.GT.U32.AND P0, PT, R6.reuse, 0x1f, PT ;   /* 0x0000001f0600780c */
        |                                                                                   /* 0x040fe20003f04070 */
        |        /*00a0*/                   IMAD R12, R3, 0xa, R7.reuse ;                   /* 0x0000000a030c7824 */
        |                                                                                   /* 0x100fe200078e0207 */
        |        /*00b0*/                   ISETP.GE.U32.AND P1, PT, R6, 0x10, PT ;         /* 0x000000100600780c */
        |                                                                                   /* 0x000fe20003f26070 */
        |        /*00c0*/                   IMAD R7, R4, 0x5, R7 ;                          /* 0x0000000504077824 */
        |                                                                                   /* 0x000fe200078e0207 */
        |        /*00d0*/                   LEA R5, R2, R5, 0x3 ;                           /* 0x0000000502057211 */
        |                                                                                   /* 0x000fe200078e18ff */
        |        /*00e0*/                   IMAD.WIDE.U32 R8, R12.reuse, -0x55555555, RZ ;  /* 0xaaaaaaab0c087825 */
        |                                                                                   /* 0x040fe200078e00ff */
        |        /*00f0*/                   IADD3 R13, R12, 0x10, RZ ;                      /* 0x000000100c0d7810 */
        |                                                                                   /* 0x000fe40007ffe0ff */
        |        /*0100*/                   ISETP.GE.U32.AND P2, PT, R6, 0x8, PT ;          /* 0x000000080600780c */
        |                                                                                   /* 0x000fe20003f46070 */
        |        /*0110*/                   IMAD.WIDE.U32 R10, R7, -0x55555555, RZ ;        /* 0xaaaaaaab070a7825 */
        |                                                                                   /* 0x000fe200078e00ff */
        |        /*0120*/                   SHF.R.U32.HI R15, RZ, 0x1, R9 ;                 /* 0x00000001ff0f7819 */
        |                                                                                   /* 0x000fc40000011609 */
        |        /*0130*/                   IADD3 R10, R7, 0x10, RZ ;                       /* 0x00000010070a7810 */
        |                                                                                   /* 0x000fe20007ffe0ff */
        |        /*0140*/                   IMAD.WIDE.U32 R8, R13, -0x55555555, RZ ;        /* 0xaaaaaaab0d087825 */
        |                                                                                   /* 0x000fe200078e00ff */
        |        /*0150*/                   SHF.R.U32.HI R8, RZ, 0x1, R11 ;                 /* 0x00000001ff087819 */
        |                                                                                   /* 0x000fc6000001160b */
        |        /*0160*/                   IMAD R12, R15, -0x3, R12 ;                      /* 0xfffffffd0f0c7824 */
        |                                                                                   /* 0x000fe200078e020c */
        |        /*0170*/                   SHF.R.U32.HI R14, RZ, 0x1, R9 ;                 /* 0x00000001ff0e7819 */
        |                                                                                   /* 0x000fe20000011609 */
        |        /*0180*/                   IMAD R7, R8, -0x3, R7 ;                         /* 0xfffffffd08077824 */
        |                                                                                   /* 0x000fe400078e0207 */
        |        /*0190*/                   IMAD.WIDE.U32 R8, R10, -0x55555555, RZ ;        /* 0xaaaaaaab0a087825 */
        |                                                                                   /* 0x000fe400078e00ff */
        |        /*01a0*/                   I2F.U32 R12, R12 ;                              /* 0x0000000c000c7306 */
        |                                                                                   /* 0x000e240000201000 */
        |        /*01b0*/                   IMAD R13, R14, -0x3, R13 ;                      /* 0xfffffffd0e0d7824 */
        |                                                                                   /* 0x000fe200078e020d */
        |        /*01c0*/                   SHF.R.U32.HI R9, RZ, 0x1, R9 ;                  /* 0x00000001ff097819 */
        |                                                                                   /* 0x000fe20000011609 */
        |        /*01d0*/                   IMAD.SHL.U32 R11, R6, 0x8, RZ ;                 /* 0x00000008060b7824 */
        |                                                                                   /* 0x000fc800078e00ff */
        |        /*01e0*/                   IMAD R10, R9, -0x3, R10 ;                       /* 0xfffffffd090a7824 */
        |                                                                                   /* 0x000fe200078e020a */
        |        /*01f0*/                   I2F.U32 R7, R7 ;                                /* 0x0000000700077306 */
        |                                                                                   /* 0x000e620000201000 */
        |        /*0200*/                   FADD R12, R12, 1 ;                              /* 0x3f8000000c0c7421 */
        |                                                                                   /* 0x001fce0000000000 */
        |        /*0210*/                   I2F.U32 R13, R13 ;                              /* 0x0000000d000d7306 */
        |                                                                                   /* 0x000e300000201000 */
        |        /*0220*/                   I2F.U32 R10, R10 ;                              /* 0x0000000a000a7306 */
        |                                                                                   /* 0x000ea20000201000 */
        |        /*0230*/                   FADD R9, R7, 1 ;                                /* 0x3f80000007097421 */
        |                                                                                   /* 0x002fca0000000000 */
        |        /*0240*/                   F2FP.PACK_AB R6, R9, R12 ;                      /* 0x0000000c0906723e */
        |                                                                                   /* 0x000fe200000000ff */
        |        /*0250*/                   FADD R8, R13, 1 ;                               /* 0x3f8000000d087421 */
        |                                                                                   /* 0x001fe20000000000 */
        |        /*0260*/                   SEL R12, R11.reuse, RZ, !P1 ;                   /* 0x000000ff0b0c7207 */
        |                                                                                   /* 0x040fe40004800000 */
        |        /*0270*/                   SEL R11, R11, RZ, !P2 ;                         /* 0x000000ff0b0b7207 */
        |                                                                                   /* 0x000fe20005000000 */
        |        /*0280*/                   FADD R7, R10, 1 ;                               /* 0x3f8000000a077421 */
        |                                                                                   /* 0x004fca0000000000 */
        |        /*0290*/                   F2FP.PACK_AB R8, R7, R8 ;                       /* 0x000000080708723e */
        |                                                                                   /* 0x000fe200000000ff */
        |        /*02a0*/                   IMAD.SHL.U32 R7, R5, 0x2, RZ ;                  /* 0x0000000205077824 */
        |                                                                                   /* 0x000fe200078e00ff */
        |        /*02b0*/               @P0 BRA `(.L_x_1) ;                                 /* 0x000000f000000947 */
        |                                                                                   /* 0x000fea0003800000 */
        |        /*02c0*/                   IMAD R2, R2, 0x5, RZ ;                          /* 0x0000000502027824 */
        |                                                                                   /* 0x000fc800078e02ff */
        |        /*02d0*/                   IMAD R9, R3, 0xe, R2.reuse ;                    /* 0x0000000e03097824 */
        |                                                                                   /* 0x100fe400078e0202 */
        |        /*02e0*/                   IMAD R10, R4, 0x7, R2 ;                         /* 0x00000007040a7824 */
        |                                                                                   /* 0x000fe400078e0202 */
        |        /*02f0*/                   IMAD.WIDE.U32 R2, R9, -0x55555555, RZ ;         /* 0xaaaaaaab09027825 */
        |                                                                                   /* 0x000fc800078e00ff */
        |        /*0300*/                   IMAD.WIDE.U32 R4, R10, -0x55555555, RZ ;        /* 0xaaaaaaab0a047825 */
        |                                                                                   /* 0x000fe200078e00ff */
        |        /*0310*/                   SHF.R.U32.HI R2, RZ, 0x1, R3 ;                  /* 0x00000001ff027819 */
        |                                                                                   /* 0x000fc80000011603 */
        |        /*0320*/                   SHF.R.U32.HI R5, RZ, 0x1, R5 ;                  /* 0x00000001ff057819 */
        |                                                                                   /* 0x000fe20000011605 */
        |        /*0330*/                   IMAD R9, R2, -0x3, R9 ;                         /* 0xfffffffd02097824 */
        |                                                                                   /* 0x000fc800078e0209 */
        |        /*0340*/                   IMAD R10, R5, -0x3, R10 ;                       /* 0xfffffffd050a7824 */
        |                                                                                   /* 0x000fe400078e020a */
        |        /*0350*/                   I2F.U32 R9, R9 ;                                /* 0x0000000900097306 */
        |                                                                                   /* 0x000e300000201000 */
        |        /*0360*/                   I2F.U32 R10, R10 ;                              /* 0x0000000a000a7306 */
        |                                                                                   /* 0x000e620000201000 */
        |        /*0370*/                   FADD R2, R9, 1 ;                                /* 0x3f80000009027421 */
        |                                                                                   /* 0x001fc40000000000 */
        |        /*0380*/                   FADD R3, R10, 1 ;                               /* 0x3f8000000a037421 */
        |                                                                                   /* 0x002fca0000000000 */
        |        /*0390*/                   F2FP.PACK_AB R2, R3, R2 ;                       /* 0x000000020302723e */
        |                                                                                   /* 0x000fca00000000ff */
        |        /*03a0*/                   STS [R7+0x100], R2 ;                            /* 0x0001000207007388 */
        |                                                                                   /* 0x0001e40000000800 */
        |.L_x_1:
        |        /*03b0*/                   BSYNC B0 ;                                      /* 0x0000000000007941 */
        |                                                                                   /* 0x000fea0003800000 */
        |.L_x_0:
        |        /*03c0*/                   STS [R7], R6 ;                                  /* 0x0000000607007388 */
        |                                                                                   /* 0x000fe20000000800 */
        |        /*03d0*/                   LEA R11, R11, 0x100, 0x1 ;                      /* 0x000001000b0b7811 */
        |                                                                                   /* 0x000fe200078e08ff */
        |        /*03e0*/                   ULDC.64 UR4, c[0x0][0x118] ;                    /* 0x0000460000047ab9 */
        |                                                                                   /* 0x000fe20000000a00 */
        |        /*03f0*/                   SHF.L.U32 R12, R12, 0x1, RZ ;                   /* 0x000000010c0c7819 */
        |                                                                                   /* 0x000fe200000006ff */
        |        /*0400*/                   STS [R7+0x80], R8 ;                             /* 0x0000800807007388 */
        |                                                                                   /* 0x000fe20000000800 */
        |        /*0410*/                   MOV R3, 0x4 ;                                   /* 0x0000000400037802 */
        |                                                                                   /* 0x000fc60000000f00 */
        |        /*0420*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;                   /* 0x0000000000007b1d */
        |                                                                                   /* 0x000fe40000010000 */
        |        /*0430*/                   IMAD.WIDE.U32 R2, R0, R3, c[0x0][0x160] ;       /* 0x0000580000027625 */
        |                                                                                   /* 0x001fc800078e0003 */
        |        /*0440*/                   LDSM.16.MT88 R11, [R11] ;                       /* 0x000000000b0b783b */
        |                                                                                   /* 0x000fe80000004000 */
        |        /*0450*/                   LDSM.16.M88.2 R12, [R12] ;                      /* 0x000000000c0c783b */
        |                                                                                   /* 0x000e240000000100 */
        |        /*0460*/                   HMMA.1688.F16 R4, R12, R11, RZ ;                /* 0x0000000b0c04723c */
        |                                                                                   /* 0x001fde00000000ff */
        |        /*0470*/                   STG.E [R2.64], R4 ;                             /* 0x0000000402007986 */
        |                                                                                   /* 0x000fe8000c101904 */
        |        /*0480*/                   STG.E [R2.64+0x4], R5 ;                         /* 0x0000040502007986 */
        |                                                                                   /* 0x000fe2000c101904 */
        |        /*0490*/                   EXIT ;                                          /* 0x000000000000794d */
        |                                                                                   /* 0x000fea0003800000 */
        |.L_x_3:
        |""".stripMargin,
    "ldsmSm89" ->
      """//--------------------- .text.matrix_load         --------------------------
        |	.section	.text.matrix_load,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=30"
        |	.align	128
        |        .global         matrix_load
        |        .type           matrix_load,@function
        |        .size           matrix_load,(.L_x_1 - matrix_load)
        |        .other          matrix_load,@"STO_CUDA_ENTRY STV_DEFAULT"
        |matrix_load:
        |.text.matrix_load:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;    /* 0x00000a00ff017624 */
        |                                                                              /* 0x000fe400078e00ff */
        |        /*0010*/                   S2R R4, SR_TID.X ;                         /* 0x0000000000047919 */
        |                                                                              /* 0x000e220000002100 */
        |        /*0020*/                   ULDC.64 UR4, c[0x0][0x118] ;               /* 0x0000460000047ab9 */
        |                                                                              /* 0x000fe20000000a00 */
        |        /*0030*/                   IMAD.SHL.U32 R2, R4.reuse, 0x8, RZ ;       /* 0x0000000804027824 */
        |                                                                              /* 0x041fe200078e00ff */
        |        /*0040*/                   LEA R0, R4, c[0x0][0x168], 0x4 ;           /* 0x00005a0004007a11 */
        |                                                                              /* 0x000fc800078e20ff */
        |        /*0050*/                   IADD3 R3, R2.reuse, 0x1, RZ ;              /* 0x0000000102037810 */
        |                                                                              /* 0x040fe40007ffe0ff */
        |        /*0060*/                   IADD3 R5, R2.reuse, 0x2, RZ ;              /* 0x0000000202057810 */
        |                                                                              /* 0x040fe40007ffe0ff */
        |        /*0070*/                   IADD3 R7, R2.reuse, 0x3, RZ ;              /* 0x0000000302077810 */
        |                                                                              /* 0x040fe20007ffe0ff */
        |        /*0080*/                   STS.U16 [R0], R3 ;                         /* 0x0000000300007388 */
        |                                                                              /* 0x0001e20000000400 */
        |        /*0090*/                   IADD3 R9, R2.reuse, 0x4, RZ ;              /* 0x0000000402097810 */
        |                                                                              /* 0x040fe40007ffe0ff */
        |        /*00a0*/                   IADD3 R11, R2.reuse, 0x5, RZ ;             /* 0x00000005020b7810 */
        |                                                                              /* 0x040fe20007ffe0ff */
        |        /*00b0*/                   STS.U16 [R0+0x2], R5 ;                     /* 0x0000020500007388 */
        |                                                                              /* 0x000fe20000000400 */
        |        /*00c0*/                   IADD3 R13, R2, 0x6, RZ ;                   /* 0x00000006020d7810 */
        |                                                                              /* 0x000fc40007ffe0ff */
        |        /*00d0*/                   IADD3 R15, R2.reuse, 0x7, RZ ;             /* 0x00000007020f7810 */
        |                                                                              /* 0x040fe20007ffe0ff */
        |        /*00e0*/                   STS.U16 [R0+0x4], R7 ;                     /* 0x0000040700007388 */
        |                                                                              /* 0x000fe20000000400 */
        |        /*00f0*/                   IADD3 R17, R2, 0x8, RZ ;                   /* 0x0000000802117810 */
        |                                                                              /* 0x000fe20007ffe0ff */
        |        /*0100*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x2c ;            /* 0x0000002cff037424 */
        |                                                                              /* 0x001fe400078e00ff */
        |        /*0110*/                   STS.U16 [R0+0x6], R9 ;                     /* 0x0000060900007388 */
        |                                                                              /* 0x000fe40000000400 */
        |        /*0120*/                   IMAD.WIDE.U32 R2, R4, R3, c[0x0][0x160] ;  /* 0x0000580004027625 */
        |                                                                              /* 0x000fe400078e0003 */
        |        /*0130*/                   STS.U16 [R0+0x8], R11 ;                    /* 0x0000080b00007388 */
        |                                                                              /* 0x000fe80000000400 */
        |        /*0140*/                   STS.U16 [R0+0xa], R13 ;                    /* 0x00000a0d00007388 */
        |                                                                              /* 0x000fe80000000400 */
        |        /*0150*/                   STS.U16 [R0+0xc], R15 ;                    /* 0x00000c0f00007388 */
        |                                                                              /* 0x000fe80000000400 */
        |        /*0160*/                   STS.U16 [R0+0xe], R17 ;                    /* 0x00000e1100007388 */
        |                                                                              /* 0x000fe80000000400 */
        |        /*0170*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;              /* 0x0000000000007b1d */
        |                                                                              /* 0x000fec0000010000 */
        |        /*0180*/                   LDSM.16.M88.4 R20, [R0] ;                  /* 0x000000000014783b */
        |                                                                              /* 0x000e280000000200 */
        |        /*0190*/                   LDSM.16.M88.2 R6, [R0] ;                   /* 0x000000000006783b */
        |                                                                              /* 0x000e680000000100 */
        |        /*01a0*/                   LDSM.16.M88 R5, [R0] ;                     /* 0x000000000005783b */
        |                                                                              /* 0x000ea80000000000 */
        |        /*01b0*/                   LDSM.16.MT88.4 R24, [R0] ;                 /* 0x000000000018783b */
        |                                                                              /* 0x000ee80000004200 */
        |        /*01c0*/                   STG.E [R2.64+0xc], R20 ;                   /* 0x00000c1402007986 */
        |                                                                              /* 0x001fe8000c101904 */
        |        /*01d0*/                   STG.E [R2.64+0x10], R21 ;                  /* 0x0000101502007986 */
        |                                                                              /* 0x000fe8000c101904 */
        |        /*01e0*/                   STG.E [R2.64+0x14], R22 ;                  /* 0x0000141602007986 */
        |                                                                              /* 0x000fe8000c101904 */
        |        /*01f0*/                   STG.E [R2.64+0x18], R23 ;                  /* 0x0000181702007986 */
        |                                                                              /* 0x000fe8000c101904 */
        |        /*0200*/                   STG.E [R2.64+0x4], R6 ;                    /* 0x0000040602007986 */
        |                                                                              /* 0x002fe8000c101904 */
        |        /*0210*/                   STG.E [R2.64+0x8], R7 ;                    /* 0x0000080702007986 */
        |                                                                              /* 0x000fe8000c101904 */
        |        /*0220*/                   STG.E [R2.64], R5 ;                        /* 0x0000000502007986 */
        |                                                                              /* 0x004fe8000c101904 */
        |        /*0230*/                   STG.E [R2.64+0x1c], R24 ;                  /* 0x00001c1802007986 */
        |                                                                              /* 0x008fe8000c101904 */
        |        /*0240*/                   STG.E [R2.64+0x20], R25 ;                  /* 0x0000201902007986 */
        |                                                                              /* 0x000fe8000c101904 */
        |        /*0250*/                   STG.E [R2.64+0x24], R26 ;                  /* 0x0000241a02007986 */
        |                                                                              /* 0x000fe8000c101904 */
        |        /*0260*/                   STG.E [R2.64+0x28], R27 ;                  /* 0x0000281b02007986 */
        |                                                                              /* 0x000fe2000c101904 */
        |        /*0270*/                   EXIT ;                                     /* 0x000000000000794d */
        |                                                                              /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "ldsmUniformSm89Patched" ->
      """//--------------------- .text.matrix_load         --------------------------
        |	.section	.text.matrix_load,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=30"
        |	.align	128
        |        .global         matrix_load
        |        .type           matrix_load,@function
        |        .size           matrix_load,(.L_x_1 - matrix_load)
        |        .other          matrix_load,@"STO_CUDA_ENTRY STV_DEFAULT"
        |matrix_load:
        |.text.matrix_load:
        |        /*0000*/                   UMOV UR6, 0x10 ;                           /* 0x0000001000067882 */
        |                                                                              /* 0x000fe40000000000 */
        |        /*0010*/                   S2R R4, SR_TID.X ;                         /* 0x0000000000047919 */
        |                                                                              /* 0x000e220000002100 */
        |        /*0020*/                   ULDC.64 UR4, c[0x0][0x118] ;               /* 0x0000460000047ab9 */
        |                                                                              /* 0x000fe20000000a00 */
        |        /*0030*/                   IMAD.SHL.U32 R2, R4.reuse, 0x8, RZ ;       /* 0x0000000804027824 */
        |                                                                              /* 0x041fe200078e00ff */
        |        /*0040*/                   LEA R0, R4, c[0x0][0x168], 0x4 ;           /* 0x00005a0004007a11 */
        |                                                                              /* 0x000fc800078e20ff */
        |        /*0050*/                   IADD3 R3, R2.reuse, 0x1, RZ ;              /* 0x0000000102037810 */
        |                                                                              /* 0x040fe40007ffe0ff */
        |        /*0060*/                   IADD3 R5, R2.reuse, 0x2, RZ ;              /* 0x0000000202057810 */
        |                                                                              /* 0x040fe40007ffe0ff */
        |        /*0070*/                   IADD3 R7, R2.reuse, 0x3, RZ ;              /* 0x0000000302077810 */
        |                                                                              /* 0x040fe20007ffe0ff */
        |        /*0080*/                   STS.U16 [R0], R3 ;                         /* 0x0000000300007388 */
        |                                                                              /* 0x0001e20000000400 */
        |        /*0090*/                   IADD3 R9, R2.reuse, 0x4, RZ ;              /* 0x0000000402097810 */
        |                                                                              /* 0x040fe40007ffe0ff */
        |        /*00a0*/                   IADD3 R11, R2.reuse, 0x5, RZ ;             /* 0x00000005020b7810 */
        |                                                                              /* 0x040fe20007ffe0ff */
        |        /*00b0*/                   STS.U16 [R0+0x2], R5 ;                     /* 0x0000020500007388 */
        |                                                                              /* 0x000fe20000000400 */
        |        /*00c0*/                   IADD3 R13, R2, 0x6, RZ ;                   /* 0x00000006020d7810 */
        |                                                                              /* 0x000fc40007ffe0ff */
        |        /*00d0*/                   IADD3 R15, R2.reuse, 0x7, RZ ;             /* 0x00000007020f7810 */
        |                                                                              /* 0x040fe20007ffe0ff */
        |        /*00e0*/                   STS.U16 [R0+0x4], R7 ;                     /* 0x0000040700007388 */
        |                                                                              /* 0x000fe20000000400 */
        |        /*00f0*/                   IADD3 R17, R2, 0x8, RZ ;                   /* 0x0000000802117810 */
        |                                                                              /* 0x000fe20007ffe0ff */
        |        /*0100*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x2c ;            /* 0x0000002cff037424 */
        |                                                                              /* 0x001fe400078e00ff */
        |        /*0110*/                   STS.U16 [R0+0x6], R9 ;                     /* 0x0000060900007388 */
        |                                                                              /* 0x000fe40000000400 */
        |        /*0120*/                   IMAD.WIDE.U32 R2, R4, R3, c[0x0][0x160] ;  /* 0x0000580004027625 */
        |                                                                              /* 0x000fe400078e0003 */
        |        /*0130*/                   STS.U16 [R0+0x8], R11 ;                    /* 0x0000080b00007388 */
        |                                                                              /* 0x000fe80000000400 */
        |        /*0140*/                   STS.U16 [R0+0xa], R13 ;                    /* 0x00000a0d00007388 */
        |                                                                              /* 0x000fe80000000400 */
        |        /*0150*/                   STS.U16 [R0+0xc], R15 ;                    /* 0x00000c0f00007388 */
        |                                                                              /* 0x000fe80000000400 */
        |        /*0160*/                   STS.U16 [R0+0xe], R17 ;                    /* 0x00000e1100007388 */
        |                                                                              /* 0x000fe80000000400 */
        |        /*0170*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;              /* 0x0000000000007b1d */
        |                                                                              /* 0x000fec0000010000 */
        |        /*0180*/                   LDSM.16.M88.4 R20, [R0+UR6+-0x10] ;        /* 0xfffff0060014783b */
        |                                                                              /* 0x000e280008000200 */
        |        /*0190*/                   LDSM.16.M88.2 R6, [R0+UR6+-0x10] ;         /* 0xfffff0060006783b */
        |                                                                              /* 0x000e680008000100 */
        |        /*01a0*/                   LDSM.16.M88 R5, [R0+UR6+-0x10] ;           /* 0xfffff0060005783b */
        |                                                                              /* 0x000ea80008000000 */
        |        /*01b0*/                   LDSM.16.MT88.4 R24, [R0+UR6+-0x10] ;       /* 0xfffff0060018783b */
        |                                                                              /* 0x000ee80008004200 */
        |        /*01c0*/                   STG.E [R2.64+0xc], R20 ;                   /* 0x00000c1402007986 */
        |                                                                              /* 0x001fe8000c101904 */
        |        /*01d0*/                   STG.E [R2.64+0x10], R21 ;                  /* 0x0000101502007986 */
        |                                                                              /* 0x000fe8000c101904 */
        |        /*01e0*/                   STG.E [R2.64+0x14], R22 ;                  /* 0x0000141602007986 */
        |                                                                              /* 0x000fe8000c101904 */
        |        /*01f0*/                   STG.E [R2.64+0x18], R23 ;                  /* 0x0000181702007986 */
        |                                                                              /* 0x000fe8000c101904 */
        |        /*0200*/                   STG.E [R2.64+0x4], R6 ;                    /* 0x0000040602007986 */
        |                                                                              /* 0x002fe8000c101904 */
        |        /*0210*/                   STG.E [R2.64+0x8], R7 ;                    /* 0x0000080702007986 */
        |                                                                              /* 0x000fe8000c101904 */
        |        /*0220*/                   STG.E [R2.64], R5 ;                        /* 0x0000000502007986 */
        |                                                                              /* 0x004fe8000c101904 */
        |        /*0230*/                   STG.E [R2.64+0x1c], R24 ;                  /* 0x00001c1802007986 */
        |                                                                              /* 0x008fe8000c101904 */
        |        /*0240*/                   STG.E [R2.64+0x20], R25 ;                  /* 0x0000201902007986 */
        |                                                                              /* 0x000fe8000c101904 */
        |        /*0250*/                   STG.E [R2.64+0x24], R26 ;                  /* 0x0000241a02007986 */
        |                                                                              /* 0x000fe8000c101904 */
        |        /*0260*/                   STG.E [R2.64+0x28], R27 ;                  /* 0x0000281b02007986 */
        |                                                                              /* 0x000fe2000c101904 */
        |        /*0270*/                   EXIT ;                                     /* 0x000000000000794d */
        |                                                                              /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "tensorSm86" ->
      """//--------------------- .text._ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf --------------------------
        |	.section	.text._ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=26"
        |	.align	128
        |        .global         _ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf
        |        .type           _ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf,@function
        |        .size           _ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf,(.L_x_1 - _ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf)
        |        .other          _ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf,@"STO_CUDA_ENTRY STV_DEFAULT"
        |_ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf:
        |.text._ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   MOV R7, 0x2 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   MOV R13, 0x4 ;
        |        /*0050*/                   S2R R3, SR_CTAID.X ;
        |        /*0060*/                   S2R R5, SR_LANEID ;
        |        /*0070*/                   SHF.R.U32.HI R0, RZ, 0x5, R0 ;
        |        /*0080*/                   LEA R0, R3, R0, 0x1 ;
        |        /*0090*/                   MOV R3, RZ ;
        |        /*00a0*/                   LOP3.LUT R2, R5, 0x3, RZ, 0xc0, !PT ;
        |        /*00b0*/                   SHF.R.U32.HI R5, RZ, 0x2, R5 ;
        |        /*00c0*/                   SHF.L.U32 R0, R0, 0x9, RZ ;
        |        /*00d0*/                   IMAD.WIDE.U32 R4, R5, 0x10, R2 ;
        |        /*00e0*/                   IMAD.WIDE.U32 R2, R0, R7, c[0x0][0x160] ;
        |        /*00f0*/                   IMAD.WIDE.U32 R6, R0, R7, c[0x0][0x168] ;
        |        /*0100*/                   LEA R8, P0, R4, R2, 0x2 ;
        |        /*0110*/                   IMAD.WIDE.U32 R12, R0, R13, c[0x0][0x170] ;
        |        /*0120*/                   LEA R10, P1, R4.reuse, R6, 0x2 ;
        |        /*0130*/                   LEA.HI.X R9, R4.reuse, R3, R5.reuse, 0x2, P0 ;
        |        /*0140*/                   LEA R2, P2, R4.reuse, R12, 0x3 ;
        |        /*0150*/                   LEA.HI.X R11, R4.reuse, R7, R5.reuse, 0x2, P1 ;
        |        /*0160*/                   LDG.E R6, [R8.64+0x10] ;
        |        /*0170*/                   LEA.HI.X R3, R4, R13, R5, 0x3, P2 ;
        |        /*0180*/                   LDG.E R4, [R8.64] ;
        |        /*0190*/                   LDG.E R5, [R8.64+0x200] ;
        |        /*01a0*/                   LDG.E R7, [R8.64+0x210] ;
        |        /*01b0*/                   LDG.E R12, [R10.64] ;
        |        /*01c0*/                   LDG.E R13, [R10.64+0x10] ;
        |        /*01d0*/                   LDG.E.64 R16, [R2.64] ;
        |        /*01e0*/                   LDG.E.64 R18, [R2.64+0x400] ;
        |        /*01f0*/                   LDG.E R14, [R10.64+0x200] ;
        |        /*0200*/                   LDG.E R15, [R10.64+0x210] ;
        |        /*0210*/                   LDG.E.64 R20, [R2.64+0x20] ;
        |        /*0220*/                   LDG.E.64 R22, [R2.64+0x420] ;
        |        /*0230*/                   HMMA.16816.F32 R16, R4.reuse, R12, R16 ;
        |        /*0240*/                   HMMA.16816.F32 R20, R4, R14, R20 ;
        |        /*0250*/                   @!UPT UIADD3 URZ, URZ, URZ, URZ ;
        |        /*0260*/                   FADD R16, R16, R16 ;
        |        /*0270*/                   FADD R17, R17, R17 ;
        |        /*0280*/                   FADD R18, R18, R18 ;
        |        /*0290*/                   FADD R19, R19, R19 ;
        |        /*02a0*/                   STG.E.64 [R2.64], R16 ;
        |        /*02b0*/                   STG.E.64 [R2.64+0x400], R18 ;
        |        /*02c0*/                   FADD R20, R20, R20 ;
        |        /*02d0*/                   FADD R21, R21, R21 ;
        |        /*02e0*/                   FADD R22, R22, R22 ;
        |        /*02f0*/                   FADD R23, R23, R23 ;
        |        /*0300*/                   STG.E.64 [R2.64+0x20], R20 ;
        |        /*0310*/                   STG.E.64 [R2.64+0x420], R22 ;
        |        /*0320*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin
  )
}
