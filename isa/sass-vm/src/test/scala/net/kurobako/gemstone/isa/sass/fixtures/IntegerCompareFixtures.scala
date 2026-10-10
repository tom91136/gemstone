package net.kurobako.gemstone.isa.sass.fixtures

object IntegerCompareFixtures {
  val icmpPtx: String =
    """.version 7.8
      |.target sm_35
      |.address_size 64
      |.visible .entry probe(.param .u64 out, .param .s32 a, .param .s32 b, .param .s32 c) {
      |.reg .b64 %rd;
      |.reg .s32 %a, %b, %c, %x, %y, %z;
      |.reg .pred %p;
      |ld.param.u64 %rd, [out];
      |ld.param.s32 %a, [a];
      |ld.param.s32 %b, [b];
      |ld.param.s32 %c, [c];
      |setp.eq.s32 %p, %c, 0;
      |selp.s32 %x, %b, %a, %p;
      |slct.b32.s32 %y, %b, %a, %c;
      |setp.ge.s32 %p, 0, %c;
      |selp.s32 %z, %b, %a, %p;
      |st.global.s32 [%rd], %x;
      |st.global.s32 [%rd+4], %y;
      |st.global.s32 [%rd+8], %z;
      |ret;
      |}
      |""".stripMargin

  val isetEq64Cuda: String =
    """extern "C" __global__ void eq64(unsigned *out, unsigned long long a, unsigned long long b) {
      |  out[0] = a == b;
      |  out[1] = a != b;
      |  out[2] = a == 0;
      |  out[3] = b == 0;
      |}
      |""".stripMargin

  val isetEq64SignedCuda: String =
    """extern "C" __global__ void eq64s(unsigned *out, long long a, long long b) {
      |  out[0] = a == b;
      |  out[1] = a != b;
      |}
      |""".stripMargin

  val isetExtendedPtx: String =
    """.version 7.8
      |.target sm_35
      |.address_size 64
      |.visible .entry iset_probe(.param .u64 out, .param .u64 a, .param .u64 b) {
      |.reg .u64 %rd<4>;
      |.reg .u32 %r<6>;
      |ld.param.u64 %rd0,[out];
      |ld.param.u64 %rd1,[a];
      |ld.param.u64 %rd2,[b];
      |set.lt.s32.s64 %r0,%rd1,%rd2;
      |set.lt.u32.u64 %r1,%rd1,%rd2;
      |set.le.s32.s64 %r2,%rd1,%rd2;
      |set.gt.s32.s64 %r3,%rd1,%rd2;
      |set.ge.s32.s64 %r4,%rd1,%rd2;
      |st.global.u32 [%rd0],%r0;
      |st.global.u32 [%rd0+4],%r1;
      |st.global.u32 [%rd0+8],%r2;
      |st.global.u32 [%rd0+12],%r3;
      |st.global.u32 [%rd0+16],%r4;
      |ret;
      |}
      |""".stripMargin

  val psetPtx: String =
    """.version 7.8
      |.target sm_50
      |.address_size 64
      |.visible .entry probe(.param .u64 out, .param .u32 a) {
      |.reg .u64 %out;
      |.reg .u32 %a,%r;
      |.reg .pred %p;
      |ld.param.u64 %out,[out];ld.param.u32 %a,[a];
      |setp.ne.u32 %p,%a,0;
      |vote.any.pred %p,%p;
      |selp.u32 %r,0xffffffff,0,%p;
      |st.global.u32 [%out],%r;
      |ret;
      |}""".stripMargin

