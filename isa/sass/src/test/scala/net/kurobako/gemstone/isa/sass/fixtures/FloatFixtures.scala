package net.kurobako.gemstone.isa.sass.fixtures

object FloatFixtures {
  val dsetPtx: String =
    """.version 7.8
      |.target sm_35
      |.address_size 64
      |.visible .entry probe(.param .u64 output, .param .u64 inputs) {
      |.reg .b32 r<8>;
      |.reg .b64 d<4>;
      |.reg .f64 f<2>;
      |.reg .f32 v;
      |.reg .pred p;
      |ld.param.u64 d0,[output];
      |ld.param.u64 d1,[inputs];
      |mov.u32 r0,%tid.x;
      |mad.wide.u32 d2,r0,16,d1;
      |ld.global.v2.f64 {f0,f1},[d2];
      |and.b32 r7,r0,1;
      |setp.eq.u32 p,r7,0;
      |set.neu.u32.f64 r1,f0,f1;
      |set.eq.u32.f64 r2,f0,f1;
      |set.lt.u32.f64 r3,f0,f1;
      |set.geu.u32.f64 r4,f0,f1;
      |set.eq.and.u32.f64 r5,f0,f1,p;
      |set.lt.or.u32.f64 r6,f0,f1,p;
      |set.neu.xor.u32.f64 r7,f0,f1,!p;
      |set.ge.f32.f64 v,f0,f1;
      |mad.wide.u32 d3,r0,32,d0;
      |st.global.v4.u32 [d3],{r1,r2,r3,r4};
      |st.global.v2.u32 [d3+16],{r5,r6};
      |st.global.u32 [d3+24],r7;
      |st.global.f32 [d3+28],v;
      |ret;
      |}
      |""".stripMargin

  def fchkPtx(sm: Int): String =
    s""".version ${if (sm <= 70) "7.8" else "9.2"}
      |.target sm_$sm
      |.address_size 64
      |.visible .entry probe(.param .u64 p_num, .param .u64 p_den, .param .u64 p_out) {
      |.reg .b64 %pn, %pd, %po, %off;
      |.reg .b32 %n, %d, %q, %i, %b;
      |.reg .pred %p;
      |ld.param.u64 %pn,[p_num]; ld.param.u64 %pd,[p_den]; ld.param.u64 %po,[p_out];
      |mov.u32 %i,%tid.x; mov.u32 %b,%ctaid.x; mad.lo.u32 %i,%b,256,%i;
      |cvt.u64.u32 %off,%i; shl.b64 %off,%off,2;
      |add.u64 %pn,%pn,%off; add.u64 %pd,%pd,%off; add.u64 %po,%po,%off;
      |ld.global.f32 %n,[%pn]; ld.global.f32 %d,[%pd];
      |setp.eq.f32 %p,%n,%d;
      |selp.u32 %q,1,0,%p;
      |st.global.u32 [%po],%q; ret;
      |}
      |""".stripMargin

  val fselImmediatePtx: String =
    """.version 8.0
      |.target sm_86
      |.address_size 64
      |.visible .entry probe(.param .u64 output) {
      |.reg .b32 r<2>;
      |.reg .b64 d<2>;
      |.reg .f32 f<2>;
      |.reg .pred p;
      |ld.param.u64 d0,[output];
      |mov.u32 r0,%tid.x;
      |setp.eq.u32 p,r0,0;
      |selp.f32 f0,0f3f800000,0f7ff80000,p;
      |selp.f32 f1,0f3f800000,0f7fc00000,p;
      |mad.wide.u32 d1,r0,8,d0;
      |st.global.v2.f32 [d1],{f0,f1};
      |ret;
      |}
      |""".stripMargin

