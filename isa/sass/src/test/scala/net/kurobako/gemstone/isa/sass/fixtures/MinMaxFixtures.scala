package net.kurobako.gemstone.isa.sass.fixtures

object MinMaxFixtures {
  val extremaPtx: String =
    """.version 9.2
      |.target sm_89
      |.address_size 64
      |.visible .entry extrema(.param .u64 param_a, .param .u64 param_b, .param .u64 param_max, .param .u64 param_min) {
      |.reg .pred %p;
      |.reg .b32 %thread;
      |.reg .b64 %a, %b, %max, %min, %pa, %pb, %pmax, %pmin, %offset;
      |.reg .b64 %tmp;
      |ld.param.u64 %pa, [param_a];
      |ld.param.u64 %pb, [param_b];
      |ld.param.u64 %pmax, [param_max];
      |ld.param.u64 %pmin, [param_min];
      |mov.u32 %thread, %tid.x;
      |setp.ge.u32 %p, %thread, 9;
      |@%p ret;
      |cvt.u64.u32 %offset, %thread;
      |shl.b64 %offset, %offset, 3;
      |add.u64 %pa, %pa, %offset;
      |add.u64 %pb, %pb, %offset;
      |add.u64 %pmax, %pmax, %offset;
      |add.u64 %pmin, %pmin, %offset;
      |ld.global.b64 %a, [%pa];
      |ld.global.b64 %b, [%pb];
      |max.f64 %max, %a, %b;
      |min.f64 %min, %a, %b;
      |st.global.b64 [%pmax], %max;
      |st.global.b64 [%pmin], %min;
      |ret;
      |}
      |""".stripMargin

  val fmnmxPtx: String =
    """.version 8.0
      |.target sm_86
      |.address_size 64
      |.visible .entry probe(.param .u64 output, .param .u64 input_a, .param .u64 input_b) {
      |.reg .b32 %r<2>;
      |.reg .b64 %rd<4>;
      |.reg .f32 %f<4>;
      |.reg .pred %p;
      |ld.param.u64 %rd0, [output];
      |ld.param.u64 %rd1, [input_a];
      |ld.param.u64 %rd2, [input_b];
      |mov.u32 %r0, %tid.x;
      |mad.wide.u32 %rd3, %r0, 4, %rd1;
      |ld.global.b32 %r1, [%rd3];
      |mov.b32 %f0, %r1;
      |mad.wide.u32 %rd3, %r0, 4, %rd2;
      |ld.global.b32 %r1, [%rd3];
      |mov.b32 %f1, %r1;
      |min.f32 %f2, %f0, %f1;
      |max.f32 %f3, %f0, %f1;
      |mad.wide.u32 %rd3, %r0, 8, %rd0;
      |st.global.b32 [%rd3], %f2;
      |add.u64 %rd3, %rd3, 4;
      |st.global.b32 [%rd3], %f3;
      |ret;
      |}
      |""".stripMargin

  val minmaxCuda: String =
    """#include <stdint.h>
      |extern "C" __global__ void minmax_probe(uint64_t *out, uint64_t a, uint64_t b) {
      |  uint64_t mn = a < b ? a : b;
      |  uint64_t mx = a > b ? a : b;
      |  out[0] = mn;
      |  out[1] = mx;
      |}
      |extern "C" __global__ void minmax_signed_probe(int64_t *out, int64_t a, int64_t b) {
      |  int64_t mn = a < b ? a : b;
      |  int64_t mx = a > b ? a : b;
      |  out[0] = mn;
      |  out[1] = mx;
      |}
      |""".stripMargin

