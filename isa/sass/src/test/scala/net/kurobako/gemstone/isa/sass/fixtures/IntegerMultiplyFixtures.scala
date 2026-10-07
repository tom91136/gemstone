package net.kurobako.gemstone.isa.sass.fixtures

object IntegerMultiplyFixtures {
  val divideCuda: String =
    """#include <stdint.h>
      |extern "C" __global__ void divide_probe(int *out, int a, int b) {
      |  out[0] = a / b;
      |  out[1] = a % b;
      |}
      |""".stripMargin

  val multiplySubtractCuda: String =
    """extern "C" __global__ void subtract_product(unsigned long long *out, unsigned a, unsigned b, unsigned long long c) {
      | out[0] = (unsigned long long)a * b - c;
      |}
      |""".stripMargin

  val xmadArithmeticPtx: String =
    """.version 7.8
      |.target sm_50
      |.address_size 64
      |.visible .entry xmad_patterns(.param .u64 output, .param .u64 input) {
      |.reg .u64 %out, %in;
      |.reg .u32 %a, %b, %c, %prod, %res, %tmp, %lo, %hi, %as, %bu;
      |.reg .u16 %a16, %b16;
      |ld.param.u64 %out,[output];
      |ld.param.u64 %in,[input];
      |ld.global.u32 %a,[%in];
      |ld.global.u32 %b,[%in+4];
      |ld.global.u32 %c,[%in+8];
      |// basic mul.wide.u16 + add
      |cvt.u16.u32 %a16,%a;
      |cvt.u16.u32 %b16,%b;
      |mul.wide.u16 %prod,%a16,%b16;
      |add.u32 %res,%prod,%c;
      |st.global.u32 [%out],%res;
      |// PSL
      |mul.wide.u16 %prod,%a16,%b16;
      |shl.b32 %prod,%prod,16;
      |add.u32 %res,%prod,%c;
      |st.global.u32 [%out+4],%res;
      |// PSL.CLO
      |mul.wide.u16 %prod,%a16,%b16;
      |shl.b32 %prod,%prod,16;
      |and.b32 %tmp,%c,0xffff;
      |add.u32 %res,%prod,%tmp;
      |st.global.u32 [%out+8],%res;
      |// PSL.CBCC
      |mul.wide.u16 %prod,%a16,%b16;
      |shl.b32 %prod,%prod,16;
      |shl.b32 %tmp,%b,16;
      |add.u32 %prod,%prod,%c;
      |add.u32 %res,%prod,%tmp;
      |st.global.u32 [%out+12],%res;
      |// CHI
      |mul.wide.u16 %prod,%a16,%b16;
      |shr.u32 %tmp,%c,16;
      |add.u32 %res,%prod,%tmp;
      |st.global.u32 [%out+16],%res;
      |// MRG
      |mul.wide.u16 %prod,%a16,%b16;
      |add.u32 %prod,%prod,%c;
      |and.b32 %lo,%prod,0xffff;
      |shl.b32 %hi,%b,16;
      |or.b32 %res,%lo,%hi;
      |st.global.u32 [%out+20],%res;
      |// mixed signedness
      |cvt.s32.s16 %as,%a;
      |cvt.u32.u16 %bu,%b;
      |mul.lo.s32 %prod,%as,%bu;
      |add.u32 %res,%prod,%c;
      |st.global.u32 [%out+24],%res;
      |ret;
      |}
      |""".stripMargin