  private val fchkSm80Patched: String =
    """//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=12"
      |	.align	128
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_0 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
      |        /*0010*/                   S2R R0, SR_TID.X ;
      |        /*0020*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*0030*/                   S2R R3, SR_CTAID.X ;
      |        /*0040*/                   IMAD R0, R3, 0x100, R0 ;
      |        /*0050*/                   IMAD.SHL.U32 R6, R0, 0x4, RZ ;
      |        /*0060*/                   SHF.R.U32.HI R0, RZ, 0x1e, R0 ;
      |        /*0070*/                   IADD3 R4, P1, R6.reuse, c[0x0][0x168], RZ ;
      |        /*0080*/                   IADD3 R2, P0, R6, c[0x0][0x160], RZ ;
      |        /*0090*/                   IADD3.X R5, R0.reuse, c[0x0][0x16c], RZ, P1, !PT ;
      |        /*00a0*/                   IADD3.X R3, R0, c[0x0][0x164], RZ, P0, !PT ;
      |        /*00b0*/                   LDG.E R5, [R4.64] ;
      |        /*00c0*/                   LDG.E R2, [R2.64] ;
      |        /*00d0*/                   IADD3 R6, P1, R6, c[0x0][0x170], RZ ;
      |        /*00e0*/                   IADD3.X R7, R0, c[0x0][0x174], RZ, P1, !PT ;
      |        /*00f0*/                   FCHK P0, R2, R5 ;
      |        /*0100*/                   NOP ;
      |        /*0110*/                   NOP ;
      |        /*0120*/                   NOP ;
      |        /*0130*/                   NOP ;
      |        /*0140*/                   NOP ;
      |        /*0150*/                   NOP ;
      |        /*0160*/                   NOP ;
      |        /*0170*/                   NOP ;
      |        /*0180*/                   SEL R9, RZ, 0x1, P0 ;
      |        /*0190*/                   STG.E [R6.64], R9 ;
      |        /*01a0*/                   EXIT ;
      |        /*01b0*/                   NOP;
      |        /*01c0*/                   NOP;
      |        /*01d0*/                   NOP;
      |        /*01e0*/                   NOP;
      |        /*01f0*/                   NOP;
      |.L_x_0:
      |""".stripMargin

