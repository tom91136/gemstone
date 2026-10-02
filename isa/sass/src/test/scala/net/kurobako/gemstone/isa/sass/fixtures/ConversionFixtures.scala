package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object ConversionFixtures {
  val f2fF16F32Sm75ProbeCudaSource: String =
    """#include <cuda_fp16.h>
      |#include <stdint.h>
      |extern "C" __global__ void probe(uint32_t *out, const uint32_t *in) {
      |  unsigned i = blockIdx.x;
      |  float x = __uint_as_float(in[i]);
      |  __half h = __float2half_rn(x);
      |  out[i] = static_cast<uint32_t>(*reinterpret_cast<uint16_t *>(&h));
      |}
      |""".stripMargin

  val f2fF32F64Sm70ProbePtxSource: String =
    """.version 7.8
      |.target sm_70
      |.address_size 64
      |
      |.visible .entry f2f_conversion_probe(
      |    .param .u64 out_f32,
      |    .param .u64 in_f64,
      |    .param .u64 out_f64,
      |    .param .u64 in_f32,
      |    .param .u32 count
      |)
      |{
      |    .reg .pred %p<2>;
      |    .reg .b32 %r<8>;
      |    .reg .b64 %rd<12>;
      |    .reg .f32 %f<3>;
      |    .reg .f64 %d<3>;
      |
      |    ld.param.u64 %rd1, [out_f32];
      |    ld.param.u64 %rd2, [in_f64];
      |    ld.param.u64 %rd3, [out_f64];
      |    ld.param.u64 %rd4, [in_f32];
      |    ld.param.u32 %r1, [count];
      |    mov.u32 %r2, %tid.x;
      |    setp.ge.u32 %p1, %r2, %r1;
      |    @%p1 bra DONE;
      |
      |    mul.wide.u32 %rd5, %r2, 8;
      |    add.u64 %rd6, %rd2, %rd5;
      |    ld.global.f64 %d1, [%rd6];
      |    cvt.rn.f32.f64 %f1, %d1;
      |    mul.wide.u32 %rd7, %r2, 4;
      |    add.u64 %rd8, %rd1, %rd7;
      |    st.global.f32 [%rd8], %f1;
      |
      |    add.u64 %rd9, %rd4, %rd7;
      |    ld.global.f32 %f2, [%rd9];
      |    cvt.f64.f32 %d2, %f2;
      |    add.u64 %rd10, %rd3, %rd5;
      |    st.global.f64 [%rd10], %d2;
      |DONE:
      |    ret;
      |}
      |""".stripMargin

  private def f2fF32F64Source(sm: Int): String =
    s""".version 8.7
      |.target sm_$sm
      |.address_size 64
      |
      |.visible .entry f2f_conversion_probe(
      |    .param .u64 out_f32,
      |    .param .u64 in_f64,
      |    .param .u64 out_f64,
      |    .param .u64 in_f32,
      |    .param .u32 count
      |)
      |{
      |    .reg .pred %p<2>;
      |    .reg .b32 %r<8>;
      |    .reg .b64 %rd<12>;
      |    .reg .f32 %f<3>;
      |    .reg .f64 %d<3>;
      |
      |    ld.param.u64 %rd1, [out_f32];
      |    ld.param.u64 %rd2, [in_f64];
      |    ld.param.u64 %rd3, [out_f64];
      |    ld.param.u64 %rd4, [in_f32];
      |    ld.param.u32 %r1, [count];
      |    mov.u32 %r2, %tid.x;
      |    setp.ge.u32 %p1, %r2, %r1;
      |    @%p1 bra DONE;
      |
      |    mul.wide.u32 %rd5, %r2, 8;
      |    add.u64 %rd6, %rd2, %rd5;
      |    ld.global.f64 %d1, [%rd6];
      |    cvt.rn.f32.f64 %f1, %d1;
      |    mul.wide.u32 %rd7, %r2, 4;
      |    add.u64 %rd8, %rd1, %rd7;
      |    st.global.f32 [%rd8], %f1;
      |
      |    add.u64 %rd9, %rd4, %rd7;
      |    ld.global.f32 %f2, [%rd9];
      |    cvt.f64.f32 %d2, %f2;
      |    add.u64 %rd10, %rd3, %rd5;
      |    st.global.f64 [%rd10], %d2;
      |DONE:
      |    ret;
      |}
      |""".stripMargin

  val f2fF32F64Sm75ProbePtxSource: String = f2fF32F64Source(75)

  val f2fF32F64Sm80ProbePtxSource: String = f2fF32F64Source(80)

  val f2fF32F64Sm86ProbePtxSource: String = f2fF32F64Source(86)

  val f2fF32F64Sm89ProbePtxSource: String = f2fF32F64Source(89)

  val f2fpPackAbSm89F2fpPackAbSm89CudaSource: String =
    """#include <cuda_fp16.h>
      |#include <stdint.h>
      |
      |extern "C" __global__ void f2fp_pack_ab_probe(
      |    const float *input, uint32_t *output) {
      |  const unsigned index = threadIdx.x;
      |  const float a = input[2 * index];
      |  const float b = input[2 * index + 1];
      |  const uint16_t a_half = __half_as_ushort(__float2half_rn(a));
      |  const uint16_t b_half = __half_as_ushort(__float2half_rn(b));
      |  output[index] = static_cast<uint32_t>(a_half) |
      |      (static_cast<uint32_t>(b_half) << 16);
      |}
      |""".stripMargin

  val i2fF16Sm80I2fF16Sm80CudaSource: String =
    """#include <stdint.h>
      |
      |extern "C" __global__ void i2f_f16_probe(const int32_t *input, uint32_t *output) {
      |  const unsigned index = threadIdx.x;
      |  uint32_t converted;
      |  asm volatile("cvt.rn.f16.s32 %0, %1;" : "=r"(converted) : "r"(input[index]));
      |  output[index] = converted;
      |}
      |
      |extern "C" __global__ void i2f_f16_immediate_probe(uint32_t *output) {
      |  uint32_t converted;
      |  asm volatile("cvt.rn.f16.s32 %0, 1;" : "=r"(converted));
      |  output[threadIdx.x] = converted;
      |}
      |""".stripMargin

  val i2fpSm86PtxSource: String =
    """.version 9.2
      |.target sm_86
      |.address_size 64
      |.visible .entry convert(.param .u64 output, .param .s32 input) {
      |.reg .b64 %p;
      |.reg .b32 %x, %a, %b, %c, %d;
      |ld.param.u64 %p,[output]; ld.param.s32 %x,[input];
      |cvt.rn.f32.s32 %a,%x; cvt.rm.f32.s32 %b,%x; cvt.rp.f32.s32 %c,%x; cvt.rz.f32.s32 %d,%x;
      |st.global.b32 [%p],%a; st.global.b32 [%p+4],%b; st.global.b32 [%p+8],%c; st.global.b32 [%p+12],%d; ret;
      |}
      |""".stripMargin

  val f2fF16F32Sm75ProbeSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                     /* 0x00000a0000017a02 */
      |                                                                              /* 0x000fc40000000f00 */
      |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;              /* 0x000000fffffff389 */
      |                                                                              /* 0x000fe200000e00ff */
      |        /*0020*/                   S2R R4, SR_CTAID.X ;                       /* 0x0000000000047919 */
      |                                                                              /* 0x000e220000002500 */
      |        /*0030*/                   MOV R5, 0x4 ;                              /* 0x0000000400057802 */
      |                                                                              /* 0x000fca0000000f00 */
      |        /*0040*/                   IMAD.WIDE.U32 R2, R4, R5, c[0x0][0x168] ;  /* 0x00005a0004027625 */
      |                                                                              /* 0x001fd000078e0005 */
      |        /*0050*/                   LDG.E.SYS R2, [R2] ;                       /* 0x0000000002027381 */
      |                                                                              /* 0x000ea200001ee900 */
      |        /*0060*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x160] ;  /* 0x0000580004047625 */
      |                                                                              /* 0x000fe200078e0005 */
      |        /*0070*/                   F2F.F16.F32 R7, R2 ;                       /* 0x0000000200077304 */
      |                                                                              /* 0x004e2e0000200800 */
      |        /*0080*/                   STG.E.SYS [R4], R7 ;                       /* 0x0000000704007386 */
      |                                                                              /* 0x001fe2000010e900 */
      |        /*0090*/                   EXIT ;                                     /* 0x000000000000794d */
      |                                                                              /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00a0*/                   BRA `(.L_x_0);                             /* 0xfffffff000007947 */
      |                                                                              /* 0x000fc0000383ffff */
      |        /*00b0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00c0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00d0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00e0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00f0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val f2fF16F32Sm75ProbeSm75: String =
    """	.target	sm_75
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=4"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;               /* 0x00000a0000017a02 */
      |                                                                        /* 0x000fe40000000f00 */
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
      |        /*0080*/                   F2F.F16.F32 R0, R0 ;                 /* 0x0000000000007304 */
      |                                                                        /* 0x004e300000200800 */
      |        /*0090*/                   STG.E.SYS [UR4], R0 ;                /* 0x00000000ff007986 */
      |                                                                        /* 0x001fe2000c10e904 */
      |        /*00a0*/                   EXIT ;                               /* 0x000000000000794d */
      |                                                                        /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00b0*/                   BRA `(.L_x_0);                       /* 0xfffffff000007947 */
      |                                                                        /* 0x000fc0000383ffff */
      |        /*00c0*/                   NOP;                                 /* 0x0000000000007918 */
      |                                                                        /* 0x000fc00000000000 */
      |        /*00d0*/                   NOP;                                 /* 0x0000000000007918 */
      |                                                                        /* 0x000fc00000000000 */
      |        /*00e0*/                   NOP;                                 /* 0x0000000000007918 */
      |                                                                        /* 0x000fc00000000000 */
      |        /*00f0*/                   NOP;                                 /* 0x0000000000007918 */
      |                                                                        /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val f2fF16F32Sm75ProbeSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                     /* 0x00000a0000017a02 */
      |                                                                              /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R4, SR_CTAID.X ;                       /* 0x0000000000047919 */
      |                                                                              /* 0x000e220000002500 */
      |        /*0020*/                   MOV R5, 0x4 ;                              /* 0x0000000400057802 */
      |                                                                              /* 0x000fe20000000f00 */
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;               /* 0x0000460000047ab9 */
      |                                                                              /* 0x000fc80000000a00 */
      |        /*0040*/                   IMAD.WIDE.U32 R2, R4, R5, c[0x0][0x168] ;  /* 0x00005a0004027625 */
      |                                                                              /* 0x001fcc00078e0005 */
      |        /*0050*/                   LDG.E R2, [R2.64] ;                        /* 0x0000000402027981 */
      |                                                                              /* 0x000ea2000c1e1900 */
      |        /*0060*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x160] ;  /* 0x0000580004047625 */
      |                                                                              /* 0x000fe200078e0005 */
      |        /*0070*/                   F2F.F16.F32 R7, R2 ;                       /* 0x0000000200077304 */
      |                                                                              /* 0x004e280000200800 */
      |        /*0080*/                   STG.E [R4.64], R7 ;                        /* 0x0000000704007986 */
      |                                                                              /* 0x001fe2000c101904 */
      |        /*0090*/                   EXIT ;                                     /* 0x000000000000794d */
      |                                                                              /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00a0*/                   BRA `(.L_x_0);                             /* 0xfffffff000007947 */
      |                                                                              /* 0x000fc0000383ffff */
      |        /*00b0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00c0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00d0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00e0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00f0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0100*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0110*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0120*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0130*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val f2fF32F64Sm70Probe: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.f2f_conversion_probe --------------------------
      |	.section	.text.f2f_conversion_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=16"
      |	.align	128
      |        .global         f2f_conversion_probe
      |        .type           f2f_conversion_probe,@function
      |        .size           f2f_conversion_probe,(.L_x_1 - f2f_conversion_probe)
      |        .other          f2f_conversion_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |f2f_conversion_probe:
      |.text.f2f_conversion_probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                             /* 0x00000a0000017a02 */
      |                                                                                      /* 0x000fc40000000f00 */
      |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                      /* 0x000000fffffff389 */
      |                                                                                      /* 0x000fe200000e00ff */
      |        /*0020*/                   S2R R10, SR_TID.X ;                                /* 0x00000000000a7919 */
      |                                                                                      /* 0x000e240000002100 */
      |        /*0030*/                   ISETP.GE.U32.AND P0, PT, R10, c[0x0][0x180], PT ;  /* 0x000060000a007a0c */
      |                                                                                      /* 0x001fd80003f06070 */
      |        /*0040*/               @P0 EXIT ;                                             /* 0x000000000000094d */
      |                                                                                      /* 0x000fea0003800000 */
      |        /*0050*/                   MOV R11, 0x8 ;                                     /* 0x00000008000b7802 */
      |                                                                                      /* 0x000fca0000000f00 */
      |        /*0060*/                   IMAD.WIDE.U32 R2, R10, R11, c[0x0][0x168] ;        /* 0x00005a000a027625 */
      |                                                                                      /* 0x000fd000078e000b */
      |        /*0070*/                   LDG.E.64.SYS R2, [R2] ;                            /* 0x0000000002027381 */
      |                                                                                      /* 0x000ea200001eeb00 */
      |        /*0080*/                   MOV R7, 0x4 ;                                      /* 0x0000000400077802 */
      |                                                                                      /* 0x000fca0000000f00 */
      |        /*0090*/                   IMAD.WIDE.U32 R4, R10, R7, c[0x0][0x160] ;         /* 0x000058000a047625 */
      |                                                                                      /* 0x000fc800078e0007 */
      |        /*00a0*/                   IMAD.WIDE.U32 R6, R10.reuse, R7, c[0x0][0x178] ;   /* 0x00005e000a067625 */
      |                                                                                      /* 0x040fe200078e0007 */
      |        /*00b0*/                   F2F.F32.F64 R13, R2 ;                              /* 0x00000002000d7310 */
      |                                                                                      /* 0x004e260000301000 */
      |        /*00c0*/                   STG.E.SYS [R4], R13 ;                              /* 0x0000000d04007386 */
      |                                                                                      /* 0x001fe8000010e900 */
      |        /*00d0*/                   LDG.E.SYS R6, [R6] ;                               /* 0x0000000006067381 */
      |                                                                                      /* 0x000ea200001ee900 */
      |        /*00e0*/                   IMAD.WIDE.U32 R10, R10, R11, c[0x0][0x170] ;       /* 0x00005c000a0a7625 */
      |                                                                                      /* 0x000fe200078e000b */
      |        /*00f0*/                   F2F.F64.F32 R8, R6 ;                               /* 0x0000000600087310 */
      |                                                                                      /* 0x004e2e0000201800 */
      |        /*0100*/                   STG.E.64.SYS [R10], R8 ;                           /* 0x000000080a007386 */
      |                                                                                      /* 0x001fe2000010eb00 */
      |        /*0110*/                   EXIT ;                                             /* 0x000000000000794d */
      |                                                                                      /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*0120*/                   BRA `(.L_x_0);                                     /* 0xfffffff000007947 */
      |                                                                                      /* 0x000fc0000383ffff */
      |        /*0130*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val f2fF32F64Sm75Probe: String =
    """	.target	sm_75
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.f2f_conversion_probe --------------------------
      |	.section	.text.f2f_conversion_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=16"
      |	.align	128
      |        .global         f2f_conversion_probe
      |        .type           f2f_conversion_probe,@function
      |        .size           f2f_conversion_probe,(.L_x_1 - f2f_conversion_probe)
      |        .other          f2f_conversion_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |f2f_conversion_probe:
      |.text.f2f_conversion_probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                             /* 0x00000a0000017a02 */
      |                                                                                      /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R10, SR_TID.X ;                                /* 0x00000000000a7919 */
      |                                                                                      /* 0x000e240000002100 */
      |        /*0020*/                   ISETP.GE.U32.AND P0, PT, R10, c[0x0][0x180], PT ;  /* 0x000060000a007a0c */
      |                                                                                      /* 0x001fd80003f06070 */
      |        /*0030*/               @P0 EXIT ;                                             /* 0x000000000000094d */
      |                                                                                      /* 0x000fea0003800000 */
      |        /*0040*/                   MOV R11, 0x8 ;                                     /* 0x00000008000b7802 */
      |                                                                                      /* 0x000fca0000000f00 */
      |        /*0050*/                   IMAD.WIDE.U32 R2, R10, R11, c[0x0][0x168] ;        /* 0x00005a000a027625 */
      |                                                                                      /* 0x000fd000078e000b */
      |        /*0060*/                   LDG.E.64.SYS R2, [R2] ;                            /* 0x0000000002027381 */
      |                                                                                      /* 0x000ea200001eeb00 */
      |        /*0070*/                   MOV R7, 0x4 ;                                      /* 0x0000000400077802 */
      |                                                                                      /* 0x000fca0000000f00 */
      |        /*0080*/                   IMAD.WIDE.U32 R4, R10, R7, c[0x0][0x160] ;         /* 0x000058000a047625 */
      |                                                                                      /* 0x000fc800078e0007 */
      |        /*0090*/                   IMAD.WIDE.U32 R6, R10.reuse, R7, c[0x0][0x178] ;   /* 0x00005e000a067625 */
      |                                                                                      /* 0x040fe200078e0007 */
      |        /*00a0*/                   F2F.F32.F64 R13, R2 ;                              /* 0x00000002000d7310 */
      |                                                                                      /* 0x004e260000301000 */
      |        /*00b0*/                   STG.E.SYS [R4], R13 ;                              /* 0x0000000d04007386 */
      |                                                                                      /* 0x001fe8000010e900 */
      |        /*00c0*/                   LDG.E.SYS R6, [R6] ;                               /* 0x0000000006067381 */
      |                                                                                      /* 0x000ea200001ee900 */
      |        /*00d0*/                   IMAD.WIDE.U32 R10, R10, R11, c[0x0][0x170] ;       /* 0x00005c000a0a7625 */
      |                                                                                      /* 0x000fe200078e000b */
      |        /*00e0*/                   F2F.F64.F32 R8, R6 ;                               /* 0x0000000600087310 */
      |                                                                                      /* 0x004e2e0000201800 */
      |        /*00f0*/                   STG.E.64.SYS [R10], R8 ;                           /* 0x000000080a007386 */
      |                                                                                      /* 0x001fe2000010eb00 */
      |        /*0100*/                   EXIT ;                                             /* 0x000000000000794d */
      |                                                                                      /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*0110*/                   BRA `(.L_x_0);                                     /* 0xfffffff000007947 */
      |                                                                                      /* 0x000fc0000383ffff */
      |        /*0120*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0130*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val f2fF32F64Sm80Probe: String =
    """	.target	sm_80
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.f2f_conversion_probe --------------------------
      |	.section	.text.f2f_conversion_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=16"
      |	.align	128
      |        .global         f2f_conversion_probe
      |        .type           f2f_conversion_probe,@function
      |        .size           f2f_conversion_probe,(.L_x_1 - f2f_conversion_probe)
      |        .other          f2f_conversion_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |f2f_conversion_probe:
      |.text.f2f_conversion_probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;
      |        /*0010*/                   S2R R10, SR_TID.X ;
      |        /*0020*/                   ISETP.GE.U32.AND P0, PT, R10, c[0x0][0x180], PT ;
      |        /*0030*/               @P0 EXIT ;
      |        /*0040*/                   HFMA2.MMA R11, -RZ, RZ, 0, 4.76837158203125e-07 ;
      |        /*0050*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*0060*/                   IMAD.WIDE.U32 R2, R10, R11, c[0x0][0x168] ;
      |        /*0070*/                   LDG.E.64 R2, [R2.64] ;
      |        /*0080*/                   HFMA2.MMA R7, -RZ, RZ, 0, 2.384185791015625e-07 ;
      |        /*0090*/                   IMAD.WIDE.U32 R4, R10, R7, c[0x0][0x160] ;
      |        /*00a0*/                   IMAD.WIDE.U32 R6, R10.reuse, R7, c[0x0][0x178] ;
      |        /*00b0*/                   F2F.F32.F64 R13, R2 ;
      |        /*00c0*/                   STG.E [R4.64], R13 ;
      |        /*00d0*/                   LDG.E R6, [R6.64] ;
      |        /*00e0*/                   IMAD.WIDE.U32 R10, R10, R11, c[0x0][0x170] ;
      |        /*00f0*/                   F2F.F64.F32 R8, R6 ;
      |        /*0100*/                   STG.E.64 [R10.64], R8 ;
      |        /*0110*/                   EXIT ;
      |.L_x_0:
      |        /*0120*/                   BRA `(.L_x_0);
      |        /*0130*/                   NOP;
      |        /*0140*/                   NOP;
      |        /*0150*/                   NOP;
      |        /*0160*/                   NOP;
      |        /*0170*/                   NOP;
      |        /*0180*/                   NOP;
      |        /*0190*/                   NOP;
      |        /*01a0*/                   NOP;
      |        /*01b0*/                   NOP;
      |        /*01c0*/                   NOP;
      |        /*01d0*/                   NOP;
      |        /*01e0*/                   NOP;
      |        /*01f0*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  private def f2fF32F64Listing(sm: Int): String =
    s"""	.target	sm_$sm
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.f2f_conversion_probe --------------------------
      |	.section	.text.f2f_conversion_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=16"
      |	.align	128
      |        .global         f2f_conversion_probe
      |        .type           f2f_conversion_probe,@function
      |        .size           f2f_conversion_probe,(.L_x_1 - f2f_conversion_probe)
      |        .other          f2f_conversion_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |f2f_conversion_probe:
      |.text.f2f_conversion_probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;
      |        /*0010*/                   S2R R10, SR_TID.X ;
      |        /*0020*/                   ISETP.GE.U32.AND P0, PT, R10, c[0x0][0x180], PT ;
      |        /*0030*/               @P0 EXIT ;
      |        /*0040*/                   MOV R11, 0x8 ;
      |        /*0050*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*0060*/                   IMAD.WIDE.U32 R2, R10, R11, c[0x0][0x168] ;
      |        /*0070*/                   LDG.E.64 R2, [R2.64] ;
      |        /*0080*/                   MOV R7, 0x4 ;
      |        /*0090*/                   IMAD.WIDE.U32 R4, R10, R7, c[0x0][0x160] ;
      |        /*00a0*/                   IMAD.WIDE.U32 R6, R10.reuse, R7, c[0x0][0x178] ;
      |        /*00b0*/                   F2F.F32.F64 R13, R2 ;
      |        /*00c0*/                   STG.E [R4.64], R13 ;
      |        /*00d0*/                   LDG.E R6, [R6.64] ;
      |        /*00e0*/                   IMAD.WIDE.U32 R10, R10, R11, c[0x0][0x170] ;
      |        /*00f0*/                   F2F.F64.F32 R8, R6 ;
      |        /*0100*/                   STG.E.64 [R10.64], R8 ;
      |        /*0110*/                   EXIT ;
      |.L_x_0:
      |        /*0120*/                   BRA `(.L_x_0);
      |        /*0130*/                   NOP;
      |        /*0140*/                   NOP;
      |        /*0150*/                   NOP;
      |        /*0160*/                   NOP;
      |        /*0170*/                   NOP;
      |        /*0180*/                   NOP;
      |        /*0190*/                   NOP;
      |        /*01a0*/                   NOP;
      |        /*01b0*/                   NOP;
      |        /*01c0*/                   NOP;
      |        /*01d0*/                   NOP;
      |        /*01e0*/                   NOP;
      |        /*01f0*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val f2fF32F64Sm86Probe: String = f2fF32F64Listing(86)

  val f2fF32F64Sm89Probe: String = f2fF32F64Listing(89)

  val f2fpPackAbSm89F2fpPackAbSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.f2fp_pack_ab_probe  --------------------------
      |	.section	.text.f2fp_pack_ab_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	128
      |        .global         f2fp_pack_ab_probe
      |        .type           f2fp_pack_ab_probe,@function
      |        .size           f2fp_pack_ab_probe,(.L_x_1 - f2fp_pack_ab_probe)
      |        .other          f2fp_pack_ab_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |f2fp_pack_ab_probe:
      |.text.f2fp_pack_ab_probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                     /* 0x00000a0000017a02 */
      |                                                                              /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R4, SR_TID.X ;                         /* 0x0000000000047919 */
      |                                                                              /* 0x000e220000002100 */
      |        /*0020*/                   MOV R5, 0x4 ;                              /* 0x0000000400057802 */
      |                                                                              /* 0x000fe20000000f00 */
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;               /* 0x0000460000047ab9 */
      |                                                                              /* 0x000fe20000000a00 */
      |        /*0040*/                   SHF.L.U32 R2, R4, 0x1, RZ ;                /* 0x0000000104027819 */
      |                                                                              /* 0x001fca00000006ff */
      |        /*0050*/                   IMAD.WIDE.U32 R2, R2, R5, c[0x0][0x160] ;  /* 0x0000580002027625 */
      |                                                                              /* 0x000fca00078e0005 */
      |        /*0060*/                   LDG.E R0, [R2.64] ;                        /* 0x0000000402007981 */
      |                                                                              /* 0x000ea8000c1e1900 */
      |        /*0070*/                   LDG.E R7, [R2.64+0x4] ;                    /* 0x0000040402077981 */
      |                                                                              /* 0x000ea2000c1e1900 */
      |        /*0080*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x168] ;  /* 0x00005a0004047625 */
      |                                                                              /* 0x000fe200078e0005 */
      |        /*0090*/                   F2FP.F16.F32.PACK_AB R7, R7, R0 ;          /* 0x000000000707723e */
      |                                                                              /* 0x004fca00000000ff */
      |        /*00a0*/                   STG.E [R4.64], R7 ;                        /* 0x0000000704007986 */
      |                                                                              /* 0x000fe2000c101904 */
      |        /*00b0*/                   EXIT ;                                     /* 0x000000000000794d */
      |                                                                              /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00c0*/                   BRA `(.L_x_0);                             /* 0xfffffff000007947 */
      |                                                                              /* 0x000fc0000383ffff */
      |        /*00d0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00e0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*00f0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0100*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0110*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0120*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0130*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val i2fF16Sm80I2fF16ImmediateSm80: String =
    """	.target	sm_80
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.i2f_f16_immediate_probe --------------------------
      |	.section	.text.i2f_f16_immediate_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	128
      |        .global         i2f_f16_immediate_probe
      |        .type           i2f_f16_immediate_probe,@function
      |        .size           i2f_f16_immediate_probe,(.L_x_1 - i2f_f16_immediate_probe)
      |        .other          i2f_f16_immediate_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |i2f_f16_immediate_probe:
      |.text.i2f_f16_immediate_probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                             /* 0x00000a0000017a02 */
      |                                                                                      /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R2, SR_TID.X ;                                 /* 0x0000000000027919 */
      |                                                                                      /* 0x000e220000002100 */
      |        /*0020*/                   I2F.F16 R5, 0x1 ;                                  /* 0x0000000100057906 */
      |                                                                                      /* 0x000e620000200c00 */
      |        /*0030*/                   HFMA2.MMA R3, -RZ, RZ, 0, 2.384185791015625e-07 ;  /* 0x00000004ff037435 */
      |                                                                                      /* 0x000fe200000001ff */
      |        /*0040*/                   ULDC.64 UR4, c[0x0][0x118] ;                       /* 0x0000460000047ab9 */
      |                                                                                      /* 0x000fd20000000a00 */
      |        /*0050*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;          /* 0x0000580002027625 */
      |                                                                                      /* 0x001fca00078e0003 */
      |        /*0060*/                   STG.E [R2.64], R5 ;                                /* 0x0000000502007986 */
      |                                                                                      /* 0x002fe2000c101904 */
      |        /*0070*/                   EXIT ;                                             /* 0x000000000000794d */
      |                                                                                      /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*0080*/                   BRA `(.L_x_0);                                     /* 0xfffffff000007947 */
      |                                                                                      /* 0x000fc0000383ffff */
      |        /*0090*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*00a0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*00b0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*00c0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*00d0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*00e0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*00f0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0100*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0110*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0120*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0130*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val i2fF16Sm80I2fF16Sm80: String =
    """	.target	sm_80
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.i2f_f16_probe       --------------------------
      |	.section	.text.i2f_f16_probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=10"
      |	.align	128
      |        .global         i2f_f16_probe
      |        .type           i2f_f16_probe,@function
      |        .size           i2f_f16_probe,(.L_x_1 - i2f_f16_probe)
      |        .other          i2f_f16_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |i2f_f16_probe:
      |.text.i2f_f16_probe:
      |        /*0000*/                   MOV R1, c[0x0][0x28] ;                             /* 0x00000a0000017a02 */
      |                                                                                      /* 0x000fe40000000f00 */
      |        /*0010*/                   S2R R4, SR_TID.X ;                                 /* 0x0000000000047919 */
      |                                                                                      /* 0x000e220000002100 */
      |        /*0020*/                   HFMA2.MMA R5, -RZ, RZ, 0, 2.384185791015625e-07 ;  /* 0x00000004ff057435 */
      |                                                                                      /* 0x000fe200000001ff */
      |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                       /* 0x0000460000047ab9 */
      |                                                                                      /* 0x000fd20000000a00 */
      |        /*0040*/                   IMAD.WIDE.U32 R2, R4, R5, c[0x0][0x160] ;          /* 0x0000580004027625 */
      |                                                                                      /* 0x001fcc00078e0005 */
      |        /*0050*/                   LDG.E R2, [R2.64] ;                                /* 0x0000000402027981 */
      |                                                                                      /* 0x000ea2000c1e1900 */
      |        /*0060*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x168] ;          /* 0x00005a0004047625 */
      |                                                                                      /* 0x000fe200078e0005 */
      |        /*0070*/                   I2F.F16 R7, R2 ;                                   /* 0x0000000200077306 */
      |                                                                                      /* 0x004e280000200c00 */
      |        /*0080*/                   STG.E [R4.64], R7 ;                                /* 0x0000000704007986 */
      |                                                                                      /* 0x001fe2000c101904 */
      |        /*0090*/                   EXIT ;                                             /* 0x000000000000794d */
      |                                                                                      /* 0x000fea0003800000 */
      |.L_x_0:
      |        /*00a0*/                   BRA `(.L_x_0);                                     /* 0xfffffff000007947 */
      |                                                                                      /* 0x000fc0000383ffff */
      |        /*00b0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*00c0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*00d0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*00e0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*00f0*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0100*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0110*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0120*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0130*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                               /* 0x0000000000007918 */
      |                                                                                      /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val i2fpSm86: String =
    """	.target	sm_86
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.convert             --------------------------
      |	.section	.text.convert,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=14"
      |	.align	128
      |        .global         convert
      |        .type           convert,@function
      |        .size           convert,(.L_x_1 - convert)
      |        .other          convert,@"STO_CUDA_ENTRY STV_DEFAULT"
      |convert:
      |.text.convert:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
      |        /*0010*/                   I2F.RM R7, c[0x0][0x168] ;
      |        /*0020*/                   I2FP.F32.S32 R5, c[0x0][0x168] ;
      |        /*0030*/                   IMAD.MOV.U32 R3, RZ, RZ, c[0x0][0x164] ;
      |        /*0040*/                   MOV R2, c[0x0][0x160] ;
      |        /*0050*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*0060*/                   I2FP.F32.S32.RZ R11, c[0x0][0x168] ;
      |        /*0070*/                   I2F.RP R9, c[0x0][0x168] ;
      |        /*0080*/                   STG.E [R2.64], R5 ;
      |        /*0090*/                   STG.E [R2.64+0xc], R11 ;
      |        /*00a0*/                   STG.E [R2.64+0x4], R7 ;
      |        /*00b0*/                   STG.E [R2.64+0x8], R9 ;
      |        /*00c0*/                   EXIT ;
      |.L_x_0:
      |        /*00d0*/                   BRA `(.L_x_0);
      |        /*00e0*/                   NOP;
      |        /*00f0*/                   NOP;
      |        /*0100*/                   NOP;
      |        /*0110*/                   NOP;
      |        /*0120*/                   NOP;
      |        /*0130*/                   NOP;
      |        /*0140*/                   NOP;
      |        /*0150*/                   NOP;
      |        /*0160*/                   NOP;
      |        /*0170*/                   NOP;
      |.L_x_1:
      |""".stripMargin

}