  val listings: Map[String, String] = Map(
    "icmpSm35" ->
      """//--------------------- .text.probe               --------------------------
        |	.section	.text.probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	64
        |        .global         probe
        |        .type           probe,@function
        |        .size           probe,(.L_x_1 - probe)
        |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |probe:
        |.text.probe:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x14c];
        |        /*0018*/                   MOV R2, c[0x0][0x140];
        |        /*0020*/                   MOV R0, c[0x0][0x150];
        |        /*0028*/                   MOV R3, c[0x0][0x144];
        |        /*0030*/                   ICMP.EQ R4, R6, c[0x0][0x148], R0;
        |        /*0038*/                   ICMP.GE R5, R6, c[0x0][0x148], R0;
        |        /*0048*/                   ICMP.LE R6, R6, c[0x0][0x148], R0;
        |        /*0050*/                   ST.E [R2], R4;
        |        /*0058*/                   ST.E [R2+0x4], R5;
        |        /*0060*/                   ST.E [R2+0x8], R6;
        |        /*0068*/                   EXIT;
        |.L_x_1:
        |""".stripMargin,
    "isetEq64SignedSm35" ->
      """//--------------------- .text.eq64s               --------------------------
        |	.section	.text.eq64s,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	64
        |        .global         eq64s
        |        .type           eq64s,@function
        |        .size           eq64s,(.L_x_1 - eq64s)
        |        .other          eq64s,@"STO_CUDA_ENTRY STV_DEFAULT"
        |eq64s:
        |.text.eq64s:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R0, c[0x0][0x148];
        |        /*0018*/                   MOV R5, c[0x0][0x14c];
        |        /*0020*/                   MOV R2, c[0x0][0x140];
        |        /*0028*/                   MOV R3, c[0x0][0x144];
        |        /*0030*/                   ISUB RZ.CC, R0, c[0x0][0x150];
        |        /*0038*/                   ISET.EQ.X.AND R4, R5, c[0x0][0x154], PT;
        |        /*0048*/                   ISET.NE.X.AND R5, R5, c[0x0][0x154], PT;
        |        /*0050*/                   LOP32I.AND R4, R4, 0x1;
        |        /*0058*/                   LOP32I.AND R5, R5, 0x1;
        |        /*0060*/                   ST.E [R2], R4;
        |        /*0068*/                   ST.E [R2+0x4], R5;
        |        /*0070*/                   EXIT;
        |.L_x_1:
        |""".stripMargin,
    "isetEq64Sm35" ->
      """//--------------------- .text.eq64                --------------------------
        |	.section	.text.eq64,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	64
        |        .global         eq64
        |        .type           eq64,@function
        |        .size           eq64,(.L_x_1 - eq64)
        |        .other          eq64,@"STO_CUDA_ENTRY STV_DEFAULT"
        |eq64:
        |.text.eq64:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R0, c[0x0][0x148];
        |        /*0018*/                   MOV R5, c[0x0][0x14c];
        |        /*0020*/                   MOV R2, c[0x0][0x140];
        |        /*0028*/                   MOV R3, c[0x0][0x144];
        |        /*0030*/                   ISUB RZ.CC, R0, c[0x0][0x150];
        |        /*0038*/                   ISET.EQ.X.AND R4, R5, c[0x0][0x154], PT;
        |        /*0048*/                   ISET.NE.X.AND R5, R5, c[0x0][0x154], PT;
        |        /*0050*/                   LOP32I.AND R4, R4, 0x1;
        |        /*0058*/                   ISUB RZ.CC, c[0x0][0x148], RZ;
        |        /*0060*/                   LOP32I.AND R5, R5, 0x1;
        |        /*0068*/                   ST.E [R2], R4;
        |        /*0070*/                   ISET.EQ.X.AND R6, RZ, c[0x0][0x14c], PT;
        |        /*0078*/                   ST.E [R2+0x4], R5;
        |        /*0088*/                   ISUB RZ.CC, c[0x0][0x150], RZ;
        |        /*0090*/                   LOP32I.AND R6, R6, 0x1;
        |        /*0098*/                   ST.E [R2+0x8], R6;
        |        /*00a0*/                   ISET.EQ.X.AND R7, RZ, c[0x0][0x154], PT;
        |        /*00a8*/                   LOP32I.AND R7, R7, 0x1;
        |        /*00b0*/                   ST.E [R2+0xc], R7;
        |        /*00b8*/                   EXIT;
        |.L_x_1:
        |""".stripMargin,
    "isetExtendedSm35" ->
      """//--------------------- .text.iset_probe          --------------------------
        |	.section	.text.iset_probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	64
        |        .global         iset_probe
        |        .type           iset_probe,@function
        |        .size           iset_probe,(.L_x_1 - iset_probe)
        |        .other          iset_probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |iset_probe:
        |.text.iset_probe:
        |                                                                                 /* 0x08a0b09c108010c0 */
        |        /*0008*/                   MOV R1, c[0x0][0x44];                         /* 0x64c03c00089c0006 */
        |        /*0010*/                   MOV R0, c[0x0][0x148];                        /* 0x64c03c00291c0002 */
        |        /*0018*/                   MOV R9, c[0x0][0x14c];                        /* 0x64c03c00299c0026 */
        |        /*0020*/                   MOV R2, c[0x0][0x140];                        /* 0x64c03c00281c000a */
        |        /*0028*/                   MOV R3, c[0x0][0x144];                        /* 0x64c03c00289c000e */
        |        /*0030*/                   ISUB RZ.CC, R0, c[0x0][0x150];                /* 0x608c00002a1c03fe */
        |        /*0038*/                   ISET.LT.X.AND R4, R9, c[0x0][0x154], PT;      /* 0x5a985c002a9c2412 */
        |                                                                                 /* 0x08b8b8ac80808010 */
        |        /*0048*/                   ISET.LT.U32.X.AND R5, R9, c[0x0][0x154], PT;  /* 0x5a905c002a9c2416 */
        |        /*0050*/                   ST.E [R2], R4;                                /* 0xe4800000001c0810 */
        |        /*0058*/                   ISET.LE.X.AND R6, R9, c[0x0][0x154], PT;      /* 0x5ab85c002a9c241a */
        |        /*0060*/                   ISET.GT.X.AND R7, R9, c[0x0][0x154], PT;      /* 0x5ac85c002a9c241e */
        |        /*0068*/                   ISET.GE.X.AND R9, R9, c[0x0][0x154], PT;      /* 0x5ae85c002a9c2426 */
        |        /*0070*/                   ST.E [R2+0x4], R5;                            /* 0xe4800000021c0814 */
        |        /*0078*/                   ST.E [R2+0x8], R6;                            /* 0xe4800000041c0818 */
        |                                                                                 /* 0x0800000000bc10b8 */
        |        /*0088*/                   ST.E [R2+0xc], R7;                            /* 0xe4800000061c081c */
        |        /*0090*/                   ST.E [R2+0x10], R9;                           /* 0xe4800000081c0824 */
        |        /*0098*/                   EXIT;                                         /* 0x18000000001c003c */
        |.L_x_1:
        |""".stripMargin,
    "psetSm50Patched" ->
      """//--------------------- .text.probe               --------------------------
        |	.section	.text.probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=4"
        |	.align	32
        |        .global         probe
        |        .type           probe,@function
        |        .size           probe,(.L_x_1 - probe)
        |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |probe:
        |.text.probe:
        |                                                                                     /* 0x001fc400fe2007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;                            /* 0x4c98078000870001 */
        |        /*0010*/                   ISETP.NE.U32.AND P0, PT, RZ, c[0x0][0x148], PT ;  /* 0x4b6a03800527ff07 */
        |        /*0018*/                   MOV R2, c[0x0][0x140] ;                           /* 0x4c98078005070002 */
        |                                                                                     /* 0x001fc800fda007fb */
        |        /*0028*/                   MOV R3, c[0x0][0x144] ;                           /* 0x4c98078005170003 */
        |        /*0030*/                   VOTE.ANY P0, P0 ;                                 /* 0x50d90000000700ff */
        |        /*0038*/                   PSET.AND.AND R0, P0, PT, PT ;                     /* 0x50880380e0070000 */
        |                                                                                     /* 0x001ffc00fdc007f1 */
        |        /*0048*/                   STG.E [R2], R0 ;                                  /* 0xeedc200000070200 */
        |        /*0050*/                   NOP ;                                             /* 0x50b0000000070f00 */
        |        /*0058*/                   EXIT ;                                            /* 0xe30000000007000f */
        |.L_x_0:
        |                                                                                     /* 0x001f8000fc0007ff */
        |        /*0068*/                   BRA `(.L_x_0) ;                                   /* 0xe2400fffff07000f */
        |        /*0070*/                   NOP;                                              /* 0x50b0000000070f00 */
        |        /*0078*/                   NOP;                                              /* 0x50b0000000070f00 */
        |.L_x_1:
        |""".stripMargin,
    "psetSm60Patched" ->
      """//--------------------- .text.probe               --------------------------
        |	.section	.text.probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=4"
        |	.align	32
        |        .global         probe
        |        .type           probe,@function
        |        .size           probe,(.L_x_1 - probe)
        |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |probe:
        |.text.probe:
        |                                                                                     /* 0x001f8400fda007f6 */
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;                            /* 0x4c98078000870001 */
        |        /*0010*/                   ISETP.NE.U32.AND P0, PT, RZ, c[0x0][0x148], PT ;  /* 0x4b6a03800527ff07 */
        |        /*0018*/                   VOTE.ANY P0, P0 ;                                 /* 0x50d90000000700ff */
        |                                                                                     /* 0x001f8800ff6007e1 */
        |        /*0028*/                   MOV R2, c[0x0][0x140] ;                           /* 0x4c98078005070002 */
        |        /*0030*/                   MOV R3, c[0x0][0x144] ;                           /* 0x4c98078005170003 */
        |        /*0038*/                   PSET.AND.AND R0, P0, PT, PT ;                     /* 0x50880380e0070000 */
        |                                                                                     /* 0x001ffc00fdc007f1 */
        |        /*0048*/                   STG.E [R2], R0 ;                                  /* 0xeedc200000070200 */
        |        /*0050*/                   NOP ;                                             /* 0x50b0000000070f00 */
        |        /*0058*/                   EXIT ;                                            /* 0xe30000000007000f */
        |.L_x_0:
        |                                                                                     /* 0x001f8000fc0007ff */
        |        /*0068*/                   BRA `(.L_x_0) ;                                   /* 0xe2400fffff07000f */
        |        /*0070*/                   NOP;                                              /* 0x50b0000000070f00 */
        |        /*0078*/                   NOP;                                              /* 0x50b0000000070f00 */
        |.L_x_1:
        |""".stripMargin
  )
}