  val listings: Map[String, String] = Map(
    "dsetSm35" ->
      """//--------------------- .text.probe               --------------------------
        |	.section	.text.probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=24"
        |	.align	64
        |        .global         probe
        |        .type           probe,@function
        |        .size           probe,(.L_x_1 - probe)
        |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |probe:
        |.text.probe:
        |                                                                                 /* 0x08a010b010a010ac */
        |        /*0008*/                   MOV R1, c[0x0][0x44];                         /* 0x64c03c00089c0006 */
        |        /*0010*/                   S2R R3, SR_TID.X;                             /* 0x86400000109c000e */
        |        /*0018*/                   MOV32I R0, 0x10;                              /* 0x74000000081fc002 */
        |        /*0020*/                   ISCADD R4.CC, R3, c[0x0][0x148], 0x4;         /* 0x60c41000291c0c12 */
        |        /*0028*/                   MOV32I R8, 0x20;                              /* 0x74000000101fc022 */
        |        /*0030*/                   IMAD.U32.U32.HI.X R5, R3, R0, c[0x0][0x14c];  /* 0x92100000299c0c16 */
        |        /*0038*/                   LOP32I.AND R0, R3, 0x1;                       /* 0x20000000009c0c00 */
        |                                                                                 /* 0x088c8c8cfcb08010 */
        |        /*0048*/                   LD.E.128 R4, [R4];                            /* 0xc6800000001c1010 */
        |        /*0050*/                   ISETP.NE.U32.AND P0, PT, R0, RZ, PT;          /* 0xdb501c007f9c001e */
        |        /*0058*/                   ISCADD R2.CC, R3, c[0x0][0x140], 0x5;         /* 0x60c41400281c0c0a */
        |        /*0060*/                   IMAD.U32.U32.HI.X R3, R3, R8, c[0x0][0x144];  /* 0x92102000289c0c0e */
        |        /*0068*/                   DSET.NEU.AND R12, R4, R6, PT;                 /* 0xc8681c00031c1032 */
        |        /*0070*/                   DSET.EQ.AND R13, R4, R6, PT;                  /* 0xc8101c00031c1036 */
        |        /*0078*/                   DSET.LT.AND R14, R4, R6, PT;                  /* 0xc8081c00031c103a */
        |                                                                                 /* 0x0880a48c108c8c8c */
        |        /*0088*/                   DSET.GEU.AND R15, R4, R6, PT;                 /* 0xc8701c00031c103e */
        |        /*0090*/                   DSET.EQ.AND R20, R4, R6, !P0;                 /* 0xc8102000031c1052 */
        |        /*0098*/                   DSET.LT.OR R21, R4, R6, !P0;                  /* 0xc8092000031c1056 */
        |        /*00a0*/                   ST.E.128 [R2], R12;                           /* 0xe6800000001c0830 */
        |        /*00a8*/                   DSET.NEU.XOR R22, R4, R6, P0;                 /* 0xc86a0000031c105a */
        |        /*00b0*/                   DSET.BF.GE.AND R23, R4, R6, PT;               /* 0xc8b01c00031c105e */
        |        /*00b8*/                   ST.E.128 [R2+0x10], R20;                      /* 0xe6800000081c0850 */
        |                                                                                 /* 0x08000000000000b8 */
        |        /*00c8*/                   EXIT;                                         /* 0x18000000001c003c */
        |.L_x_1:
        |""".stripMargin,
    "fchkSm70Patched" ->
      """//--------------------- .text.probe               --------------------------
        |	.section	.text.probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         probe
        |        .type           probe,@function
        |        .size           probe,(.L_x_1 - probe)
        |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |probe:
        |.text.probe:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;             /* 0x00000a00ff017624 */
        |                                                                                       /* 0x000fc400078e00ff */
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;                       /* 0x000000fffffff389 */
        |                                                                                       /* 0x000fe200000e00ff */
        |        /*0020*/                   S2R R0, SR_TID.X ;                                  /* 0x0000000000007919 */
        |                                                                                       /* 0x000e280000002100 */
        |        /*0030*/                   S2R R3, SR_CTAID.X ;                                /* 0x0000000000037919 */
        |                                                                                       /* 0x000e240000002500 */
        |        /*0040*/                   IMAD R0, R3, 0x100, R0 ;                            /* 0x0000010003007824 */
        |                                                                                       /* 0x001fc800078e0200 */
        |        /*0050*/                   IMAD.SHL.U32 R6, R0, 0x4, RZ ;                      /* 0x0000000400067824 */
        |                                                                                       /* 0x000fe200078e00ff */
        |        /*0060*/                   SHF.R.U32.HI R0, RZ, 0x1e, R0 ;                     /* 0x0000001eff007819 */
        |                                                                                       /* 0x000fc80000011600 */
        |        /*0070*/                   IADD3 R2, P0, R6.reuse, c[0x0][0x160], RZ ;         /* 0x0000580006027a10 */
        |                                                                                       /* 0x040fe40007f1e0ff */
        |        /*0080*/                   IADD3 R4, P1, R6, c[0x0][0x168], RZ ;               /* 0x00005a0006047a10 */
        |                                                                                       /* 0x000fe40007f3e0ff */
        |        /*0090*/                   IADD3.X R3, R0.reuse, c[0x0][0x164], RZ, P0, !PT ;  /* 0x0000590000037a10 */
        |                                                                                       /* 0x040fe400007fe4ff */
        |        /*00a0*/                   IADD3.X R5, R0, c[0x0][0x16c], RZ, P1, !PT ;        /* 0x00005b0000057a10 */
        |                                                                                       /* 0x000fcc0000ffe4ff */
        |        /*00b0*/                   LDG.E.SYS R2, [R2] ;                                /* 0x0000000002027381 */
        |                                                                                       /* 0x000ea800001ee900 */
        |        /*00c0*/                   LDG.E.SYS R5, [R4] ;                                /* 0x0000000004057381 */
        |                                                                                       /* 0x000ea200001ee900 */
        |        /*00d0*/                   IADD3 R6, P1, R6, c[0x0][0x170], RZ ;               /* 0x00005c0006067a10 */
        |                                                                                       /* 0x000fc80007f3e0ff */
        |        /*00e0*/                   IADD3.X R7, R0, c[0x0][0x174], RZ, P1, !PT ;        /* 0x00005d0000077a10 */
        |                                                                                       /* 0x004fe40000ffe4ff */
        |        /*00f0*/                   FCHK P0, R2, R5 ;                                   /* 0x0000000502007302 */
        |                                                                                       /* 0x000e620000000000 */
        |        /*0100*/                   SEL R9, RZ, 0x1, P0 ;                               /* 0x00000001ff097807 */
        |                                                                                       /* 0x002fd00000000000 */
        |        /*0110*/                   STG.E.SYS [R6], R9 ;                                /* 0x0000000906007386 */
        |                                                                                       /* 0x000fe2000010e900 */
        |        /*0120*/                   EXIT ;                                              /* 0x000000000000794d */
        |                                                                                       /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "fchkSm75Patched" ->
      """//--------------------- .text.probe               --------------------------
        |	.section	.text.probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         probe
        |        .type           probe,@function
        |        .size           probe,(.L_x_1 - probe)
        |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |probe:
        |.text.probe:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;  /* 0x00000a00ff017624 */
        |                                                                            /* 0x000fe400078e00ff */
        |        /*0010*/                   S2R R0, SR_TID.X ;                       /* 0x0000000000007919 */
        |                                                                            /* 0x000e220000002100 */
        |        /*0020*/                   ULDC.64 UR4, c[0x0][0x160] ;             /* 0x0000580000047ab9 */
        |                                                                            /* 0x000fe40000000a00 */
        |        /*0030*/                   ULDC.64 UR6, c[0x0][0x168] ;             /* 0x00005a0000067ab9 */
        |                                                                            /* 0x000fe20000000a00 */
        |        /*0040*/                   S2R R3, SR_CTAID.X ;                     /* 0x0000000000037919 */
        |                                                                            /* 0x000e240000002500 */
        |        /*0050*/                   IMAD R0, R3, 0x100, R0 ;                 /* 0x0000010003007824 */
        |                                                                            /* 0x001fc800078e0200 */
        |        /*0060*/                   IMAD.SHL.U32 R2, R0, 0x4, RZ ;           /* 0x0000000400027824 */
        |                                                                            /* 0x000fe200078e00ff */
        |        /*0070*/                   SHF.R.U32.HI R3, RZ, 0x1e, R0 ;          /* 0x0000001eff037819 */
        |                                                                            /* 0x000fd00000011600 */
        |        /*0080*/                   LDG.E.SYS R0, [R2.64+UR4] ;              /* 0x0000000402007981 */
        |                                                                            /* 0x000ea8000c1ee900 */
        |        /*0090*/                   LDG.E.SYS R5, [R2.64+UR6] ;              /* 0x0000000602057981 */
        |                                                                            /* 0x000ea2000c1ee900 */
        |        /*00a0*/                   ULDC.64 UR4, c[0x0][0x170] ;             /* 0x00005c0000047ab9 */
        |                                                                            /* 0x004fe20000000a00 */
        |        /*00b0*/                   FCHK P0, R0, R5 ;                        /* 0x0000000500007302 */
        |                                                                            /* 0x000e620000000000 */
        |        /*00c0*/                   SEL R5, RZ, 0x1, P0 ;                    /* 0x00000001ff057807 */
        |                                                                            /* 0x002fca0000000000 */
        |        /*00d0*/                   STG.E.SYS [R2.64+UR4], R5 ;              /* 0x0000000502007986 */
        |                                                                            /* 0x000fe2000c10e904 */
        |        /*00e0*/                   EXIT ;                                   /* 0x000000000000794d */
        |                                                                            /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin,
    "fchkSm80Patched"   -> fchkSm80Patched,
    "fchkSm86Patched"   -> fchkSm80Patched,
    "fchkSm89Patched"   -> fchkSm80Patched,
    "fselImmediateSm86" ->
      """//--------------------- .text.probe               --------------------------
        |	.section	.text.probe,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         probe
        |        .type           probe,@function
        |        .size           probe,(.L_x_1 - probe)
        |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
        |probe:
        |.text.probe:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;        /* 0x00000a00ff017624 */
        |                                                                                  /* 0x000fe400078e00ff */
        |        /*0010*/                   S2R R0, SR_TID.X ;                             /* 0x0000000000007919 */
        |                                                                                  /* 0x000e220000002100 */
        |        /*0020*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x3f800000 ;          /* 0x3f800000ff057424 */
        |                                                                                  /* 0x000fe200078e00ff */
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;                   /* 0x0000460000047ab9 */
        |                                                                                  /* 0x000fe20000000a00 */
        |        /*0040*/                   ISETP.NE.U32.AND P0, PT, R0.reuse, RZ, PT ;    /* 0x000000ff0000720c */
        |                                                                                  /* 0x041fe40003f05070 */
        |        /*0050*/                   LEA R2, P1, R0.reuse, c[0x0][0x160], 0x3 ;     /* 0x0000580000027a11 */
        |                                                                                  /* 0x040fe400078218ff */
        |        /*0060*/                   FSEL R4, R5.reuse, +QNAN , !P0 ;               /* 0x7ff8000005047808 */
        |                                                                                  /* 0x040fe40004000000 */
        |        /*0070*/                   LEA.HI.X R3, R0, c[0x0][0x164], RZ, 0x3, P1 ;  /* 0x0000590000037a11 */
        |                                                                                  /* 0x000fe400008f1cff */
        |        /*0080*/                   FSEL R5, R5, +QNAN , !P0 ;                     /* 0x7fc0000005057808 */
        |                                                                                  /* 0x000fca0004000000 */
        |        /*0090*/                   STG.E.64 [R2.64], R4 ;                         /* 0x0000000402007986 */
        |                                                                                  /* 0x000fe2000c101b04 */
        |        /*00a0*/                   EXIT ;                                         /* 0x000000000000794d */
        |                                                                                  /* 0x000fea0003800000 */
        |.L_x_1:
        |""".stripMargin
  )
}
