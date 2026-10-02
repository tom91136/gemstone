package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object Hsetp2Fixtures {
  val hsetp2NeSm89CudaSource: String =
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

  val hsetp2Sm89CudaSource: String =
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

  private def hsetp2TwoHsetp2TwoForTarget(sm: Int): String =
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

  val hsetp2TwoHsetp2TwoSm70PtxSource: String = hsetp2TwoHsetp2TwoForTarget(70)

  val hsetp2TwoHsetp2TwoSm89PtxSource: String = hsetp2TwoHsetp2TwoForTarget(89)

  val hsetp2GeuRawSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hsetp2_probe        --------------------------
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
      |.L_x_0:
      |        /*0120*/                   BRA `(.L_x_0);                                /* 0xfffffff000007947 */
      |                                                                                 /* 0x000fc0000383ffff */
      |        /*0130*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*0180*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*0190*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*01a0*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*01b0*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*01c0*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*01d0*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*01e0*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*01f0*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hsetp2NeRawSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hsetp2_ne_probe     --------------------------
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
      |.L_x_0:
      |        /*0110*/                   BRA `(.L_x_0);                                /* 0xfffffff000007947 */
      |                                                                                 /* 0x000fc0000383ffff */
      |        /*0120*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*0130*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*0180*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*0190*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*01a0*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*01b0*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*01c0*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*01d0*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*01e0*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |        /*01f0*/                   NOP;                                          /* 0x0000000000007918 */
      |                                                                                 /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hsetp2Sm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hsetp2_probe        --------------------------
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
      |.L_x_0:
      |        /*0120*/                   BRA `(.L_x_0);                                   /* 0xfffffff000007947 */
      |                                                                                    /* 0x000fc0000383ffff */
      |        /*0130*/                   NOP;                                             /* 0x0000000000007918 */
      |                                                                                    /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                             /* 0x0000000000007918 */
      |                                                                                    /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                             /* 0x0000000000007918 */
      |                                                                                    /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                             /* 0x0000000000007918 */
      |                                                                                    /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                             /* 0x0000000000007918 */
      |                                                                                    /* 0x000fc00000000000 */
      |        /*0180*/                   NOP;                                             /* 0x0000000000007918 */
      |                                                                                    /* 0x000fc00000000000 */
      |        /*0190*/                   NOP;                                             /* 0x0000000000007918 */
      |                                                                                    /* 0x000fc00000000000 */
      |        /*01a0*/                   NOP;                                             /* 0x0000000000007918 */
      |                                                                                    /* 0x000fc00000000000 */
      |        /*01b0*/                   NOP;                                             /* 0x0000000000007918 */
      |                                                                                    /* 0x000fc00000000000 */
      |        /*01c0*/                   NOP;                                             /* 0x0000000000007918 */
      |                                                                                    /* 0x000fc00000000000 */
      |        /*01d0*/                   NOP;                                             /* 0x0000000000007918 */
      |                                                                                    /* 0x000fc00000000000 */
      |        /*01e0*/                   NOP;                                             /* 0x0000000000007918 */
      |                                                                                    /* 0x000fc00000000000 */
      |        /*01f0*/                   NOP;                                             /* 0x0000000000007918 */
      |                                                                                    /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hsetp2TwoHsetp2TwoSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hsetp2_two          --------------------------
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
      |.L_x_0:
      |        /*00f0*/                   BRA `(.L_x_0);                             /* 0xfffffff000007947 */
      |                                                                              /* 0x000fc0000383ffff */
      |.L_x_1:
      |""".stripMargin

  val hsetp2TwoHsetp2TwoSm89: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hsetp2_two          --------------------------
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
      |.L_x_0:
      |        /*00f0*/                   BRA `(.L_x_0);                                         /* 0xfffffff000007947 */
      |                                                                                          /* 0x000fc0000383ffff */
      |        /*0100*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0110*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0120*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0130*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0140*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0150*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0160*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |        /*0170*/                   NOP;                                                   /* 0x0000000000007918 */
      |                                                                                          /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

}

class Hsetp2Fixtures extends NvidiaFixtureSuite {
  import Hsetp2Fixtures.*

  reproduces("hsetp2GeuRawSm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hsetp2-sm89.cu" -> hsetp2Sm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hsetp2-sm89.cu -o hsetp2-sm89.cubin",
      """perl -0777 - hsetp2-sm89.cubin > hsetp2-geu-raw-sm89.cubin <<'PERL'
      |$x=<>;
      |$o=pack("H*","347200020500002000e8f00300e48f00");
      |$n=pack("H*","347200020500000000e0f00300e48f00");
      |die "expected one opcode\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
      |$x =~ s/\Q$o\E/$n/;
      |print $x
      |PERL""".stripMargin,
      "nvdisasm -c -ndf -hex hsetp2-geu-raw-sm89.cubin > hsetp2-geu-raw-sm89.sass"
    ),
    output = "hsetp2-geu-raw-sm89.sass",
    expected = hsetp2GeuRawSm89
  )

  reproduces("hsetp2NeRawSm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hsetp2-ne-sm89.cu" -> hsetp2NeSm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hsetp2-ne-sm89.cu -o hsetp2-ne-sm89.cubin",
      """perl -0777 - hsetp2-ne-sm89.cubin > hsetp2-ne-raw-sm89.cubin <<'PERL'
      |$x=<>;
      |$o=pack("H*","34720002ff0000200058f00300c60f00");
      |$n=pack("H*","34720002ff0000000050f00300c60f00");
      |die "expected one opcode\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
      |$x =~ s/\Q$o\E/$n/;
      |print $x
      |PERL""".stripMargin,
      "nvdisasm -c -ndf -hex hsetp2-ne-raw-sm89.cubin > hsetp2-ne-raw-sm89.sass"
    ),
    output = "hsetp2-ne-raw-sm89.sass",
    expected = hsetp2NeRawSm89
  )

  reproduces("hsetp2Sm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hsetp2-sm89.cu" -> hsetp2Sm89CudaSource
    ),
    script = Vec(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hsetp2-sm89.cu -o hsetp2-sm89.cubin",
      "nvdisasm -c -ndf -hex hsetp2-sm89.cubin > hsetp2-sm89.sass"
    ),
    output = "hsetp2-sm89.sass",
    expected = hsetp2Sm89
  )

  reproduces("hsetp2TwoHsetp2TwoSm70")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "hsetp2-two-sm70.ptx" -> hsetp2TwoHsetp2TwoSm70PtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_70 hsetp2-two-sm70.ptx -o hsetp2-two-sm70.cubin",
      "nvdisasm -c -ndf -hex hsetp2-two-sm70.cubin > hsetp2-two-sm70.sass"
    ),
    output = "hsetp2-two-sm70.sass",
    expected = hsetp2TwoHsetp2TwoSm70
  )

  reproduces("hsetp2TwoHsetp2TwoSm89")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "hsetp2-two-sm89.ptx" -> hsetp2TwoHsetp2TwoSm89PtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_89 hsetp2-two-sm89.ptx -o hsetp2-two-sm89.cubin",
      "nvdisasm -c -ndf -hex hsetp2-two-sm89.cubin > hsetp2-two-sm89.sass"
    ),
    output = "hsetp2-two-sm89.sass",
    expected = hsetp2TwoHsetp2TwoSm89
  )

}