  val xmadCompilerFormsPtx: String =
    """.version 7.8
      |.target sm_50
      |.address_size 64
      |.visible .entry xmad_patterns(.param .u64 output, .param .u64 input) {
      |.reg .u64 %out, %in, %p;
      |.reg .u32 %lane, %a, %b, %c, %prod, %res, %tmp, %lo, %hi, %as, %bu;
      |.reg .u16 %a16, %b16;
      |ld.param.u64 %out,[output];
      |ld.param.u64 %in,[input];
      |mov.u32 %lane,%tid.x;
      |mul.wide.u32 %p,%lane,12;
      |add.u64 %in,%in,%p;
      |ld.global.u32 %a,[%in];
      |ld.global.u32 %b,[%in+4];
      |ld.global.u32 %c,[%in+8];
      |cvt.u16.u32 %a16,%a;
      |cvt.u16.u32 %b16,%b;
      |mul.wide.u16 %prod,%a16,%b16;
      |add.u32 %res,%prod,%c;
      |st.global.u32 [%out],%res;
      |mul.wide.u16 %prod,%a16,%b16;
      |shl.b32 %prod,%prod,16;
      |add.u32 %res,%prod,%c;
      |st.global.u32 [%out+4],%res;
      |mul.wide.u16 %prod,%a16,%b16;
      |shl.b32 %prod,%prod,16;
      |and.b32 %tmp,%c,0xffff;
      |add.u32 %res,%prod,%tmp;
      |st.global.u32 [%out+8],%res;
      |mul.wide.u16 %prod,%a16,%b16;
      |shl.b32 %prod,%prod,16;
      |shl.b32 %tmp,%b,16;
      |add.u32 %prod,%prod,%c;
      |add.u32 %res,%prod,%tmp;
      |st.global.u32 [%out+12],%res;
      |mul.wide.u16 %prod,%a16,%b16;
      |shr.u32 %tmp,%c,16;
      |add.u32 %res,%prod,%tmp;
      |st.global.u32 [%out+16],%res;
      |mul.wide.u16 %prod,%a16,%b16;
      |add.u32 %prod,%prod,%c;
      |and.b32 %lo,%prod,0xffff;
      |shl.b32 %hi,%b,16;
      |or.b32 %res,%lo,%hi;
      |st.global.u32 [%out+20],%res;
      |cvt.s32.s16 %as,%a;
      |cvt.u32.u16 %bu,%b;
      |mul.lo.s32 %prod,%as,%bu;
      |add.u32 %res,%prod,%c;
      |st.global.u32 [%out+24],%res;
      |ret;
      |}
      |""".stripMargin

  val xmadSignedHiPtx: String =
    """.version 7.8
      |.target sm_50
      |.address_size 64
      |.visible .entry signed_hi(.param .u64 output, .param .u64 input) {
      |.reg .u64 %out, %in;
      |.reg .u32 %a, %b, %hi;
      |ld.param.u64 %out, [output];
      |ld.param.u64 %in, [input];
      |ld.global.u32 %a, [%in];
      |ld.global.u32 %b, [%in+4];
      |mul.hi.s32 %hi, %a, %b;
      |st.global.u32 [%out], %hi;
      |ret;
      |}
      |""".stripMargin