  val videoMinMaxNestedCuda: String =
    """#include <cuda_runtime.h>
      |extern "C" __global__ void nested_min(const int* a, const int* b, const int* c, int* out) {
      |  const unsigned i = blockIdx.x * blockDim.x + threadIdx.x;
      |  out[i] = min(min(a[i], b[i]), c[i]);
      |}
      |extern "C" __global__ void nested_max(const int* a, const int* b, const int* c, int* out) {
      |  const unsigned i = blockIdx.x * blockDim.x + threadIdx.x;
      |  out[i] = max(max(a[i], b[i]), c[i]);
      |}
      |""".stripMargin

  val videoMinMaxPtx: String =
    """.version 7.8
      |.target sm_35
      |.address_size 64
      |.visible .entry video_minmax(.param .u64 out, .param .u32 a, .param .u32 b, .param .u32 c) {
      |  .reg .b64 %p;
      |  .reg .b32 %r<15>;
      |  ld.param.u64 %p, [out];
      |  ld.param.u32 %r0, [a];
      |  ld.param.u32 %r1, [b];
      |  ld.param.u32 %r2, [c];
      |  vmin.s32.s32.s32.min %r3, %r0, %r1, %r2;
      |  vmin.s32.s32.s32.max %r4, %r0, %r1, %r2;
      |  vmin.s32.s32.s32.add %r5, %r0, %r1, %r2;
      |  vmax.s32.s32.s32.min %r6, %r0, %r1, %r2;
      |  vmax.s32.s32.s32.max %r7, %r0, %r1, %r2;
      |  vmax.s32.s32.s32.add %r8, %r0, %r1, %r2;
      |  vmin.u32.u32.u32.min %r9, %r0, %r1, %r2;
      |  vmin.u32.u32.u32.max %r10, %r0, %r1, %r2;
      |  vmin.u32.u32.u32.add %r11, %r0, %r1, %r2;
      |  vmax.u32.u32.u32.min %r12, %r0, %r1, %r2;
      |  vmax.u32.u32.u32.max %r13, %r0, %r1, %r2;
      |  vmax.u32.u32.u32.add %r14, %r0, %r1, %r2;
      |  st.global.u32 [%p+24], %r9;
      |  st.global.u32 [%p+28], %r10;
      |  st.global.u32 [%p+32], %r11;
      |  st.global.u32 [%p+36], %r12;
      |  st.global.u32 [%p+40], %r13;
      |  st.global.u32 [%p+44], %r14;
      |  st.global.u32 [%p], %r3;
      |  st.global.u32 [%p+4], %r4;
      |  st.global.u32 [%p+8], %r5;
      |  st.global.u32 [%p+12], %r6;
      |  st.global.u32 [%p+16], %r7;
      |  st.global.u32 [%p+20], %r8;
      |  ret;
      |}
      |""".stripMargin

