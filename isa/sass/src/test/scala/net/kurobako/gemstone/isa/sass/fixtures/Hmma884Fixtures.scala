package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object Hmma884Fixtures {
  val hmma884Sm70Hmma884F16ColColSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hmma884_f16_col_col_probe --------------------------
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
      |.L_x_0:
      |        /*0180*/                   BRA `(.L_x_0);                                                  /* 0xfffffff000007947 */
      |                                                                                                   /* 0x000fc0000383ffff */
      |        /*0190*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01a0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01b0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01c0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01d0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01e0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01f0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hmma884Sm70Hmma884F16ColColSource: String =
    """.version 7.8
      |.target sm_70
      |.address_size 64
      |.visible .entry hmma884_f16_col_col_probe(
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
      |mma.sync.aligned.m8n8k4.col.col.f16.f16.f16.f16 {%r9,%r10,%r11,%r12},{%r1,%r2},{%r3,%r4},{%r5,%r6,%r7,%r8};
      |add.u64 %rd6, %rd0, %rd5;
      |st.global.b32 [%rd6], %r9;
      |st.global.b32 [%rd6+4], %r10;
      |st.global.b32 [%rd6+8], %r11;
      |st.global.b32 [%rd6+12], %r12;
      |ret;
      |}
      |""".stripMargin

  val hmma884Sm70Hmma884F16ColRowSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hmma884_f16_col_row_probe --------------------------
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
      |.L_x_0:
      |        /*0180*/                   BRA `(.L_x_0);                                                  /* 0xfffffff000007947 */
      |                                                                                                   /* 0x000fc0000383ffff */
      |        /*0190*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01a0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01b0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01c0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01d0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01e0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01f0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hmma884Sm70Hmma884F16ColRowSource: String =
    """.version 7.8
      |.target sm_70
      |.address_size 64
      |.visible .entry hmma884_f16_col_row_probe(
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
      |mma.sync.aligned.m8n8k4.col.row.f16.f16.f16.f16 {%r9,%r10,%r11,%r12},{%r1,%r2},{%r3,%r4},{%r5,%r6,%r7,%r8};
      |add.u64 %rd6, %rd0, %rd5;
      |st.global.b32 [%rd6], %r9;
      |st.global.b32 [%rd6+4], %r10;
      |st.global.b32 [%rd6+8], %r11;
      |st.global.b32 [%rd6+12], %r12;
      |ret;
      |}
      |""".stripMargin

  val hmma884Sm70Hmma884F16RowColSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hmma884_f16_row_col_probe --------------------------
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
      |.L_x_0:
      |        /*0180*/                   BRA `(.L_x_0);                                                  /* 0xfffffff000007947 */
      |                                                                                                   /* 0x000fc0000383ffff */
      |        /*0190*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01a0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01b0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01c0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01d0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01e0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01f0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hmma884Sm70Hmma884F16RowColSource: String =
    """.version 7.8
      |.target sm_70
      |.address_size 64
      |.visible .entry hmma884_f16_row_col_probe(
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
      |mma.sync.aligned.m8n8k4.row.col.f16.f16.f16.f16 {%r9,%r10,%r11,%r12},{%r1,%r2},{%r3,%r4},{%r5,%r6,%r7,%r8};
      |add.u64 %rd6, %rd0, %rd5;
      |st.global.b32 [%rd6], %r9;
      |st.global.b32 [%rd6+4], %r10;
      |st.global.b32 [%rd6+8], %r11;
      |st.global.b32 [%rd6+12], %r12;
      |ret;
      |}
      |""".stripMargin

  val hmma884Sm70Hmma884F16RowRowSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hmma884_f16_row_row_probe --------------------------
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
      |.L_x_0:
      |        /*0180*/                   BRA `(.L_x_0);                                                  /* 0xfffffff000007947 */
      |                                                                                                   /* 0x000fc0000383ffff */
      |        /*0190*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01a0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01b0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01c0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01d0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01e0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01f0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hmma884Sm70Hmma884F16RowRowSource: String =
    """.version 7.8
      |.target sm_70
      |.address_size 64
      |.visible .entry hmma884_f16_row_row_probe(
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
      |mma.sync.aligned.m8n8k4.row.row.f16.f16.f16.f16 {%r9,%r10,%r11,%r12},{%r1,%r2},{%r3,%r4},{%r5,%r6,%r7,%r8};
      |add.u64 %rd6, %rd0, %rd5;
      |st.global.b32 [%rd6], %r9;
      |st.global.b32 [%rd6+4], %r10;
      |st.global.b32 [%rd6+8], %r11;
      |st.global.b32 [%rd6+12], %r12;
      |ret;
      |}
      |""".stripMargin

  val hmma884Sm70Hmma884F32ColColSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hmma884_f32_col_col_probe --------------------------
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
      |.L_x_0:
      |        /*0220*/                   BRA `(.L_x_0);                                                  /* 0xfffffff000007947 */
      |                                                                                                   /* 0x000fc0000383ffff */
      |        /*0230*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0240*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0250*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0260*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0270*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hmma884Sm70Hmma884F32ColColSource: String =
    """.version 7.8
      |.target sm_70
      |.address_size 64
      |.visible .entry hmma884_f32_col_col_probe(
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
      |mma.sync.aligned.m8n8k4.col.col.f32.f16.f16.f32 {%f8,%f9,%f10,%f11,%f12,%f13,%f14,%f15},{%r1,%r2},{%r3,%r4},{%f0,%f1,%f2,%f3,%f4,%f5,%f6,%f7};
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

  val hmma884Sm70Hmma884F32ColRowSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hmma884_f32_col_row_probe --------------------------
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
      |.L_x_0:
      |        /*0220*/                   BRA `(.L_x_0);                                                  /* 0xfffffff000007947 */
      |                                                                                                   /* 0x000fc0000383ffff */
      |        /*0230*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0240*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0250*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0260*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0270*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hmma884Sm70Hmma884F32ColRowSource: String =
    """.version 7.8
      |.target sm_70
      |.address_size 64
      |.visible .entry hmma884_f32_col_row_probe(
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
      |mma.sync.aligned.m8n8k4.col.row.f32.f16.f16.f32 {%f8,%f9,%f10,%f11,%f12,%f13,%f14,%f15},{%r1,%r2},{%r3,%r4},{%f0,%f1,%f2,%f3,%f4,%f5,%f6,%f7};
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

  val hmma884Sm70Hmma884F32RowColSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hmma884_f32_row_col_probe --------------------------
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
      |.L_x_0:
      |        /*0220*/                   BRA `(.L_x_0);                                                  /* 0xfffffff000007947 */
      |                                                                                                   /* 0x000fc0000383ffff */
      |        /*0230*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0240*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0250*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0260*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0270*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hmma884Sm70Hmma884F32RowColSource: String =
    """.version 7.8
      |.target sm_70
      |.address_size 64
      |.visible .entry hmma884_f32_row_col_probe(
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

  val hmma884Sm70Hmma884F32RowRowSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hmma884_f32_row_row_probe --------------------------
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
      |.L_x_0:
      |        /*0220*/                   BRA `(.L_x_0);                                                  /* 0xfffffff000007947 */
      |                                                                                                   /* 0x000fc0000383ffff */
      |        /*0230*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0240*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0250*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0260*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*0270*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hmma884Sm70Hmma884F32RowRowSource: String =
    """.version 7.8
      |.target sm_70
      |.address_size 64
      |.visible .entry hmma884_f32_row_row_probe(
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
      |mma.sync.aligned.m8n8k4.row.row.f32.f16.f16.f32 {%f8,%f9,%f10,%f11,%f12,%f13,%f14,%f15},{%r1,%r2},{%r3,%r4},{%f0,%f1,%f2,%f3,%f4,%f5,%f6,%f7};
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

  val hmma884Sm70Hmma884F32ZeroSm70: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM70 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM70)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.hmma884_f32_probe   --------------------------
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
      |.L_x_0:
      |        /*0190*/                   BRA `(.L_x_0);                                                  /* 0xfffffff000007947 */
      |                                                                                                   /* 0x000fc0000383ffff */
      |        /*01a0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01b0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01c0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01d0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01e0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |        /*01f0*/                   NOP;                                                            /* 0x0000000000007918 */
      |                                                                                                   /* 0x000fc00000000000 */
      |.L_x_1:
      |""".stripMargin

  val hmma884Sm70Hmma884F32ZeroSource: String =
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

}

class Hmma884Fixtures extends NvidiaFixtureSuite {
  import Hmma884Fixtures.*

  for (
    (name, source, expected) <- Vec(
      ("hmma884Sm70Hmma884F16ColColSm70", hmma884Sm70Hmma884F16ColColSource, hmma884Sm70Hmma884F16ColColSm70),
      ("hmma884Sm70Hmma884F16ColRowSm70", hmma884Sm70Hmma884F16ColRowSource, hmma884Sm70Hmma884F16ColRowSm70),
      ("hmma884Sm70Hmma884F16RowColSm70", hmma884Sm70Hmma884F16RowColSource, hmma884Sm70Hmma884F16RowColSm70),
      ("hmma884Sm70Hmma884F16RowRowSm70", hmma884Sm70Hmma884F16RowRowSource, hmma884Sm70Hmma884F16RowRowSm70),
      ("hmma884Sm70Hmma884F32ColColSm70", hmma884Sm70Hmma884F32ColColSource, hmma884Sm70Hmma884F32ColColSm70),
      ("hmma884Sm70Hmma884F32ColRowSm70", hmma884Sm70Hmma884F32ColRowSource, hmma884Sm70Hmma884F32ColRowSm70),
      ("hmma884Sm70Hmma884F32RowColSm70", hmma884Sm70Hmma884F32RowColSource, hmma884Sm70Hmma884F32RowColSm70),
      ("hmma884Sm70Hmma884F32RowRowSm70", hmma884Sm70Hmma884F32RowRowSource, hmma884Sm70Hmma884F32RowRowSm70),
      ("hmma884Sm70Hmma884F32ZeroSm70", hmma884Sm70Hmma884F32ZeroSource, hmma884Sm70Hmma884F32ZeroSm70)
    )
  ) reproduces(name)(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec("input.ptx" -> source),
    script = Vec(
      "ptxas -arch=sm_70 input.ptx -o kernel.cubin",
      "nvdisasm -c -ndf -hex kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = expected
  )

}
