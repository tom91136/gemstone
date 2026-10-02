package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object LdsmFixtures {
  val ldsmSm89ProbePtxSource: String =
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

  val ldsmSm89Probe: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.matrix_load         --------------------------
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
      |.L_x_0:
      |        /*0280*/                   BRA `(.L_x_0);                             /* 0xfffffff000007947 */
      |                                                                              /* 0x000fc0000383ffff */
      |        /*0290*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*02a0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*02b0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*02c0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*02d0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*02e0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*02f0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0300*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0310*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0320*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0330*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0340*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0350*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0360*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0370*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val ldsmSm89Uniform: String =
    """	.target	sm_89
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.matrix_load         --------------------------
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
      |.L_x_0:
      |        /*0280*/                   BRA `(.L_x_0);                             /* 0xfffffff000007947 */
      |                                                                              /* 0x000fc0000383ffff */
      |        /*0290*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*02a0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*02b0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*02c0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*02d0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*02e0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*02f0*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0300*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0310*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0320*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0330*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0340*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0350*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0360*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |        /*0370*/                   NOP;                                       /* 0x0000000000007918 */
      |                                                                              /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

}

class LdsmFixtures extends NvidiaFixtureSuite {
  import LdsmFixtures.*

  reproduces("ldsmSm89Probe")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "probe.ptx" -> ldsmSm89ProbePtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_89 probe.ptx -o probe.cubin",
      "nvdisasm -c -ndf -hex probe.cubin > probe.sass"
    ),
    output = "probe.sass",
    expected = ldsmSm89Probe
  )

  reproduces("ldsmSm89Uniform")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "probe.ptx" -> ldsmSm89ProbePtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_89 probe.ptx -o probe.cubin",
      "nvdisasm -c -ndf -hex probe.cubin > probe.sass",
      """perl -0777 - probe.cubin > uniform.cubin <<'PERL'
      |$x=<>;
      |sub once { my($old,$new)=@_;
      |die "encoding mismatch\n" unless index($x,$old)>=0 && index($x,$old)==rindex($x,$old);
      |$x =~ s/\Q$old\E/$new/;
      |} once(pack("H*","247601ff000a0000ff008e0700e40f00"),pack("H*","82780600100000000000000000e40f00"));
      |once(pack("H*","3b781400000000000002000000280e00"),pack("H*","3b78140006f0ffff0002000800280e00"));
      |once(pack("H*","3b780600000000000001000000680e00"),pack("H*","3b78060006f0ffff0001000800680e00"));
      |once(pack("H*","3b780500000000000000000000a80e00"),pack("H*","3b78050006f0ffff0000000800a80e00"));
      |once(pack("H*","3b781800000000000042000000e80e00"),pack("H*","3b78180006f0ffff0042000800e80e00"));
      |print $x
      |PERL""".stripMargin,
      "nvdisasm -c -ndf -hex uniform.cubin > uniform.sass"
    ),
    output = "uniform.sass",
    expected = ldsmSm89Uniform
  )

}