  val listings: Map[String, String] = Map(
    "extremaSm89" ->
      """//--------------------- .text.extrema             --------------------------
        |	.section	.text.extrema,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=17"
        |	.align	128
        |        .global         extrema
        |        .type           extrema,@function
        |        .size           extrema,(.L_x_1 - extrema)
        |        .other          extrema,@"STO_CUDA_ENTRY STV_DEFAULT"
        |extrema:
        |.text.extrema:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   ISETP.GE.U32.AND P0, PT, R0, 0x9, PT ;
        |        /*0030*/               @P0 EXIT ;
        |        /*0040*/                   IMAD.SHL.U32 R8, R0, 0x8, RZ ;
        |        /*0050*/                   SHF.R.U32.HI R0, RZ, 0x1d, R0 ;
        |        /*0060*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0070*/                   IADD3 R2, P0, R8.reuse, c[0x0][0x160], RZ ;
        |        /*0080*/                   IADD3 R4, P1, R8, c[0x0][0x168], RZ ;
        |        /*0090*/                   IADD3.X R3, R0.reuse, c[0x0][0x164], RZ, P0, !PT ;
        |        /*00a0*/                   IADD3.X R5, R0, c[0x0][0x16c], RZ, P1, !PT ;
        |        /*00b0*/                   LDG.E.64 R2, [R2.64] ;
        |        /*00c0*/                   LDG.E.64 R4, [R4.64] ;
        |        /*00d0*/                   IMAD.MOV.U32 R11, RZ, RZ, R2.reuse ;
        |        /*00e0*/                   IMAD.MOV.U32 R6, RZ, RZ, R2 ;
        |        /*00f0*/                   DSETP.MIN.AND P4, P5, R2.reuse, R4.reuse, PT ;
        |        /*0100*/                   IMAD.MOV.U32 R14, RZ, RZ, R4.reuse ;
        |        /*0110*/                   IMAD.MOV.U32 R12, RZ, RZ, R3.reuse ;
        |        /*0120*/                   DSETP.MAX.AND P2, P3, R2, R4, PT ;
        |        /*0130*/                   IMAD.MOV.U32 R9, RZ, RZ, R4 ;
        |        /*0140*/                   SEL R4, R11, R14, P4 ;
        |        /*0150*/                   IMAD.MOV.U32 R7, RZ, RZ, R3 ;
        |        /*0160*/                   FSEL R11, R12, R5, P4 ;
        |        /*0170*/                   IMAD.MOV.U32 R10, RZ, RZ, R5 ;
        |        /*0180*/                   SEL R2, R6, R9, P2 ;
        |        /*0190*/                   IADD3 R6, P0, R8.reuse, c[0x0][0x170], RZ ;
        |        /*01a0*/               @P5 LOP3.LUT R11, R5, 0x80000, RZ, 0xfc, !PT ;
        |        /*01b0*/                   IADD3 R8, P1, R8, c[0x0][0x178], RZ ;
        |        /*01c0*/                   FSEL R3, R7, R10, P2 ;
        |        /*01d0*/                   IMAD.MOV.U32 R5, RZ, RZ, R11 ;
        |        /*01e0*/                   IADD3.X R7, R0, c[0x0][0x174], RZ, P0, !PT ;
        |        /*01f0*/               @P3 LOP3.LUT R3, R10, 0x80000, RZ, 0xfc, !PT ;
        |        /*0200*/                   IADD3.X R9, R0, c[0x0][0x17c], RZ, P1, !PT ;
        |        /*0210*/                   STG.E.64 [R6.64], R2 ;
        |        /*0220*/                   STG.E.64 [R8.64], R4 ;
        |        /*0230*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "fmnmxSm86" ->
      """//--------------------- .text.probe               --------------------------
        |	.section	.text.probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         probe
        |        .type           probe,@function
        |        .size           probe,(.L_x_1 - probe)
        |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |probe:
        |.text.probe:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;             /* 0x00000a00ff017624 */
        |                                                                                       /* 0x000fe400078e00ff */
        |        /*0010*/                   S2R R3, SR_TID.X ;                                  /* 0x0000000000037919 */
        |                                                                                       /* 0x000e220000002100 */
        |        /*0020*/                   ULDC.64 UR4, c[0x0][0x118] ;                        /* 0x0000460000047ab9 */
        |                                                                                       /* 0x000fe20000000a00 */
        |        /*0030*/                   IMAD.SHL.U32 R6, R3.reuse, 0x4, RZ ;                /* 0x0000000403067824 */
        |                                                                                       /* 0x041fe400078e00ff */
        |        /*0040*/                   IMAD.HI.U32 R0, R3, 0x4, RZ ;                       /* 0x0000000403007827 */
        |                                                                                       /* 0x000fc600078e00ff */
        |        /*0050*/                   IADD3 R4, P0, R6.reuse, c[0x0][0x168], RZ ;         /* 0x00005a0006047a10 */
        |                                                                                       /* 0x040fe40007f1e0ff */
        |        /*0060*/                   IADD3 R6, P1, R6, c[0x0][0x170], RZ ;               /* 0x00005c0006067a10 */
        |                                                                                       /* 0x000fe40007f3e0ff */
        |        /*0070*/                   IADD3.X R5, R0.reuse, c[0x0][0x16c], RZ, P0, !PT ;  /* 0x00005b0000057a10 */
        |                                                                                       /* 0x040fe400007fe4ff */
        |        /*0080*/                   IADD3.X R7, R0, c[0x0][0x174], RZ, P1, !PT ;        /* 0x00005d0000077a10 */
        |                                                                                       /* 0x000fc60000ffe4ff */
        |        /*0090*/                   LDG.E R4, [R4.64] ;                                 /* 0x0000000404047981 */
        |                                                                                       /* 0x000ea8000c1e1900 */
        |        /*00a0*/                   LDG.E R7, [R6.64] ;                                 /* 0x0000000406077981 */
        |                                                                                       /* 0x000ea2000c1e1900 */
        |        /*00b0*/                   LEA R2, P0, R3, c[0x0][0x160], 0x3 ;                /* 0x0000580003027a11 */
        |                                                                                       /* 0x000fc800078018ff */
        |        /*00c0*/                   LEA.HI.X R3, R3, c[0x0][0x164], RZ, 0x3, P0 ;       /* 0x0000590003037a11 */
        |                                                                                       /* 0x000fe400000f1cff */
        |        /*00d0*/                   FMNMX R9, R4.reuse, R7.reuse, PT ;                  /* 0x0000000704097209 */
        |                                                                                       /* 0x0c4fe40003800000 */
        |        /*00e0*/                   FMNMX R11, R4, R7, !PT ;                            /* 0x00000007040b7209 */
        |                                                                                       /* 0x000fc60007800000 */
        |        /*00f0*/                   STG.E [R2.64], R9 ;                                 /* 0x0000000902007986 */
        |                                                                                       /* 0x000fe8000c101904 */
        |        /*0100*/                   STG.E [R2.64+0x4], R11 ;                            /* 0x0000040b02007986 */
        |                                                                                       /* 0x000fe2000c101904 */
        |        /*0110*/                   EXIT ;                                              /* 0x000000000000794d */
        |                                                                                       /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "minmaxSignedSm35" ->
      """//--------------------- .text.minmax_signed_probe --------------------------
        |	.section	.text.minmax_signed_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	64
        |        .global         minmax_signed_probe
        |        .type           minmax_signed_probe,@function
        |        .size           minmax_signed_probe,(.L_x_1 - minmax_signed_probe)
        |        .other          minmax_signed_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |minmax_signed_probe:
        |.text.minmax_signed_probe:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x14c];
        |        /*0018*/                   MOV R0, c[0x0][0x148];
        |        /*0020*/                   MOV R2, c[0x0][0x140];
        |        /*0028*/                   MOV R3, c[0x0][0x144];
        |        /*0030*/                   IMNMX.XHI R5.CC, R6, c[0x0][0x154], PT;
        |        /*0038*/                   IMNMX.U32.XLO R4, R0, c[0x0][0x150], PT;
        |        /*0048*/                   IMNMX.XHI R7.CC, R6, c[0x0][0x154], !PT;
        |        /*0050*/                   ST.E.64 [R2], R4;
        |        /*0058*/                   IMNMX.U32.XLO R6, R0, c[0x0][0x150], !PT;
        |        /*0060*/                   ST.E.64 [R2+0x8], R6;
        |        /*0068*/                   EXIT;
        |.L_x_1:
        |""".stripMargin,
    "minmaxSm35" ->
      """//--------------------- .text.minmax_probe        --------------------------
        |	.section	.text.minmax_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	64
        |        .global         minmax_probe
        |        .type           minmax_probe,@function
        |        .size           minmax_probe,(.L_x_1 - minmax_probe)
        |        .other          minmax_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |minmax_probe:
        |.text.minmax_probe:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x14c];
        |        /*0018*/                   MOV R0, c[0x0][0x148];
        |        /*0020*/                   MOV R2, c[0x0][0x140];
        |        /*0028*/                   MOV R3, c[0x0][0x144];
        |        /*0030*/                   IMNMX.U32.XHI R5.CC, R6, c[0x0][0x154], PT;
        |        /*0038*/                   IMNMX.U32.XLO R4, R0, c[0x0][0x150], PT;
        |        /*0048*/                   IMNMX.U32.XHI R7.CC, R6, c[0x0][0x154], !PT;
        |        /*0050*/                   ST.E.64 [R2], R4;
        |        /*0058*/                   IMNMX.U32.XLO R6, R0, c[0x0][0x150], !PT;
        |        /*0060*/                   ST.E.64 [R2+0x8], R6;
        |        /*0068*/                   EXIT;
        |.L_x_1:
        |""".stripMargin,
    "videoMinMaxNestedMaxSm50" ->
      """//--------------------- .text.nested_max          --------------------------
        |	.section	.text.nested_max,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=11"
        |	.align	32
        |        .global         nested_max
        |        .type           nested_max,@function
        |        .size           nested_max,(.L_x_1 - nested_max)
        |        .other          nested_max,@"STO_CUDA_ENTRY STV_DEFAULT"
        |nested_max:
        |.text.nested_max:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R0, SR_CTAID.X ;
        |        /*0018*/                   S2R R2, SR_TID.X ;
        |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
        |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
        |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
        |        /*0048*/                   SHL R8, R0.reuse, 0x2 ;
        |        /*0050*/                   SHR.U32 R0, R0, 0x1e ;
        |        /*0058*/                   IADD R2.CC, R8.reuse, c[0x0][0x140] ;
        |        /*0068*/                   IADD.X R3, R0.reuse, c[0x0][0x144] ;
        |        /*0070*/         {         IADD R4.CC, R8.reuse, c[0x0][0x148] ;
        |        /*0078*/                   LDG.E R2, [R2]         }
        |        /*0088*/                   IADD.X R5, R0.reuse, c[0x0][0x14c] ;
        |        /*0090*/         {         IADD R6.CC, R8, c[0x0][0x150] ;
        |        /*0098*/                   LDG.E R4, [R4]         }
        |        /*00a8*/                   IADD.X R7, R0, c[0x0][0x154] ;
        |        /*00b0*/                   LDG.E R7, [R6] ;
        |        /*00b8*/                   IADD R8.CC, R8, c[0x0][0x158] ;
        |        /*00c8*/                   IADD.X R9, R0, c[0x0][0x15c] ;
        |        /*00d0*/                   VMNMX.MX.MAX R10, R2, R4, R7 ;
        |        /*00d8*/                   STG.E [R8], R10 ;
        |        /*00e8*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "videoMinMaxNestedMinSm50" ->
      """//--------------------- .text.nested_min          --------------------------
        |	.section	.text.nested_min,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=11"
        |	.align	32
        |        .global         nested_min
        |        .type           nested_min,@function
        |        .size           nested_min,(.L_x_1 - nested_min)
        |        .other          nested_min,@"STO_CUDA_ENTRY STV_DEFAULT"
        |nested_min:
        |.text.nested_min:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R0, SR_CTAID.X ;
        |        /*0018*/                   S2R R2, SR_TID.X ;
        |        /*0028*/                   XMAD.MRG R3, R0.reuse, c[0x0] [0x8].H1, RZ ;
        |        /*0030*/                   XMAD R2, R0.reuse, c[0x0] [0x8], R2 ;
        |        /*0038*/                   XMAD.PSL.CBCC R0, R0.H1, R3.H1, R2 ;
        |        /*0048*/                   SHL R8, R0.reuse, 0x2 ;
        |        /*0050*/                   SHR.U32 R0, R0, 0x1e ;
        |        /*0058*/                   IADD R2.CC, R8.reuse, c[0x0][0x140] ;
        |        /*0068*/                   IADD.X R3, R0.reuse, c[0x0][0x144] ;
        |        /*0070*/         {         IADD R4.CC, R8.reuse, c[0x0][0x148] ;
        |        /*0078*/                   LDG.E R2, [R2]         }
        |        /*0088*/                   IADD.X R5, R0.reuse, c[0x0][0x14c] ;
        |        /*0090*/         {         IADD R6.CC, R8, c[0x0][0x150] ;
        |        /*0098*/                   LDG.E R4, [R4]         }
        |        /*00a8*/                   IADD.X R7, R0, c[0x0][0x154] ;
        |        /*00b0*/                   LDG.E R7, [R6] ;
        |        /*00b8*/                   IADD R8.CC, R8, c[0x0][0x158] ;
        |        /*00c8*/                   IADD.X R9, R0, c[0x0][0x15c] ;
        |        /*00d0*/                   VMNMX.MIN R10, R2, R4, R7 ;
        |        /*00d8*/                   STG.E [R8], R10 ;
        |        /*00e8*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "videoMinMaxSm35" ->
      """//--------------------- .text.video_minmax        --------------------------
        |	.section	.text.video_minmax,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=15"
        |	.align	64
        |        .global         video_minmax
        |        .type           video_minmax,@function
        |        .size           video_minmax,(.L_x_1 - video_minmax)
        |        .other          video_minmax,@"STO_CUDA_ENTRY STV_DEFAULT"
        |video_minmax:
        |.text.video_minmax:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R0, c[0x0][0x148];
        |        /*0018*/                   MOV R13, c[0x0][0x14c];
        |        /*0020*/                   MOV R14, c[0x0][0x150];
        |        /*0028*/                   MOV R2, c[0x0][0x140];
        |        /*0030*/                   MOV R3, c[0x0][0x144];
        |        /*0038*/                   VMNMX.MIN R4, R0, R13, R14;
        |        /*0048*/                   VMNMX.MAX R5, R0, R13, R14;
        |        /*0050*/                   ST.E [R2], R4;
        |        /*0058*/                   VMNMX.ACC R6, R0, R13, R14;
        |        /*0060*/                   VMNMX.MX.MIN R7, R0, R13, R14;
        |        /*0068*/                   VMNMX.MX.MAX R9, R0, R13, R14;
        |        /*0070*/                   ST.E [R2+0x4], R5;
        |        /*0078*/                   VMNMX.UD.U32.U32.MIN R11, R0, R13, R14;
        |        /*0088*/                   VMNMX.UD.U32.U32.MAX R12, R0, R13, R14;
        |        /*0090*/                   VMNMX.MX.ACC R4, R0, R13, R14;
        |        /*0098*/                   ST.E [R2+0x8], R6;
        |        /*00a0*/                   VMNMX.UD.U32.U32.ACC R5, R0, R13, R14;
        |        /*00a8*/                   ST.E [R2+0xc], R7;
        |        /*00b0*/                   VMNMX.UD.U32.U32.MX.MIN R6, R0, R13, R14;
        |        /*00b8*/                   ST.E [R2+0x10], R9;
        |        /*00c8*/                   VMNMX.UD.U32.U32.MX.MAX R7, R0, R13, R14;
        |        /*00d0*/                   VMNMX.UD.U32.U32.MX.ACC R9, R0, R13, R14;
        |        /*00d8*/                   ST.E [R2+0x18], R11;
        |        /*00e0*/                   ST.E [R2+0x1c], R12;
        |        /*00e8*/                   ST.E [R2+0x20], R5;
        |        /*00f0*/                   ST.E [R2+0x24], R6;
        |        /*00f8*/                   ST.E [R2+0x28], R7;
        |        /*0108*/                   ST.E [R2+0x2c], R9;
        |        /*0110*/                   ST.E [R2+0x14], R4;
        |        /*0118*/                   EXIT;
        |.L_x_1:
        |""".stripMargin
  )
}