  val listings: Map[String, String] = Map(
    "divideSm89" ->
      """//--------------------- .text.divide_probe        --------------------------
        |	.section	.text.divide_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         divide_probe
        |        .type           divide_probe,@function
        |        .size           divide_probe,(.L_x_1 - divide_probe)
        |        .other          divide_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |divide_probe:
        |.text.divide_probe:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   IABS R5, c[0x0][0x16c] ;
        |        /*0020*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0030*/                   I2F.RP R0, R5 ;
        |        /*0040*/                   MUFU.RCP R0, R0 ;
        |        /*0050*/                   IADD3 R2, R0, 0xffffffe, RZ ;
        |        /*0060*/                   F2I.FTZ.U32.TRUNC.NTZ R3, R2 ;
        |        /*0070*/                   MOV R2, RZ ;
        |        /*0080*/                   IMAD.MOV R4, RZ, RZ, -R3 ;
        |        /*0090*/                   IMAD R7, R4, R5, RZ ;
        |        /*00a0*/                   IABS R4, c[0x0][0x168] ;
        |        /*00b0*/                   IMAD.HI.U32 R3, R3, R7, R2 ;
        |        /*00c0*/                   MOV R7, c[0x0][0x168] ;
        |        /*00d0*/                   IMAD.MOV.U32 R2, RZ, RZ, c[0x0][0x160] ;
        |        /*00e0*/                   IMAD.HI.U32 R3, R3, R4, RZ ;
        |        /*00f0*/                   IMAD.MOV R0, RZ, RZ, -R3 ;
        |        /*0100*/                   IMAD R0, R5, R0, R4 ;
        |        /*0110*/                   ISETP.GT.U32.AND P2, PT, R5, R0, PT ;
        |        /*0120*/              @!P2 IMAD.IADD R0, R0, 0x1, -R5 ;
        |        /*0130*/              @!P2 IADD3 R3, R3, 0x1, RZ ;
        |        /*0140*/                   ISETP.NE.AND P2, PT, RZ, c[0x0][0x16c], PT ;
        |        /*0150*/                   ISETP.GE.U32.AND P0, PT, R0, R5, PT ;
        |        /*0160*/                   LOP3.LUT R0, R7, c[0x0][0x16c], RZ, 0x3c, !PT ;
        |        /*0170*/                   ISETP.GE.AND P1, PT, R0, RZ, PT ;
        |        /*0180*/               @P0 IADD3 R3, R3, 0x1, RZ ;
        |        /*0190*/                   IMAD.MOV.U32 R5, RZ, RZ, R3 ;
        |        /*01a0*/                   MOV R3, c[0x0][0x164] ;
        |        /*01b0*/              @!P1 IMAD.MOV R5, RZ, RZ, -R5 ;
        |        /*01c0*/              @!P2 LOP3.LUT R5, RZ, c[0x0][0x16c], RZ, 0x33, !PT ;
        |        /*01d0*/                   IADD3 R0, -R5, RZ, RZ ;
        |        /*01e0*/                   STG.E [R2.64], R5 ;
        |        /*01f0*/                   IMAD R7, R0, c[0x0][0x16c], R7 ;
        |        /*0200*/                   STG.E [R2.64+0x4], R7 ;
        |        /*0210*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "multiplySubtractSm35" ->
      """//--------------------- .text.subtract_product    --------------------------
        |	.section	.text.subtract_product,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	64
        |        .global         subtract_product
        |        .type           subtract_product,@function
        |        .size           subtract_product,(.L_x_1 - subtract_product)
        |        .other          subtract_product,@"STO_CUDA_ENTRY STV_DEFAULT"
        |subtract_product:
        |.text.subtract_product:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R0, c[0x0][0x14c];
        |        /*0018*/                   MOV R4, c[0x0][0x148];
        |        /*0020*/                   LOP.PASS_B R3, RZ, ~c[0x0][0x154];
        |        /*0028*/                   MOV R2, c[0x0][0x140];
        |        /*0030*/                   IMAD.U32.U32 R4.CC, R0, R4, -c[0x0][0x150];
        |        /*0038*/                   IMAD.U32.U32.HI.X R5, R0, c[0x0][0x148], R3;
        |        /*0048*/                   MOV R3, c[0x0][0x144];
        |        /*0050*/                   ST.E.64 [R2], R4;
        |        /*0058*/                   EXIT;
        |.L_x_1:
        |""".stripMargin,
    "xmadArithmeticSm50" ->
      """//--------------------- .text.xmad_patterns       --------------------------
        |	.section	.text.xmad_patterns,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	32
        |        .global         xmad_patterns
        |        .type           xmad_patterns,@function
        |        .size           xmad_patterns,(.L_x_1 - xmad_patterns)
        |        .other          xmad_patterns,@"STO_CUDA_ENTRY STV_DEFAULT"
        |xmad_patterns:
        |.text.xmad_patterns:
        |                                                                              /* 0x001fc800fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;                     /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R2, c[0x0][0x148] ;                    /* 0x4c98078005270002 */
        |        /*0018*/                   MOV R3, c[0x0][0x14c] ;                    /* 0x4c98078005370003 */
        |                                                                              /* 0x001ec800f62007f1 */
        |        /*0028*/                   LDG.E R0, [R2] ;                           /* 0xeed4200000070200 */
        |        /*0030*/                   LDG.E R6, [R2+0x4] ;                       /* 0xeed4200000470206 */
        |        /*0038*/                   LDG.E R7, [R2+0x8] ;                       /* 0xeed4200000870207 */
        |                                                                              /* 0x081fc500fe2107e1 */
        |        /*0048*/                   XMAD R4, R0, R6, RZ ;                      /* 0x5b007f8000670004 */
        |        /*0050*/                   XMAD R9, R0, R6, R7.reuse ;                /* 0x5b00038000670009 */
        |        /*0058*/                   LOP32I.AND R5, R7.reuse, 0xffff ;          /* 0x0400000ffff70705 */
        |                                                                              /* 0x001fc440fe2007f3 */
        |        /*0068*/                   SHR.U32 R13, R7, 0x10 ;                    /* 0x382800000107070d */
        |        /*0070*/                   XMAD.PSL R11, R4.reuse, 0x1, R7 ;          /* 0x360003900017040b */
        |        /*0078*/                   LOP32I.AND R8, R9, 0xffff ;                /* 0x0400000ffff70908 */
        |                                                                              /* 0x001fc4c0fe2007e1 */
        |        /*0088*/                   XMAD.PSL R10, R4, 0x1, R5 ;                /* 0x360002900017040a */
        |        /*0090*/                   XMAD.S16.U16 R7, R0.reuse, R6.reuse, R7 ;  /* 0x5b01038000670007 */
        |        /*0098*/                   XMAD R13, R0, R6, R13 ;                    /* 0x5b0006800067000d */
        |                                                                              /* 0x001fc840fe2007f1 */
        |        /*00a8*/                   MOV R4, c[0x0][0x140] ;                    /* 0x4c98078005070004 */
        |        /*00b0*/                   XMAD.PSL R2, R6.reuse, 0x1, R11 ;          /* 0x3600059000170602 */
        |        /*00b8*/                   MOV R5, c[0x0][0x144] ;                    /* 0x4c98078005170005 */
        |                                                                              /* 0x001fc400fe2007f0 */
        |        /*00c8*/         {         XMAD.PSL R6, R6, 0x1, R8 ;                 /* 0x3600041000170606 */
        |        /*00d0*/                   STG.E [R4], R9         }
        |                                                                              /* 0xeedc200000070409 */
        |        /*00d8*/                   STG.E [R4+0x18], R7 ;                      /* 0xeedc200001870407 */
        |                                                                              /* 0x001fc400fe2007f1 */
        |        /*00e8*/                   STG.E [R4+0x8], R10 ;                      /* 0xeedc20000087040a */
        |        /*00f0*/                   STG.E [R4+0x4], R11 ;                      /* 0xeedc20000047040b */
        |        /*00f8*/                   STG.E [R4+0x10], R13 ;                     /* 0xeedc20000107040d */
        |                                                                              /* 0x001fb800fe2007f1 */
        |        /*0108*/                   STG.E [R4+0xc], R2 ;                       /* 0xeedc200000c70402 */
        |        /*0110*/                   STG.E [R4+0x14], R6 ;                      /* 0xeedc200001470406 */
        |        /*0118*/                   NOP ;                                      /* 0x50b0000000070f00 */
        |                                                                              /* 0x001f8000ffe007ff */
        |        /*0128*/                   EXIT ;                                     /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "xmadCompilerFormsSm50" ->
      """//--------------------- .text.xmad_patterns       --------------------------
        |	.section	.text.xmad_patterns,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	32
        |        .global         xmad_patterns
        |        .type           xmad_patterns,@function
        |        .size           xmad_patterns,(.L_x_1 - xmad_patterns)
        |        .other          xmad_patterns,@"STO_CUDA_ENTRY STV_DEFAULT"
        |xmad_patterns:
        |.text.xmad_patterns:
        |                                                                              /* 0x001c7c00fe0007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;                     /* 0x4c98078000870001 */
        |        /*0010*/         {         MOV32I R2, 0xc ;                           /* 0x0100000000c7f002 */
        |        /*0018*/                   S2R R0, SR_TID.X         }
        |                                                                              /* 0xf0c8000002170000 */
        |                                                                              /* 0x181fc4c0fe260ff1 */
        |        /*0028*/                   XMAD R7, R0.reuse, R2.reuse, RZ ;          /* 0x5b007f8000270007 */
        |        /*0030*/                   XMAD R3, R0.reuse, R2.reuse, RZ ;          /* 0x5b007f8000270003 */
        |        /*0038*/                   XMAD.MRG R5, R0.reuse, R2.H1.reuse, RZ ;   /* 0x5b007fa800270005 */
        |                                                                              /* 0x001f84c0fe2607f3 */
        |        /*0048*/                   XMAD R4, R0.reuse, R2.H1.reuse, RZ ;       /* 0x5b007f8800270004 */
        |        /*0050*/                   XMAD.CHI R7, R0.H1.reuse, R2.reuse, R7 ;   /* 0x5b28038000270007 */
        |        /*0058*/                   XMAD R2, R0.H1, R2.H1, RZ ;                /* 0x5b207f8800270002 */
        |                                                                              /* 0x001fd800fe2007f5 */
        |        /*0068*/                   XMAD.PSL.CBCC R3, R0.H1, R5.H1, R3 ;       /* 0x5b30019800570003 */
        |        /*0070*/                   IADD3.RS R4, R7, R4, R2 ;                  /* 0x5cc0012000470704 */
        |        /*0078*/                   IADD R2.CC, R3, c[0x0][0x148] ;            /* 0x4c10800005270302 */
        |                                                                              /* 0x001ec400fe2007f2 */
        |        /*0088*/                   IADD.X R3, R4, c[0x0][0x14c] ;             /* 0x4c10080005370403 */
        |        /*0090*/                   LDG.E R0, [R2] ;                           /* 0xeed4200000070200 */
        |        /*0098*/                   LDG.E R6, [R2+0x4] ;                       /* 0xeed4200000470206 */
        |                                                                              /* 0x201fc420fc2007b3 */
        |        /*00a8*/                   LDG.E R7, [R2+0x8] ;                       /* 0xeed4200000870207 */
        |        /*00b0*/                   XMAD R4, R0, R6, RZ ;                      /* 0x5b007f8000670004 */
        |        /*00b8*/                   XMAD R9, R0, R6, R7.reuse ;                /* 0x5b00038000670009 */
        |                                                                              /* 0x081fc400fe6207f1 */
        |        /*00c8*/                   LOP32I.AND R5, R7.reuse, 0xffff ;          /* 0x0400000ffff70705 */
        |        /*00d0*/                   SHR.U32 R13, R7, 0x10 ;                    /* 0x382800000107070d */
        |        /*00d8*/                   XMAD.PSL R11, R4.reuse, 0x1, R7 ;          /* 0x360003900017040b */
        |                                                                              /* 0x181fc400fc2007f1 */
        |        /*00e8*/                   LOP32I.AND R8, R9, 0xffff ;                /* 0x0400000ffff70908 */
        |        /*00f0*/                   XMAD.PSL R10, R4, 0x1, R5 ;                /* 0x360002900017040a */
        |        /*00f8*/                   XMAD.S16.U16 R7, R0.reuse, R6.reuse, R7 ;  /* 0x5b01038000670007 */
        |                                                                              /* 0x081fc400fe2007f1 */
        |        /*0108*/                   XMAD R13, R0, R6, R13 ;                    /* 0x5b0006800067000d */
        |        /*0110*/                   MOV R4, c[0x0][0x140] ;                    /* 0x4c98078005070004 */
        |        /*0118*/                   XMAD.PSL R2, R6.reuse, 0x1, R11 ;          /* 0x3600059000170602 */
        |                                                                              /* 0x001fc400fe0007f2 */
        |        /*0128*/                   MOV R5, c[0x0][0x144] ;                    /* 0x4c98078005170005 */
        |        /*0130*/         {         XMAD.PSL R6, R6, 0x1, R8 ;                 /* 0x3600041000170606 */
        |        /*0138*/                   STG.E [R4], R9         }
        |                                                                              /* 0xeedc200000070409 */
        |                                                                              /* 0x001fc400fe2007f1 */
        |        /*0148*/                   STG.E [R4+0x18], R7 ;                      /* 0xeedc200001870407 */
        |        /*0150*/                   STG.E [R4+0x8], R10 ;                      /* 0xeedc20000087040a */
        |        /*0158*/                   STG.E [R4+0x4], R11 ;                      /* 0xeedc20000047040b */
        |                                                                              /* 0x001fc400fe2007f1 */
        |        /*0168*/                   STG.E [R4+0x10], R13 ;                     /* 0xeedc20000107040d */
        |        /*0170*/                   STG.E [R4+0xc], R2 ;                       /* 0xeedc200000c70402 */
        |        /*0178*/                   STG.E [R4+0x14], R6 ;                      /* 0xeedc200001470406 */
        |                                                                              /* 0x001f8000ffe007ff */
        |        /*0188*/                   EXIT ;                                     /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin,
    "xmadSignedHiSm50" ->
      """//--------------------- .text.signed_hi           --------------------------
        |	.section	.text.signed_hi,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=9"
        |	.align	32
        |        .global         signed_hi
        |        .type           signed_hi,@function
        |        .size           signed_hi,(.L_x_1 - signed_hi)
        |        .other          signed_hi,@"STO_CUDA_ENTRY STV_DEFAULT"
        |signed_hi:
        |.text.signed_hi:
        |                                                                                         /* 0x001fc800fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;                                /* 0x4c98078000870001 */
        |        /*0010*/                   MOV R2, c[0x0][0x148] ;                               /* 0x4c98078005270002 */
        |        /*0018*/                   MOV R3, c[0x0][0x14c] ;                               /* 0x4c98078005370003 */
        |                                                                                         /* 0x001fc400f62007f1 */
        |        /*0028*/                   LDG.E R0, [R2] ;                                      /* 0xeed4200000070200 */
        |        /*0030*/                   LDG.E R5, [R2+0x4] ;                                  /* 0xeed4200000470205 */
        |        /*0038*/                   MOV R4, c[0x0][0x140] ;                               /* 0x4c98078005070004 */
        |                                                                                         /* 0x181fd0c0fe2107e1 */
        |        /*0048*/                   XMAD R6, R0, R5, RZ ;                                 /* 0x5b007f8000570006 */
        |        /*0050*/                   XMAD.U16.S16 R7, R0.reuse, R5.H1.reuse, RZ ;          /* 0x5b027f8800570007 */
        |        /*0058*/                   XMAD.S16.S16.CSFU R8, R0.H1.reuse, R5.H1.reuse, RZ ;  /* 0x5b2f7f8800570008 */
        |                                                                                         /* 0x001fc800fea007f1 */
        |        /*0068*/                   XMAD.S16.U16.CHI R6, R0.H1, R5, R6 ;                  /* 0x5b29030000570006 */
        |        /*0070*/                   MOV R5, c[0x0][0x144] ;                               /* 0x4c98078005170005 */
        |        /*0078*/                   IADD3.RS R6, R6, R7, R8 ;                             /* 0x5cc0042000770606 */
        |                                                                                         /* 0x001f9c00fde007f1 */
        |        /*0088*/                   STG.E [R4], R6 ;                                      /* 0xeedc200000070406 */
        |        /*0090*/                   NOP ;                                                 /* 0x50b0000000070f00 */
        |        /*0098*/                   NOP ;                                                 /* 0x50b0000000070f00 */
        |                                                                                         /* 0x001f8000ffe007ff */
        |        /*00a8*/                   EXIT ;                                                /* 0xe30000000007000f */
        |.L_x_1:
        |""".stripMargin
  )
}