class ConversionFixtures extends NvidiaFixtureSuite {
  import ConversionFixtures.*

  reproduces("f2fF16F32Sm75ProbeSm70")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "input.cu" -> f2fF16F32Sm75ProbeCudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_70 input.cu -o kernel.cubin",
      "nvdisasm -c -ndf -hex kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = f2fF16F32Sm75ProbeSm70
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("f2fF16F32Sm75ProbeSm75", 75, f2fF16F32Sm75ProbeCudaSource, f2fF16F32Sm75ProbeSm75),
      ("f2fF16F32Sm75ProbeSm89", 89, f2fF16F32Sm75ProbeCudaSource, f2fF16F32Sm75ProbeSm89),
      ("f2fpPackAbSm89F2fpPackAbSm89", 89, f2fpPackAbSm89F2fpPackAbSm89CudaSource, f2fpPackAbSm89F2fpPackAbSm89)
    )
  ) reproduces(name)(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec("input.cu" -> source),
    script = Vec(
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm input.cu -o kernel.cubin",
      "nvdisasm -c -ndf -hex kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = expected
  )

  reproduces("f2fF32F64Sm70Probe")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "input.ptx" -> f2fF32F64Sm70ProbePtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_70 input.ptx -o kernel.cubin",
      "nvdisasm -c -ndf -hex kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = f2fF32F64Sm70Probe
  )

  reproduces("f2fF32F64Sm75Probe")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "input.ptx" -> f2fF32F64Sm75ProbePtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_75 input.ptx -o kernel.cubin",
      "nvdisasm -c -ndf -hex kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = f2fF32F64Sm75Probe
  )

  for (
    (name, sm, source, expected) <- Vec(
      ("f2fF32F64Sm80Probe", 80, f2fF32F64Sm80ProbePtxSource, f2fF32F64Sm80Probe),
      ("f2fF32F64Sm86Probe", 86, f2fF32F64Sm86ProbePtxSource, f2fF32F64Sm86Probe),
      ("f2fF32F64Sm89Probe", 89, f2fF32F64Sm89ProbePtxSource, f2fF32F64Sm89Probe),
      ("i2fpSm86", 86, i2fpSm86PtxSource, i2fpSm86)
    )
  ) reproduces(name)(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec("input.ptx" -> source),
    script = Vec(
      s"ptxas -arch=sm_$sm input.ptx -o kernel.cubin",
      "nvdisasm -c -ndf kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = expected
  )

  reproduces("i2fF16Sm80I2fF16ImmediateSm80")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "input.cu" -> i2fF16Sm80I2fF16Sm80CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_80 input.cu -o kernel.cubin",
      "symbol=$(readelf -sW kernel.cubin | awk '$NF == \"i2f_f16_immediate_probe\" {sub(\":\", \"\", $1); print $1; exit}')",
      "nvdisasm -c -ndf -hex -fun \"$symbol\" kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = i2fF16Sm80I2fF16ImmediateSm80
  )

  reproduces("i2fF16Sm80I2fF16Sm80")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "input.cu" -> i2fF16Sm80I2fF16Sm80CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_80 input.cu -o kernel.cubin",
      "symbol=$(readelf -sW kernel.cubin | awk '$NF == \"i2f_f16_probe\" {sub(\":\", \"\", $1); print $1; exit}')",
      "nvdisasm -c -ndf -hex -fun \"$symbol\" kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = i2fF16Sm80I2fF16Sm80
  )

}
