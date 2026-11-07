package net.kurobako.gemstone.isa.sass.fixtures

object TextureListings {
  val listings: Map[String, String] = Map(
    "s_redSm35" ->
      """//--------------------- .text.s_red               --------------------------
        |	.section	.text.s_red,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	64
        |        .global         s_red
        |        .type           s_red,@function
        |        .size           s_red,(.L_x_14 - s_red)
        |        .other          s_red,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_red:
        |.text.s_red:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R10, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R10, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R10, 0x10;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   IADD32I R8.CC, R10, 0xc;
        |        /*0060*/                   IADD.X R9, RZ, R3;
        |        /*0068*/                   IADD32I R10.CC, R10, 0x4;
        |        /*0070*/                   IADD.X R11, RZ, R3;
        |        /*0078*/                   LDG.E.T R4, [R4];
        |        /*0088*/                   LDG.E.T R6, [R6];
        |        /*0090*/                   LDG.E.T R9, [R8];
        |        /*0098*/                   LDG.E.T R5, [R2];
        |        /*00a0*/                   LDG.E.P R10, [R10];
        |        /*00a8*/                   S2R R7, SR_TID.X;
        |        /*00b0*/                   SSY `(.L_x_0);
        |        /*00b8*/                   TEXDEPBAR 0x4;
        |        /*00c8*/                   SUCLAMP.SD.R4 PT, R4, R7, R4, 0x0;
        |        /*00d0*/                   IADD32I R7, R7, 0x5;
        |        /*00d8*/                   TEXDEPBAR 0x3;
        |        /*00e0*/                   SUCLAMP PT, R0, RZ, R6, 0x0;
        |        /*00e8*/                   TEXDEPBAR 0x2;
        |        /*00f0*/                   IMADSP.SD R9, R0, R9, R4;
        |        /*00f8*/                   SUBFM P0, R0, R4, R0, R9;
        |        /*0108*/                   TEXDEPBAR 0x1;
        |        /*0110*/                   SUEAU R9, R9, R0, R5;
        |        /*0118*/                   TEXDEPBAR 0x0;
        |        /*0120*/                   ISETP.GT.OR P0, PT, RZ, R10, P0;
        |        /*0128*/                   PRMT R2, RZ, 0x7, R9;
        |        /*0130*/                   PRMT R0, R0, 0x6540, R9;
        |        /*0138*/                   SEL R3, R2, RZ, !P0;
        |        /*0148*/                   SEL R2, R0, RZ, !P0;
        |        /*0150*/              @!P0 NOP.S                                         (*"TARGET= .L_x_0 "*);
        |        /*0158*/                   BPT.TRAP 0x1;
        |        /*0160*/                   NOP.S                                         (*"TARGET= .L_x_0 "*);
        |.L_x_0:
        |        /*0168*/                   BPT.TRAP 0x1;
        |        /*0170*/                   SUBFM P1, R0, R4, RZ, RZ;
        |        /*0178*/                   LOP32I.AND R4, R4, 0xffff;
        |        /*0188*/                   SSY `(.L_x_1);
        |        /*0190*/                   SUEAU R4, R4, R0, R5;
        |        /*0198*/                   ISETP.GT.OR P1, PT, RZ, R10, P1;
        |        /*01a0*/                   PRMT R5, RZ, 0x7, R4;
        |        /*01a8*/                   PRMT R0, R0, 0x6540, R4;
        |        /*01b0*/                   SEL R5, R5, RZ, !P1;
        |        /*01b8*/                   SEL R4, R0, RZ, !P1;
        |        /*01c8*/               @P0 NOP.S                                         (*"TARGET= .L_x_1 "*);
        |        /*01d0*/                   RED.E.ADD.S [R2], R7                          (*"TARGET= .L_x_1 "*);
        |.L_x_1:
        |        /*01d8*/                   MOV RZ, RZ;
        |        /*01e0*/               @P1 EXIT;
        |        /*01e8*/                   RED.E.MAX [R4], R7;
        |        /*01f0*/                   EXIT;
        |.L_x_2:
        |        /*01f8*/                   BRA `(.L_x_2);
        |.L_x_14:
        |""".stripMargin,
    "s_st32Sm35" ->
      """//--------------------- .text.s_st32              --------------------------
        |	.section	.text.s_st32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	64
        |        .global         s_st32
        |        .type           s_st32,@function
        |        .size           s_st32,(.L_x_15 - s_st32)
        |        .other          s_st32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_st32:
        |.text.s_st32:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R10, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R10, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R10, 0x10;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   IADD32I R8.CC, R10, 0xc;
        |        /*0060*/                   IADD.X R9, RZ, R3;
        |        /*0068*/                   IADD32I R10.CC, R10, 0x4;
        |        /*0070*/                   IADD.X R11, RZ, R3;
        |        /*0078*/                   LDG.E.T R4, [R4];
        |        /*0088*/                   LDG.E.T R6, [R6];
        |        /*0090*/                   LDG.E.T R9, [R8];
        |        /*0098*/                   LDG.E.T R8, [R2];
        |        /*00a0*/                   LDG.E.P R10, [R10];
        |        /*00a8*/                   S2R R0, SR_TID.X;
        |        /*00b0*/                   MOV32I R2, 0x1;
        |        /*00b8*/                   MOV R3, c[0x0][0x14c];
        |        /*00c8*/                   IADD32I R0, R0, 0x8;
        |        /*00d0*/                   MOV32I R11, 0xdeadbeef;
        |        /*00d8*/                   TEXDEPBAR 0x4;
        |        /*00e0*/                   SUCLAMP.SD.R4 PT, R0, R0, R4, 0x0;
        |        /*00e8*/                   TEXDEPBAR 0x3;
        |        /*00f0*/                   SUCLAMP PT, R4, R2, R6, 0x0;
        |        /*00f8*/                   MOV32I R2, 0x2;
        |        /*0108*/                   SUCLAMP PT, R6, R2, R6, 0x0;
        |        /*0110*/                   MOV R2, c[0x0][0x148];
        |        /*0118*/                   TEXDEPBAR 0x2;
        |        /*0120*/                   IMADSP.SD R5, R4, R9, R0;
        |        /*0128*/                   ST.E [R2], R11;
        |        /*0130*/                   IMADSP.SD R9, R6, R9, R0;
        |        /*0138*/                   SUBFM PT, R4, R0, R4, R5;
        |        /*0148*/                   SUBFM P0, R6, R0, R6, R9;
        |        /*0150*/                   TEXDEPBAR 0x1;
        |        /*0158*/                   SUEAU R5, R5, R4, R8;
        |        /*0160*/                   SUEAU R7, R9, R6, R8;
        |        /*0168*/                   TEXDEPBAR 0x0;
        |        /*0170*/                   SUSTGA.B.32.IGN.U8 [R4], R10, R11, !PT;
        |        /*0178*/                   SUSTGA.B.32.IGN.U8 [R6], R10, R11, P0;
        |        /*0188*/                   EXIT;
        |.L_x_3:
        |        /*0190*/                   BRA `(.L_x_3);
        |.L_x_15:
        |""".stripMargin,
    "s_ld128Sm35" ->
      """//--------------------- .text.s_ld128             --------------------------
        |	.section	.text.s_ld128,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=9"
        |	.align	64
        |        .global         s_ld128
        |        .type           s_ld128,@function
        |        .size           s_ld128,(.L_x_16 - s_ld128)
        |        .other          s_ld128,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld128:
        |.text.s_ld128:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R6, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R6, 0x4;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   LDG.E.T R4, [R4];
        |        /*0060*/                   LDG.E.T R2, [R2];
        |        /*0068*/                   LDG.E.P R8, [R6];
        |        /*0070*/                   S2R R0, SR_TID.X;
        |        /*0078*/                   MOV R3, c[0x0][0x14c];
        |        /*0088*/                   IADD32I R0, R0, 0x10;
        |        /*0090*/                   TEXDEPBAR 0x2;
        |        /*0098*/                   SUCLAMP.SD.R16 PT, R0, R0, R4, 0x0;
        |        /*00a0*/                   SUBFM P0, R4, R0, RZ, RZ;
        |        /*00a8*/                   LOP32I.AND R5, R0, 0xffff;
        |        /*00b0*/                   TEXDEPBAR 0x1;
        |        /*00b8*/                   SUEAU R5, R5, R4, R2;
        |        /*00c8*/                   TEXDEPBAR 0x0;
        |        /*00d0*/                   SULDGA.B.CA.128.Z.U8 R4, [R4], R8, P0;
        |        /*00d8*/                   MOV R2, c[0x0][0x148];
        |        /*00e0*/                   ST.E.128 [R2], R4;
        |        /*00e8*/                   EXIT;
        |.L_x_4:
        |        /*00f0*/                   BRA `(.L_x_4);
        |.L_x_16:
        |""".stripMargin,
    "s_ld64Sm35" ->
      """//--------------------- .text.s_ld64              --------------------------
        |	.section	.text.s_ld64,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	64
        |        .global         s_ld64
        |        .type           s_ld64,@function
        |        .size           s_ld64,(.L_x_17 - s_ld64)
        |        .other          s_ld64,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld64:
        |.text.s_ld64:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R6, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R6, 0x4;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   LDG.E.T R4, [R4];
        |        /*0060*/                   LDG.E.T R5, [R2];
        |        /*0068*/                   LDG.E.P R6, [R6];
        |        /*0070*/                   S2R R0, SR_TID.X;
        |        /*0078*/                   IADD32I R0, R0, 0x8;
        |        /*0088*/                   TEXDEPBAR 0x2;
        |        /*0090*/                   SUCLAMP.SD.R8 PT, R0, R0, R4, 0x0;
        |        /*0098*/                   MOV R4, c[0x0][0x148];
        |        /*00a0*/                   SUBFM P0, R2, R0, RZ, RZ;
        |        /*00a8*/                   LOP32I.AND R3, R0, 0xffff;
        |        /*00b0*/                   TEXDEPBAR 0x1;
        |        /*00b8*/                   SUEAU R3, R3, R2, R5;
        |        /*00c8*/                   TEXDEPBAR 0x0;
        |        /*00d0*/                   SULDGA.B.CA.64.TRAP.U8 R2, [R2], R6, P0;
        |        /*00d8*/                   MOV R5, c[0x0][0x14c];
        |        /*00e0*/                   ST.E.64 [R4], R2;
        |        /*00e8*/                   EXIT;
        |.L_x_5:
        |        /*00f0*/                   BRA `(.L_x_5);
        |.L_x_17:
        |""".stripMargin,
    "s_ld8Sm35" ->
      """//--------------------- .text.s_ld8               --------------------------
        |	.section	.text.s_ld8,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	64
        |        .global         s_ld8
        |        .type           s_ld8,@function
        |        .size           s_ld8,(.L_x_18 - s_ld8)
        |        .other          s_ld8,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld8:
        |.text.s_ld8:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R10, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R10, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R10, 0x10;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   IADD32I R8.CC, R10, 0xc;
        |        /*0060*/                   IADD.X R9, RZ, R3;
        |        /*0068*/                   IADD32I R10.CC, R10, 0x4;
        |        /*0070*/                   IADD.X R11, RZ, R3;
        |        /*0078*/                   LDG.E.T R4, [R4];
        |        /*0088*/                   LDG.E.T R6, [R6];
        |        /*0090*/                   LDG.E.T R9, [R8];
        |        /*0098*/                   LDG.E.T R7, [R2];
        |        /*00a0*/                   LDG.E.P R10, [R10];
        |        /*00a8*/                   S2R R0, SR_TID.X;
        |        /*00b0*/                   MOV32I R2, 0x1;
        |        /*00b8*/                   IADD32I R0, R0, 0x6;
        |        /*00c8*/                   TEXDEPBAR 0x4;
        |        /*00d0*/                   SUCLAMP PT, R0, R0, R4, 0x0;
        |        /*00d8*/                   MOV R3, c[0x0][0x14c];
        |        /*00e0*/                   TEXDEPBAR 0x3;
        |        /*00e8*/                   SUCLAMP PT, R6, R2, R6, 0x0;
        |        /*00f0*/                   MOV R2, c[0x0][0x148];
        |        /*00f8*/                   TEXDEPBAR 0x2;
        |        /*0108*/                   IMADSP.SD R9, R6, R9, R0;
        |        /*0110*/                   SUBFM PT, R6, R0, R6, R9;
        |        /*0118*/                   TEXDEPBAR 0x1;
        |        /*0120*/                   SUEAU R7, R9, R6, R7;
        |        /*0128*/                   TEXDEPBAR 0x0;
        |        /*0130*/                   SULDGA.B.CA.U8.Z.U8 R0, [R6], R10, !PT;
        |        /*0138*/                   LOP32I.AND R4, R0, 0xff;
        |        /*0148*/                   ST.E [R2], R4;
        |        /*0150*/                   EXIT;
        |.L_x_6:
        |        /*0158*/                   BRA `(.L_x_6);
        |.L_x_18:
        |""".stripMargin,
    "query_dimsSm35" ->
      """//--------------------- .text.query_dims          --------------------------
        |	.section	.text.query_dims,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	64
        |        .global         query_dims
        |        .type           query_dims,@function
        |        .size           query_dims,(.L_x_19 - query_dims)
        |        .other          query_dims,@"STO_CUDA_ENTRY STV_DEFAULT"
        |query_dims:
        |.text.query_dims:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   TXQ.T R4, RZ, TEX_HEADER_DIMENSION, 0x50, 0x1;
        |        /*0018*/                   TXQ.T R5, RZ, TEX_HEADER_DIMENSION, 0x50, 0x2;
        |        /*0020*/                   TXQ.P R6, RZ, TEX_HEADER_DIMENSION, 0x50, 0x4;
        |        /*0028*/                   MOV R2, c[0x0][0x148];
        |        /*0030*/                   MOV R3, c[0x0][0x14c];
        |        /*0038*/                   TEXDEPBAR 0x1;
        |        /*0048*/                   ST.E.64 [R2], R4;
        |        /*0050*/                   TEXDEPBAR 0x0;
        |        /*0058*/                   ST.E [R2+0x8], R6;
        |        /*0060*/                   EXIT;
        |.L_x_7:
        |        /*0068*/                   BRA `(.L_x_7);
        |.L_x_19:
        |""".stripMargin,
    "gather_bSm35" ->
      """//--------------------- .text.gather_b            --------------------------
        |	.section	.text.gather_b,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	64
        |        .global         gather_b
        |        .type           gather_b,@function
        |        .size           gather_b,(.L_x_20 - gather_b)
        |        .other          gather_b,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gather_b:
        |.text.gather_b:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV32I R4, 0x3fc00000;
        |        /*0018*/                   MOV32I R5, 0x40200000;
        |        /*0020*/                   TLD4.B.P R4, R4, RZ, 0x50, 2D, 0xf;
        |        /*0028*/                   MOV R2, c[0x0][0x148];
        |        /*0030*/                   MOV R3, c[0x0][0x14c];
        |        /*0038*/                   TEXDEPBAR 0x0;
        |        /*0048*/                   ST.E.128 [R2], R4;
        |        /*0050*/                   EXIT;
        |.L_x_8:
        |        /*0058*/                   BRA `(.L_x_8);
        |.L_x_20:
        |""".stripMargin,
    "samp2d_gSm35" ->
      """//--------------------- .text.samp2d_g            --------------------------
        |	.section	.text.samp2d_g,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	64
        |        .global         samp2d_g
        |        .type           samp2d_g,@function
        |        .size           samp2d_g,(.L_x_21 - samp2d_g)
        |        .other          samp2d_g,@"STO_CUDA_ENTRY STV_DEFAULT"
        |samp2d_g:
        |.text.samp2d_g:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV32I R4, 0x3fc00000;
        |        /*0018*/                   MOV32I R5, 0x40200000;
        |        /*0020*/                   TEX.P R4, R4, RZ, 0x50, 2D, 0x2;
        |        /*0028*/                   MOV R2, c[0x0][0x148];
        |        /*0030*/                   MOV R3, c[0x0][0x14c];
        |        /*0038*/                   TEXDEPBAR 0x0;
        |        /*0048*/                   ST.E [R2], R4;
        |        /*0050*/                   EXIT;
        |.L_x_9:
        |        /*0058*/                   BRA `(.L_x_9);
        |.L_x_21:
        |""".stripMargin,
    "samp1dSm35" ->
      """//--------------------- .text.samp1d              --------------------------
        |	.section	.text.samp1d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	64
        |        .global         samp1d
        |        .type           samp1d,@function
        |        .size           samp1d,(.L_x_22 - samp1d)
        |        .other          samp1d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |samp1d:
        |.text.samp1d:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV32I R4, 0x40200000;
        |        /*0018*/                   MOV R5, RZ;
        |        /*0020*/                   TEX.P R4, R4, RZ, 0x50, 2D, 0xf;
        |        /*0028*/                   MOV R2, c[0x0][0x148];
        |        /*0030*/                   MOV R3, c[0x0][0x14c];
        |        /*0038*/                   TEXDEPBAR 0x0;
        |        /*0048*/                   ST.E.128 [R2], R4;
        |        /*0050*/                   EXIT;
        |.L_x_10:
        |        /*0058*/                   BRA `(.L_x_10);
        |.L_x_22:
        |""".stripMargin,
    "fetch1dSm35" ->
      """//--------------------- .text.fetch1d             --------------------------
        |	.section	.text.fetch1d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	64
        |        .global         fetch1d
        |        .type           fetch1d,@function
        |        .size           fetch1d,(.L_x_23 - fetch1d)
        |        .other          fetch1d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch1d:
        |.text.fetch1d:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   S2R R0, SR_TID.X;
        |        /*0018*/                   IADD32I R0, R0, 0x2;
        |        /*0020*/                   TLD.LZ.P R4, R0, RZ, 0x50, 1D, 0xf;
        |        /*0028*/                   MOV R2, c[0x0][0x148];
        |        /*0030*/                   MOV R3, c[0x0][0x14c];
        |        /*0038*/                   TEXDEPBAR 0x0;
        |        /*0048*/                   ST.E.128 [R2], R4;
        |        /*0050*/                   EXIT;
        |.L_x_11:
        |        /*0058*/                   BRA `(.L_x_11);
        |.L_x_23:
        |""".stripMargin,
    "fetch3dSm35" ->
      """//--------------------- .text.fetch3d             --------------------------
        |	.section	.text.fetch3d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	64
        |        .global         fetch3d
        |        .type           fetch3d,@function
        |        .size           fetch3d,(.L_x_24 - fetch3d)
        |        .other          fetch3d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch3d:
        |.text.fetch3d:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   S2R R4, SR_TID.X;
        |        /*0018*/                   MOV32I R7, 0x231;
        |        /*0020*/                   MOV R5, RZ;
        |        /*0028*/                   MOV R6, RZ;
        |        /*0030*/                   TLD.LZ.AOFFI.P R4, R4, RZ, 0x50, 3D, 0xf;
        |        /*0038*/                   MOV R2, c[0x0][0x148];
        |        /*0048*/                   MOV R3, c[0x0][0x14c];
        |        /*0050*/                   TEXDEPBAR 0x0;
        |        /*0058*/                   ST.E.128 [R2], R4;
        |        /*0060*/                   EXIT;
        |.L_x_12:
        |        /*0068*/                   BRA `(.L_x_12);
        |.L_x_24:
        |""".stripMargin,
    "fetch2dSm35" ->
      """//--------------------- .text.fetch2d             --------------------------
        |	.section	.text.fetch2d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	64
        |        .global         fetch2d
        |        .type           fetch2d,@function
        |        .size           fetch2d,(.L_x_25 - fetch2d)
        |        .other          fetch2d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch2d:
        |.text.fetch2d:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   S2R R4, SR_TID.X;
        |        /*0018*/                   MOV32I R6, 0x31;
        |        /*0020*/                   MOV R5, RZ;
        |        /*0028*/                   TLD.LZ.AOFFI.P R4, R4, RZ, 0x50, 2D, 0xf;
        |        /*0030*/                   MOV R2, c[0x0][0x148];
        |        /*0038*/                   MOV R3, c[0x0][0x14c];
        |        /*0048*/                   TEXDEPBAR 0x0;
        |        /*0050*/                   ST.E.128 [R2], R4;
        |        /*0058*/                   EXIT;
        |.L_x_13:
        |        /*0060*/                   BRA `(.L_x_13);
        |.L_x_25:
        |""".stripMargin,
    "s_redSm50" ->
      """//--------------------- .text.s_red               --------------------------
        |	.section	.text.s_red,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=4"
        |	.align	32
        |        .global         s_red
        |        .type           s_red,@function
        |        .size           s_red,(.L_x_12 - s_red)
        |        .other          s_red,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_red:
        |.text.s_red:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/         {         MOV R3, RZ ;
        |        /*0018*/                   S2R R2, SR_TID.X         }
        |        /*0028*/                   IADD32I R0, R2, 0x5 ;
        |        /*0030*/                   SURED.D.BA.2D.ADD.TRAP [R2], R0, 0x50 ;
        |        /*0038*/                   SURED.D.BA.1D.MAX.IGN [R2], R0, 0x50 ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   NOP ;
        |        /*0058*/                   EXIT ;
        |.L_x_0:
        |        /*0068*/                   BRA `(.L_x_0) ;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_12:
        |""".stripMargin,
    "s_st32Sm50" ->
      """//--------------------- .text.s_st32              --------------------------
        |	.section	.text.s_st32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	32
        |        .global         s_st32
        |        .type           s_st32,@function
        |        .size           s_st32,(.L_x_13 - s_st32)
        |        .other          s_st32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_st32:
        |.text.s_st32:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/         {         MOV32I R7, 0xdeadbeef ;
        |        /*0018*/                   S2R R4, SR_TID.X         }
        |        /*0028*/                   MOV32I R5, 0x1 ;
        |        /*0030*/                   MOV R2, c[0x0][0x148] ;
        |        /*0038*/                   MOV R3, c[0x0][0x14c] ;
        |        /*0048*/         {         MOV32I R9, 0x2 ;
        |        /*0050*/                   STG.E [R2], R7         }
        |        /*0058*/                   IADD32I R4, R4, 0x8 ;
        |        /*0068*/                   SUST.D.BA.2D [R4], R7, 0x50 ;
        |        /*0070*/                   MOV R8, R4 ;
        |        /*0078*/                   SUST.D.BA.2D.IGN [R8], R7, 0x50 ;
        |        /*0088*/                   NOP ;
        |        /*0090*/                   EXIT ;
        |.L_x_1:
        |        /*0098*/                   BRA `(.L_x_1) ;
        |        /*00a8*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00b8*/                   NOP;
        |.L_x_13:
        |""".stripMargin,
    "s_ld128Sm50" ->
      """//--------------------- .text.s_ld128             --------------------------
        |	.section	.text.s_ld128,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         s_ld128
        |        .type           s_ld128,@function
        |        .size           s_ld128,(.L_x_14 - s_ld128)
        |        .other          s_ld128,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld128:
        |.text.s_ld128:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/         {         MOV R2, c[0x0][0x148] ;
        |        /*0018*/                   S2R R0, SR_TID.X         }
        |        /*0028*/                   IADD32I R0, R0, 0x10 ;
        |        /*0030*/                   SULD.D.BA.1D.128.IGN R4, [R0], 0x50 ;
        |        /*0038*/                   MOV R3, c[0x0][0x14c] ;
        |        /*0048*/                   STG.E.128 [R2], R4 ;
        |        /*0050*/                   NOP ;
        |        /*0058*/                   NOP ;
        |        /*0068*/                   EXIT ;
        |.L_x_2:
        |        /*0070*/                   BRA `(.L_x_2) ;
        |        /*0078*/                   NOP;
        |.L_x_14:
        |""".stripMargin,
    "s_ld64Sm50" ->
      """//--------------------- .text.s_ld64              --------------------------
        |	.section	.text.s_ld64,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	32
        |        .global         s_ld64
        |        .type           s_ld64,@function
        |        .size           s_ld64,(.L_x_15 - s_ld64)
        |        .other          s_ld64,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld64:
        |.text.s_ld64:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/         {         MOV R4, c[0x0][0x148] ;
        |        /*0018*/                   S2R R2, SR_TID.X         }
        |        /*0028*/                   IADD32I R2, R2, 0x8 ;
        |        /*0030*/                   SULD.D.BA.1D.64.TRAP R2, [R2], 0x50 ;
        |        /*0038*/                   MOV R5, c[0x0][0x14c] ;
        |        /*0048*/                   STG.E.64 [R4], R2 ;
        |        /*0050*/                   NOP ;
        |        /*0058*/                   NOP ;
        |        /*0068*/                   EXIT ;
        |.L_x_3:
        |        /*0070*/                   BRA `(.L_x_3) ;
        |        /*0078*/                   NOP;
        |.L_x_15:
        |""".stripMargin,
    "s_ld8Sm50" ->
      """//--------------------- .text.s_ld8               --------------------------
        |	.section	.text.s_ld8,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	32
        |        .global         s_ld8
        |        .type           s_ld8,@function
        |        .size           s_ld8,(.L_x_16 - s_ld8)
        |        .other          s_ld8,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld8:
        |.text.s_ld8:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/         {         MOV32I R5, 0x1 ;
        |        /*0018*/                   S2R R4, SR_TID.X         }
        |        /*0028*/                   IADD32I R4, R4, 0x6 ;
        |        /*0030*/                   SULD.D.BA.2D.U8 R4, [R4], 0x50 ;
        |        /*0038*/                   MOV R2, c[0x0][0x148] ;
        |        /*0048*/                   MOV R3, c[0x0][0x14c] ;
        |        /*0050*/                   LOP32I.AND R0, R4, 0xff ;
        |        /*0058*/                   STG.E [R2], R0 ;
        |        /*0068*/                   NOP ;
        |        /*0070*/                   NOP ;
        |        /*0078*/                   EXIT ;
        |.L_x_4:
        |        /*0088*/                   BRA `(.L_x_4) ;
        |        /*0090*/                   NOP;
        |        /*0098*/                   NOP;
        |        /*00a8*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00b8*/                   NOP;
        |.L_x_16:
        |""".stripMargin,
    "query_dimsSm50" ->
      """//--------------------- .text.query_dims          --------------------------
        |	.section	.text.query_dims,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	32
        |        .global         query_dims
        |        .type           query_dims,@function
        |        .size           query_dims,(.L_x_17 - query_dims)
        |        .other          query_dims,@"STO_CUDA_ENTRY STV_DEFAULT"
        |query_dims:
        |.text.query_dims:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   MOV R4, RZ ;
        |        /*0018*/         {         MOV R5, RZ ;
        |        /*0028*/                   TXQ.T R4, R4, TEX_HEADER_DIMENSION, 0x50, 0x1         }
        |        /*0030*/         {         MOV R0, RZ ;
        |        /*0038*/                   TXQ.T R5, R5, TEX_HEADER_DIMENSION, 0x50, 0x2         }
        |        /*0048*/                   TXQ.P R0, R0, TEX_HEADER_DIMENSION, 0x50, 0x4 ;
        |        /*0050*/                   MOV R2, c[0x0][0x148] ;
        |        /*0058*/                   MOV R3, c[0x0][0x14c] ;
        |        /*0068*/                   STG.E.64 [R2], R4 ;
        |        /*0070*/                   STG.E [R2+0x8], R0 ;
        |        /*0078*/                   NOP ;
        |        /*0088*/                   NOP ;
        |        /*0090*/                   NOP ;
        |        /*0098*/                   EXIT ;
        |.L_x_5:
        |        /*00a8*/                   BRA `(.L_x_5) ;
        |        /*00b0*/                   NOP;
        |        /*00b8*/                   NOP;
        |.L_x_17:
        |""".stripMargin,
    "gather_bSm50" ->
      """//--------------------- .text.gather_b            --------------------------
        |	.section	.text.gather_b,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         gather_b
        |        .type           gather_b,@function
        |        .size           gather_b,(.L_x_18 - gather_b)
        |        .other          gather_b,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gather_b:
        |.text.gather_b:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   MOV32I R4, 0x3fc00000 ;
        |        /*0018*/                   MOV32I R5, 0x40200000 ;
        |        /*0028*/                   TLD4S.B.P R6, R4, R4, R5, 0x50;
        |        /*0030*/                   MOV R2, c[0x0][0x148] ;
        |        /*0038*/                   MOV R3, c[0x0][0x14c] ;
        |        /*0048*/                   STG.E.128 [R2], R4 ;
        |        /*0050*/                   NOP ;
        |        /*0058*/                   NOP ;
        |        /*0068*/                   NOP ;
        |        /*0070*/                   EXIT ;
        |.L_x_6:
        |        /*0078*/                   BRA `(.L_x_6) ;
        |.L_x_18:
        |""".stripMargin,
    "samp2d_gSm50" ->
      """//--------------------- .text.samp2d_g            --------------------------
        |	.section	.text.samp2d_g,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	32
        |        .global         samp2d_g
        |        .type           samp2d_g,@function
        |        .size           samp2d_g,(.L_x_19 - samp2d_g)
        |        .other          samp2d_g,@"STO_CUDA_ENTRY STV_DEFAULT"
        |samp2d_g:
        |.text.samp2d_g:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   MOV32I R0, 0x3fc00000 ;
        |        /*0018*/                   MOV32I R5, 0x40200000 ;
        |        /*0028*/                   TEXS.P RZ, R0, R0, R5, 0x50, 2D, G;
        |        /*0030*/                   MOV R2, c[0x0][0x148] ;
        |        /*0038*/                   MOV R3, c[0x0][0x14c] ;
        |        /*0048*/                   STG.E [R2], R0 ;
        |        /*0050*/                   NOP ;
        |        /*0058*/                   NOP ;
        |        /*0068*/                   NOP ;
        |        /*0070*/                   EXIT ;
        |.L_x_7:
        |        /*0078*/                   BRA `(.L_x_7) ;
        |.L_x_19:
        |""".stripMargin,
    "samp1dSm50" ->
      """//--------------------- .text.samp1d              --------------------------
        |	.section	.text.samp1d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         samp1d
        |        .type           samp1d,@function
        |        .size           samp1d,(.L_x_20 - samp1d)
        |        .other          samp1d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |samp1d:
        |.text.samp1d:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   MOV32I R4, 0x40200000 ;
        |        /*0018*/                   MOV R5, RZ ;
        |        /*0028*/                   TEXS.P R6, R4, R4, R5, 0x50, 2D, RGBA;
        |        /*0030*/                   MOV R2, c[0x0][0x148] ;
        |        /*0038*/                   MOV R3, c[0x0][0x14c] ;
        |        /*0048*/                   STG.E.128 [R2], R4 ;
        |        /*0050*/                   NOP ;
        |        /*0058*/                   NOP ;
        |        /*0068*/                   NOP ;
        |        /*0070*/                   EXIT ;
        |.L_x_8:
        |        /*0078*/                   BRA `(.L_x_8) ;
        |.L_x_20:
        |""".stripMargin,
    "fetch1dSm50" ->
      """//--------------------- .text.fetch1d             --------------------------
        |	.section	.text.fetch1d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         fetch1d
        |        .type           fetch1d,@function
        |        .size           fetch1d,(.L_x_21 - fetch1d)
        |        .other          fetch1d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch1d:
        |.text.fetch1d:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/         {         MOV R2, c[0x0][0x148] ;
        |        /*0018*/                   S2R R0, SR_TID.X         }
        |        /*0028*/                   IADD32I R0, R0, 0x2 ;
        |        /*0030*/                   TLDS.LZ.P R6, R4, R0, RZ, 0x50, 1D, RGBA;
        |        /*0038*/                   MOV R3, c[0x0][0x14c] ;
        |        /*0048*/                   STG.E.128 [R2], R4 ;
        |        /*0050*/                   NOP ;
        |        /*0058*/                   NOP ;
        |        /*0068*/                   EXIT ;
        |.L_x_9:
        |        /*0070*/                   BRA `(.L_x_9) ;
        |        /*0078*/                   NOP;
        |.L_x_21:
        |""".stripMargin,
    "fetch3dSm50" ->
      """//--------------------- .text.fetch3d             --------------------------
        |	.section	.text.fetch3d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         fetch3d
        |        .type           fetch3d,@function
        |        .size           fetch3d,(.L_x_22 - fetch3d)
        |        .other          fetch3d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch3d:
        |.text.fetch3d:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/         {         MOV32I R7, 0x231 ;
        |        /*0018*/                   S2R R4, SR_TID.X         }
        |        /*0028*/                   MOV R5, RZ ;
        |        /*0030*/                   MOV R6, RZ ;
        |        /*0038*/                   TLD.LZ.AOFFI.P R4, R4, R7, 0x50, 3D, 0xf ;
        |        /*0048*/                   MOV R2, c[0x0][0x148] ;
        |        /*0050*/                   MOV R3, c[0x0][0x14c] ;
        |        /*0058*/                   STG.E.128 [R2], R4 ;
        |        /*0068*/                   NOP ;
        |        /*0070*/                   NOP ;
        |        /*0078*/                   EXIT ;
        |.L_x_10:
        |        /*0088*/                   BRA `(.L_x_10) ;
        |        /*0090*/                   NOP;
        |        /*0098*/                   NOP;
        |        /*00a8*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00b8*/                   NOP;
        |.L_x_22:
        |""".stripMargin,
    "fetch2dSm50" ->
      """//--------------------- .text.fetch2d             --------------------------
        |	.section	.text.fetch2d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	32
        |        .global         fetch2d
        |        .type           fetch2d,@function
        |        .size           fetch2d,(.L_x_23 - fetch2d)
        |        .other          fetch2d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch2d:
        |.text.fetch2d:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/         {         MOV32I R6, 0x31 ;
        |        /*0018*/                   S2R R4, SR_TID.X         }
        |        /*0028*/                   MOV R5, RZ ;
        |        /*0030*/                   TLDS.LZ.AOFFI.P R6, R4, R4, R6, 0x50, 2D, RGBA;
        |        /*0038*/                   MOV R2, c[0x0][0x148] ;
        |        /*0048*/                   MOV R3, c[0x0][0x14c] ;
        |        /*0050*/                   STG.E.128 [R2], R4 ;
        |        /*0058*/                   NOP ;
        |        /*0068*/                   NOP ;
        |        /*0070*/                   EXIT ;
        |.L_x_11:
        |        /*0078*/                   BRA `(.L_x_11) ;
        |.L_x_23:
        |""".stripMargin,
    "s_redSm70" ->
      """//--------------------- .text.s_red               --------------------------
        |	.section	.text.s_red,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	128
        |        .global         s_red
        |        .type           s_red,@function
        |        .size           s_red,(.L_x_12 - s_red)
        |        .other          s_red,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_red:
        |.text.s_red:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;
        |        /*0020*/                   S2R R2, SR_TID.X ;
        |        /*0030*/                   MOV R3, RZ ;
        |        /*0040*/                   IADD3 R0, R2, 0x5, RZ ;
        |        /*0050*/                   SURED.D.BA.2D.ADD.STRONG.SYS.TRAP [R2], R0, 0x0, 0x58 ;
        |        /*0060*/                   SURED.D.BA.1D.MAX.STRONG.SYS.IGN [R2], R0, 0x0, 0x58 ;
        |        /*0070*/                   EXIT ;
        |.L_x_0:
        |        /*0080*/                   BRA `(.L_x_0);
        |        /*0090*/                   NOP;
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_12:
        |""".stripMargin,
    "s_st32Sm70" ->
      """//--------------------- .text.s_st32              --------------------------
        |	.section	.text.s_st32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         s_st32
        |        .type           s_st32,@function
        |        .size           s_st32,(.L_x_13 - s_st32)
        |        .other          s_st32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_st32:
        |.text.s_st32:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;
        |        /*0020*/                   S2R R4, SR_TID.X ;
        |        /*0030*/                   MOV R7, 0xdeadbeef ;
        |        /*0040*/                   IMAD.MOV.U32 R3, RZ, RZ, c[0x0][0x16c] ;
        |        /*0050*/                   MOV R2, c[0x0][0x168] ;
        |        /*0060*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x1 ;
        |        /*0070*/                   MOV R9, 0x2 ;
        |        /*0080*/                   STG.E.SYS [R2], R7 ;
        |        /*0090*/                   IADD3 R4, R4, 0x8, RZ ;
        |        /*00a0*/                   IMAD.MOV.U32 R8, RZ, RZ, R4 ;
        |        /*00b0*/                   SUST.D.BA.2D.STRONG.CTA [R4], R7, 0x0, 0x58 ;
        |        /*00c0*/                   SUST.D.BA.2D.STRONG.CTA.IGN [R8], R7, 0x0, 0x58 ;
        |        /*00d0*/                   EXIT ;
        |.L_x_1:
        |        /*00e0*/                   BRA `(.L_x_1);
        |        /*00f0*/                   NOP;
        |.L_x_13:
        |""".stripMargin,
    "s_ld128Sm70" ->
      """//--------------------- .text.s_ld128             --------------------------
        |	.section	.text.s_ld128,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         s_ld128
        |        .type           s_ld128,@function
        |        .size           s_ld128,(.L_x_14 - s_ld128)
        |        .other          s_ld128,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld128:
        |.text.s_ld128:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;
        |        /*0020*/                   S2R R0, SR_TID.X ;
        |        /*0030*/                   IADD3 R0, R0, 0x10, RZ ;
        |        /*0040*/                   SULD.D.BA.1D.128.STRONG.CTA.IGN R4, [R0], 0x0, 0x58 ;
        |        /*0050*/                   MOV R2, c[0x0][0x168] ;
        |        /*0060*/                   IMAD.MOV.U32 R3, RZ, RZ, c[0x0][0x16c] ;
        |        /*0070*/                   STG.E.128.SYS [R2], R4 ;
        |        /*0080*/                   EXIT ;
        |.L_x_2:
        |        /*0090*/                   BRA `(.L_x_2);
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_14:
        |""".stripMargin,
    "s_ld64Sm70" ->
      """//--------------------- .text.s_ld64              --------------------------
        |	.section	.text.s_ld64,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         s_ld64
        |        .type           s_ld64,@function
        |        .size           s_ld64,(.L_x_15 - s_ld64)
        |        .other          s_ld64,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld64:
        |.text.s_ld64:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;
        |        /*0020*/                   S2R R2, SR_TID.X ;
        |        /*0030*/                   IADD3 R2, R2, 0x8, RZ ;
        |        /*0040*/                   SULD.D.BA.1D.64.STRONG.CTA.TRAP R2, [R2], 0x0, 0x58 ;
        |        /*0050*/                   MOV R4, c[0x0][0x168] ;
        |        /*0060*/                   IMAD.MOV.U32 R5, RZ, RZ, c[0x0][0x16c] ;
        |        /*0070*/                   STG.E.64.SYS [R4], R2 ;
        |        /*0080*/                   EXIT ;
        |.L_x_3:
        |        /*0090*/                   BRA `(.L_x_3);
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_15:
        |""".stripMargin,
    "s_ld8Sm70" ->
      """//--------------------- .text.s_ld8               --------------------------
        |	.section	.text.s_ld8,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         s_ld8
        |        .type           s_ld8,@function
        |        .size           s_ld8,(.L_x_16 - s_ld8)
        |        .other          s_ld8,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld8:
        |.text.s_ld8:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;
        |        /*0020*/                   S2R R4, SR_TID.X ;
        |        /*0030*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x1 ;
        |        /*0040*/                   IADD3 R4, R4, 0x6, RZ ;
        |        /*0050*/                   SULD.D.BA.2D.U8.STRONG.CTA R4, [R4], 0x0, 0x58 ;
        |        /*0060*/                   MOV R2, c[0x0][0x168] ;
        |        /*0070*/                   IMAD.MOV.U32 R3, RZ, RZ, c[0x0][0x16c] ;
        |        /*0080*/                   LOP3.LUT R7, R4, 0xff, RZ, 0xc0, !PT ;
        |        /*0090*/                   STG.E.SYS [R2], R7 ;
        |        /*00a0*/                   EXIT ;
        |.L_x_4:
        |        /*00b0*/                   BRA `(.L_x_4);
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_16:
        |""".stripMargin,
    "query_dimsSm70" ->
      """//--------------------- .text.query_dims          --------------------------
        |	.section	.text.query_dims,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         query_dims
        |        .type           query_dims,@function
        |        .size           query_dims,(.L_x_17 - query_dims)
        |        .other          query_dims,@"STO_CUDA_ENTRY STV_DEFAULT"
        |query_dims:
        |.text.query_dims:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;
        |        /*0020*/                   CS2R R4, SRZ ;
        |        /*0030*/                   IMAD.MOV.U32 R7, RZ, RZ, RZ ;
        |        /*0040*/                   TXQ RZ, R4, R4, TEX_HEADER_DIMENSION, 0x0, 0x58, 0x1 ;
        |        /*0050*/                   TXQ RZ, R5, R5, TEX_HEADER_DIMENSION, 0x0, 0x58, 0x2 ;
        |        /*0060*/                   TXQ RZ, R7, R7, TEX_HEADER_DIMENSION, 0x0, 0x58, 0x4 ;
        |        /*0070*/                   MOV R2, c[0x0][0x168] ;
        |        /*0080*/                   MOV R3, c[0x0][0x16c] ;
        |        /*0090*/                   STG.E.64.SYS [R2], R4 ;
        |        /*00a0*/                   STG.E.SYS [R2+0x8], R7 ;
        |        /*00b0*/                   EXIT ;
        |.L_x_5:
        |        /*00c0*/                   BRA `(.L_x_5);
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_17:
        |""".stripMargin,
    "gather_bSm70" ->
      """//--------------------- .text.gather_b            --------------------------
        |	.section	.text.gather_b,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         gather_b
        |        .type           gather_b,@function
        |        .size           gather_b,(.L_x_18 - gather_b)
        |        .other          gather_b,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gather_b:
        |.text.gather_b:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;
        |        /*0020*/                   MOV R4, 0x3fc00000 ;
        |        /*0030*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x40200000 ;
        |        /*0040*/                   TLD4.SCR.B R6, R4, R4, R5, 0x0, 0x58, 2D ;
        |        /*0050*/                   MOV R2, c[0x0][0x168] ;
        |        /*0060*/                   IMAD.MOV.U32 R3, RZ, RZ, c[0x0][0x16c] ;
        |        /*0070*/                   STG.E.128.SYS [R2], R4 ;
        |        /*0080*/                   EXIT ;
        |.L_x_6:
        |        /*0090*/                   BRA `(.L_x_6);
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_18:
        |""".stripMargin,
    "samp2d_gSm70" ->
      """//--------------------- .text.samp2d_g            --------------------------
        |	.section	.text.samp2d_g,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         samp2d_g
        |        .type           samp2d_g,@function
        |        .size           samp2d_g,(.L_x_19 - samp2d_g)
        |        .other          samp2d_g,@"STO_CUDA_ENTRY STV_DEFAULT"
        |samp2d_g:
        |.text.samp2d_g:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;
        |        /*0020*/                   IMAD.MOV.U32 R0, RZ, RZ, -0x3e000000 ;
        |        /*0030*/                   MOV R4, 0x3fc00000 ;
        |        /*0040*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x40200000 ;
        |        /*0050*/                   TEX.SCR.LL RZ, R5, R4, R0, 0x0, 0x58, 2D, 0x2 ;
        |        /*0060*/                   MOV R2, c[0x0][0x168] ;
        |        /*0070*/                   MOV R3, c[0x0][0x16c] ;
        |        /*0080*/                   STG.E.SYS [R2], R5 ;
        |        /*0090*/                   EXIT ;
        |.L_x_7:
        |        /*00a0*/                   BRA `(.L_x_7);
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_19:
        |""".stripMargin,
    "samp1dSm70" ->
      """//--------------------- .text.samp1d              --------------------------
        |	.section	.text.samp1d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         samp1d
        |        .type           samp1d,@function
        |        .size           samp1d,(.L_x_20 - samp1d)
        |        .other          samp1d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |samp1d:
        |.text.samp1d:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;
        |        /*0020*/                   IMAD.MOV.U32 R6, RZ, RZ, -0x3e000000 ;
        |        /*0030*/                   MOV R4, 0x40200000 ;
        |        /*0040*/                   IMAD.MOV.U32 R5, RZ, RZ, RZ ;
        |        /*0050*/                   TEX.SCR.LL R6, R4, R4, R6, 0x0, 0x58, 2D ;
        |        /*0060*/                   MOV R2, c[0x0][0x168] ;
        |        /*0070*/                   MOV R3, c[0x0][0x16c] ;
        |        /*0080*/                   STG.E.128.SYS [R2], R4 ;
        |        /*0090*/                   EXIT ;
        |.L_x_8:
        |        /*00a0*/                   BRA `(.L_x_8);
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_20:
        |""".stripMargin,
    "fetch1dSm70" ->
      """//--------------------- .text.fetch1d             --------------------------
        |	.section	.text.fetch1d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         fetch1d
        |        .type           fetch1d,@function
        |        .size           fetch1d,(.L_x_21 - fetch1d)
        |        .other          fetch1d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch1d:
        |.text.fetch1d:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;
        |        /*0020*/                   S2R R0, SR_TID.X ;
        |        /*0030*/                   IADD3 R0, R0, 0x2, RZ ;
        |        /*0040*/                   TLD.SCR.LZ R6, R4, R0, RZ, 0x0, 0x58, 1D;
        |        /*0050*/                   MOV R2, c[0x0][0x168] ;
        |        /*0060*/                   IMAD.MOV.U32 R3, RZ, RZ, c[0x0][0x16c] ;
        |        /*0070*/                   STG.E.128.SYS [R2], R4 ;
        |        /*0080*/                   EXIT ;
        |.L_x_9:
        |        /*0090*/                   BRA `(.L_x_9);
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_21:
        |""".stripMargin,
    "fetch3dSm70" ->
      """//--------------------- .text.fetch3d             --------------------------
        |	.section	.text.fetch3d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         fetch3d
        |        .type           fetch3d,@function
        |        .size           fetch3d,(.L_x_22 - fetch3d)
        |        .other          fetch3d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch3d:
        |.text.fetch3d:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;
        |        /*0020*/                   S2R R4, SR_TID.X ;
        |        /*0030*/                   IMAD.MOV.U32 R7, RZ, RZ, 0x231 ;
        |        /*0040*/                   MOV R5, RZ ;
        |        /*0050*/                   IMAD.MOV.U32 R6, RZ, RZ, RZ ;
        |        /*0060*/                   TLD.SCR.LZ.AOFFI R6, R4, R4, R6, 0x0, 0x58, 3D ;
        |        /*0070*/                   MOV R2, c[0x0][0x168] ;
        |        /*0080*/                   MOV R3, c[0x0][0x16c] ;
        |        /*0090*/                   STG.E.128.SYS [R2], R4 ;
        |        /*00a0*/                   EXIT ;
        |.L_x_10:
        |        /*00b0*/                   BRA `(.L_x_10);
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_22:
        |""".stripMargin,
    "fetch2dSm70" ->
      """//--------------------- .text.fetch2d             --------------------------
        |	.section	.text.fetch2d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         fetch2d
        |        .type           fetch2d,@function
        |        .size           fetch2d,(.L_x_23 - fetch2d)
        |        .other          fetch2d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch2d:
        |.text.fetch2d:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;
        |        /*0020*/                   S2R R4, SR_TID.X ;
        |        /*0030*/                   MOV R6, 0x31 ;
        |        /*0040*/                   IMAD.MOV.U32 R5, RZ, RZ, RZ ;
        |        /*0050*/                   TLD.SCR.LZ.AOFFI R6, R4, R4, R6, 0x0, 0x58, 2D ;
        |        /*0060*/                   MOV R2, c[0x0][0x168] ;
        |        /*0070*/                   IMAD.MOV.U32 R3, RZ, RZ, c[0x0][0x16c] ;
        |        /*0080*/                   STG.E.128.SYS [R2], R4 ;
        |        /*0090*/                   EXIT ;
        |.L_x_11:
        |        /*00a0*/                   BRA `(.L_x_11);
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_23:
        |""".stripMargin,
    "s_redSm75" ->
      """//--------------------- .text.s_red               --------------------------
        |	.section	.text.s_red,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	128
        |        .global         s_red
        |        .type           s_red,@function
        |        .size           s_red,(.L_x_12 - s_red)
        |        .other          s_red,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_red:
        |.text.s_red:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0020*/                   MOV R3, RZ ;
        |        /*0030*/                   IADD3 R0, R2, 0x5, RZ ;
        |        /*0040*/                   SURED.D.BA.2D.ADD.STRONG.SYS.TRAP [R2], R0, 0x0, 0x58 ;
        |        /*0050*/                   SURED.D.BA.1D.MAX.STRONG.SYS.IGN [R2], R0, 0x0, 0x58 ;
        |        /*0060*/                   EXIT ;
        |.L_x_0:
        |        /*0070*/                   BRA `(.L_x_0);
        |.L_x_12:
        |""".stripMargin,
    "s_st32Sm75" ->
      """//--------------------- .text.s_st32              --------------------------
        |	.section	.text.s_st32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         s_st32
        |        .type           s_st32,@function
        |        .size           s_st32,(.L_x_13 - s_st32)
        |        .other          s_st32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_st32:
        |.text.s_st32:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0020*/                   MOV R0, 0xdeadbeef ;
        |        /*0030*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x1 ;
        |        /*0040*/                   MOV R5, 0x2 ;
        |        /*0050*/                   ULDC.64 UR4, c[0x0][0x168] ;
        |        /*0060*/                   STG.E.SYS [UR4], R0 ;
        |        /*0070*/                   IADD3 R2, R2, 0x8, RZ ;
        |        /*0080*/                   IMAD.MOV.U32 R4, RZ, RZ, R2 ;
        |        /*0090*/                   SUST.D.BA.2D.STRONG.CTA [R2], R0, 0x0, 0x58 ;
        |        /*00a0*/                   SUST.D.BA.2D.STRONG.CTA.IGN [R4], R0, 0x0, 0x58 ;
        |        /*00b0*/                   EXIT ;
        |.L_x_1:
        |        /*00c0*/                   BRA `(.L_x_1);
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_13:
        |""".stripMargin,
    "s_ld128Sm75" ->
      """//--------------------- .text.s_ld128             --------------------------
        |	.section	.text.s_ld128,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         s_ld128
        |        .type           s_ld128,@function
        |        .size           s_ld128,(.L_x_14 - s_ld128)
        |        .other          s_ld128,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld128:
        |.text.s_ld128:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   IADD3 R0, R0, 0x10, RZ ;
        |        /*0030*/                   SULD.D.BA.1D.128.STRONG.CTA.IGN R4, [R0], 0x0, 0x58 ;
        |        /*0040*/                   ULDC.64 UR4, c[0x0][0x168] ;
        |        /*0050*/                   STG.E.128.SYS [UR4], R4 ;
        |        /*0060*/                   EXIT ;
        |.L_x_2:
        |        /*0070*/                   BRA `(.L_x_2);
        |.L_x_14:
        |""".stripMargin,
    "s_ld64Sm75" ->
      """//--------------------- .text.s_ld64              --------------------------
        |	.section	.text.s_ld64,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	128
        |        .global         s_ld64
        |        .type           s_ld64,@function
        |        .size           s_ld64,(.L_x_15 - s_ld64)
        |        .other          s_ld64,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld64:
        |.text.s_ld64:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0020*/                   IADD3 R2, R2, 0x8, RZ ;
        |        /*0030*/                   SULD.D.BA.1D.64.STRONG.CTA.TRAP R2, [R2], 0x0, 0x58 ;
        |        /*0040*/                   ULDC.64 UR4, c[0x0][0x168] ;
        |        /*0050*/                   STG.E.64.SYS [UR4], R2 ;
        |        /*0060*/                   EXIT ;
        |.L_x_3:
        |        /*0070*/                   BRA `(.L_x_3);
        |.L_x_15:
        |""".stripMargin,
    "s_ld8Sm75" ->
      """//--------------------- .text.s_ld8               --------------------------
        |	.section	.text.s_ld8,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	128
        |        .global         s_ld8
        |        .type           s_ld8,@function
        |        .size           s_ld8,(.L_x_16 - s_ld8)
        |        .other          s_ld8,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld8:
        |.text.s_ld8:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0020*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x1 ;
        |        /*0030*/                   IADD3 R2, R2, 0x6, RZ ;
        |        /*0040*/                   SULD.D.BA.2D.U8.STRONG.CTA R2, [R2], 0x0, 0x58 ;
        |        /*0050*/                   ULDC.64 UR4, c[0x0][0x168] ;
        |        /*0060*/                   LOP3.LUT R0, R2, 0xff, RZ, 0xc0, !PT ;
        |        /*0070*/                   STG.E.SYS [UR4], R0 ;
        |        /*0080*/                   EXIT ;
        |.L_x_4:
        |        /*0090*/                   BRA `(.L_x_4);
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_16:
        |""".stripMargin,
    "query_dimsSm75" ->
      """//--------------------- .text.query_dims          --------------------------
        |	.section	.text.query_dims,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	128
        |        .global         query_dims
        |        .type           query_dims,@function
        |        .size           query_dims,(.L_x_17 - query_dims)
        |        .other          query_dims,@"STO_CUDA_ENTRY STV_DEFAULT"
        |query_dims:
        |.text.query_dims:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   CS2R R2, SRZ ;
        |        /*0020*/                   MOV R0, RZ ;
        |        /*0030*/                   TXQ RZ, R2, R2, TEX_HEADER_DIMENSION, 0x0, 0x58, 0x1 ;
        |        /*0040*/                   TXQ RZ, R3, R3, TEX_HEADER_DIMENSION, 0x0, 0x58, 0x2 ;
        |        /*0050*/                   TXQ RZ, R0, R0, TEX_HEADER_DIMENSION, 0x0, 0x58, 0x4 ;
        |        /*0060*/                   ULDC.64 UR4, c[0x0][0x168] ;
        |        /*0070*/                   STG.E.64.SYS [UR4], R2 ;
        |        /*0080*/                   STG.E.SYS [UR4+0x8], R0 ;
        |        /*0090*/                   EXIT ;
        |.L_x_5:
        |        /*00a0*/                   BRA `(.L_x_5);
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_17:
        |""".stripMargin,
    "gather_bSm75" ->
      """//--------------------- .text.gather_b            --------------------------
        |	.section	.text.gather_b,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         gather_b
        |        .type           gather_b,@function
        |        .size           gather_b,(.L_x_18 - gather_b)
        |        .other          gather_b,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gather_b:
        |.text.gather_b:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   MOV R4, 0x3fc00000 ;
        |        /*0020*/                   MOV R5, 0x40200000 ;
        |        /*0030*/                   TLD4.SCR.B R6, R4, R4, R5, 0x0, 0x58, 2D ;
        |        /*0040*/                   ULDC.64 UR4, c[0x0][0x168] ;
        |        /*0050*/                   STG.E.128.SYS [UR4], R4 ;
        |        /*0060*/                   EXIT ;
        |.L_x_6:
        |        /*0070*/                   BRA `(.L_x_6);
        |.L_x_18:
        |""".stripMargin,
    "samp2d_gSm75" ->
      """//--------------------- .text.samp2d_g            --------------------------
        |	.section	.text.samp2d_g,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	128
        |        .global         samp2d_g
        |        .type           samp2d_g,@function
        |        .size           samp2d_g,(.L_x_19 - samp2d_g)
        |        .other          samp2d_g,@"STO_CUDA_ENTRY STV_DEFAULT"
        |samp2d_g:
        |.text.samp2d_g:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   IMAD.MOV.U32 R0, RZ, RZ, -0x3e000000 ;
        |        /*0020*/                   MOV R2, 0x3fc00000 ;
        |        /*0030*/                   MOV R3, 0x40200000 ;
        |        /*0040*/                   TEX.SCR.LL RZ, R0, R2, R0, 0x0, 0x58, 2D, 0x2 ;
        |        /*0050*/                   ULDC.64 UR4, c[0x0][0x168] ;
        |        /*0060*/                   STG.E.SYS [UR4], R0 ;
        |        /*0070*/                   EXIT ;
        |.L_x_7:
        |        /*0080*/                   BRA `(.L_x_7);
        |        /*0090*/                   NOP;
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_19:
        |""".stripMargin,
    "samp1dSm75" ->
      """//--------------------- .text.samp1d              --------------------------
        |	.section	.text.samp1d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         samp1d
        |        .type           samp1d,@function
        |        .size           samp1d,(.L_x_20 - samp1d)
        |        .other          samp1d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |samp1d:
        |.text.samp1d:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   IMAD.MOV.U32 R6, RZ, RZ, -0x3e000000 ;
        |        /*0020*/                   MOV R4, 0x40200000 ;
        |        /*0030*/                   MOV R5, RZ ;
        |        /*0040*/                   TEX.SCR.LL R6, R4, R4, R6, 0x0, 0x58, 2D ;
        |        /*0050*/                   ULDC.64 UR4, c[0x0][0x168] ;
        |        /*0060*/                   STG.E.128.SYS [UR4], R4 ;
        |        /*0070*/                   EXIT ;
        |.L_x_8:
        |        /*0080*/                   BRA `(.L_x_8);
        |        /*0090*/                   NOP;
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_20:
        |""".stripMargin,
    "fetch1dSm75" ->
      """//--------------------- .text.fetch1d             --------------------------
        |	.section	.text.fetch1d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         fetch1d
        |        .type           fetch1d,@function
        |        .size           fetch1d,(.L_x_21 - fetch1d)
        |        .other          fetch1d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch1d:
        |.text.fetch1d:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   IADD3 R0, R0, 0x2, RZ ;
        |        /*0030*/                   TLD.SCR.LZ R6, R4, R0, RZ, 0x0, 0x58, 1D;
        |        /*0040*/                   ULDC.64 UR4, c[0x0][0x168] ;
        |        /*0050*/                   STG.E.128.SYS [UR4], R4 ;
        |        /*0060*/                   EXIT ;
        |.L_x_9:
        |        /*0070*/                   BRA `(.L_x_9);
        |.L_x_21:
        |""".stripMargin,
    "fetch3dSm75" ->
      """//--------------------- .text.fetch3d             --------------------------
        |	.section	.text.fetch3d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         fetch3d
        |        .type           fetch3d,@function
        |        .size           fetch3d,(.L_x_22 - fetch3d)
        |        .other          fetch3d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch3d:
        |.text.fetch3d:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   IMAD.MOV.U32 R7, RZ, RZ, 0x231 ;
        |        /*0030*/                   MOV R5, RZ ;
        |        /*0040*/                   MOV R6, RZ ;
        |        /*0050*/                   TLD.SCR.LZ.AOFFI R6, R4, R4, R6, 0x0, 0x58, 3D ;
        |        /*0060*/                   ULDC.64 UR4, c[0x0][0x168] ;
        |        /*0070*/                   STG.E.128.SYS [UR4], R4 ;
        |        /*0080*/                   EXIT ;
        |.L_x_10:
        |        /*0090*/                   BRA `(.L_x_10);
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_22:
        |""".stripMargin,
    "fetch2dSm75" ->
      """//--------------------- .text.fetch2d             --------------------------
        |	.section	.text.fetch2d,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         fetch2d
        |        .type           fetch2d,@function
        |        .size           fetch2d,(.L_x_23 - fetch2d)
        |        .other          fetch2d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch2d:
        |.text.fetch2d:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   MOV R6, 0x31 ;
        |        /*0030*/                   MOV R5, RZ ;
        |        /*0040*/                   TLD.SCR.LZ.AOFFI R6, R4, R4, R6, 0x0, 0x58, 2D ;
        |        /*0050*/                   ULDC.64 UR4, c[0x0][0x168] ;
        |        /*0060*/                   STG.E.128.SYS [UR4], R4 ;
        |        /*0070*/                   EXIT ;
        |.L_x_11:
        |        /*0080*/                   BRA `(.L_x_11);
        |        /*0090*/                   NOP;
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_23:
        |""".stripMargin,
    "s_redSm90" ->
      """//--------------------- .text.s_red               --------------------------
        |	.section	.text.s_red,"ax",@progbits
        |	.align	128
        |        .global         s_red
        |        .type           s_red,@function
        |        .size           s_red,(.L_x_12 - s_red)
        |        .other          s_red,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_red:
        |.text.s_red:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   HFMA2.MMA R5, -RZ, RZ, 0, 0 ;
        |        /*0030*/                   ULDC UR4, c[0x0][0x210] ;
        |        /*0040*/                   IADD3 R3, R4, 0x5, RZ ;
        |        /*0050*/                   SURED.D.BA.2D.ADD.STRONG.SYS.TRAP [R4], R3, UR4, 0x0 ;
        |        /*0060*/                   SURED.D.BA.1D.MAX.STRONG.SYS.IGN [R4], R3, UR4, 0x0 ;
        |        /*0070*/                   EXIT ;
        |.L_x_0:
        |        /*0080*/                   BRA `(.L_x_0);
        |        /*0090*/                   NOP;
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_12:
        |""".stripMargin,
    "s_st32Sm90" ->
      """//--------------------- .text.s_st32              --------------------------
        |	.section	.text.s_st32,"ax",@progbits
        |	.align	128
        |        .global         s_st32
        |        .type           s_st32,@function
        |        .size           s_st32,(.L_x_13 - s_st32)
        |        .other          s_st32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_st32:
        |.text.s_st32:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*0030*/                   HFMA2.MMA R7, -RZ, RZ, -427.25, -1.7333984375 ;
        |        /*0040*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x1 ;
        |        /*0050*/                   MOV R9, 0x2 ;
        |        /*0060*/                   ULDC UR4, c[0x0][0x210] ;
        |        /*0070*/                   IADD3 R4, R4, 0x8, RZ ;
        |        /*0080*/                   SUST.D.BA.2D.STRONG.SM [R4], R7, UR4, 0x0 ;
        |        /*0090*/                   IMAD.MOV.U32 R8, RZ, RZ, R4 ;
        |        /*00a0*/                   SUST.D.BA.2D.STRONG.SM.IGN [R8], R7, UR4, 0x0 ;
        |        /*00b0*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*00c0*/                   STG.E desc[UR4][R2.64], R7 ;
        |        /*00d0*/                   EXIT ;
        |.L_x_1:
        |        /*00e0*/                   BRA `(.L_x_1);
        |        /*00f0*/                   NOP;
        |        /*0100*/                   NOP;
        |        /*0110*/                   NOP;
        |        /*0120*/                   NOP;
        |        /*0130*/                   NOP;
        |        /*0140*/                   NOP;
        |        /*0150*/                   NOP;
        |        /*0160*/                   NOP;
        |        /*0170*/                   NOP;
        |.L_x_13:
        |""".stripMargin,
    "s_ld128Sm90" ->
      """//--------------------- .text.s_ld128             --------------------------
        |	.section	.text.s_ld128,"ax",@progbits
        |	.align	128
        |        .global         s_ld128
        |        .type           s_ld128,@function
        |        .size           s_ld128,(.L_x_14 - s_ld128)
        |        .other          s_ld128,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld128:
        |.text.s_ld128:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   ULDC UR4, c[0x0][0x210] ;
        |        /*0030*/                   IADD3 R0, R0, 0x10, RZ ;
        |        /*0040*/                   SULD.D.BA.1D.128.STRONG.SM.IGN R4, [R0], UR4, 0x0 ;
        |        /*0050*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*0060*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0070*/                   STG.E.128 desc[UR4][R2.64], R4 ;
        |        /*0080*/                   EXIT ;
        |.L_x_2:
        |        /*0090*/                   BRA `(.L_x_2);
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_14:
        |""".stripMargin,
    "s_ld64Sm90" ->
      """//--------------------- .text.s_ld64              --------------------------
        |	.section	.text.s_ld64,"ax",@progbits
        |	.align	128
        |        .global         s_ld64
        |        .type           s_ld64,@function
        |        .size           s_ld64,(.L_x_15 - s_ld64)
        |        .other          s_ld64,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld64:
        |.text.s_ld64:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0020*/                   ULDC UR4, c[0x0][0x210] ;
        |        /*0030*/                   IADD3 R2, R2, 0x8, RZ ;
        |        /*0040*/                   SULD.D.BA.1D.64.STRONG.SM.TRAP R2, [R2], UR4, 0x0 ;
        |        /*0050*/                   LDC.64 R4, c[0x0][0x218] ;
        |        /*0060*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0070*/                   STG.E.64 desc[UR4][R4.64], R2 ;
        |        /*0080*/                   EXIT ;
        |.L_x_3:
        |        /*0090*/                   BRA `(.L_x_3);
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_15:
        |""".stripMargin,
    "s_ld8Sm90" ->
      """//--------------------- .text.s_ld8               --------------------------
        |	.section	.text.s_ld8,"ax",@progbits
        |	.align	128
        |        .global         s_ld8
        |        .type           s_ld8,@function
        |        .size           s_ld8,(.L_x_16 - s_ld8)
        |        .other          s_ld8,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld8:
        |.text.s_ld8:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x1 ;
        |        /*0030*/                   ULDC UR4, c[0x0][0x210] ;
        |        /*0040*/                   IADD3 R4, R4, 0x6, RZ ;
        |        /*0050*/                   SULD.D.BA.2D.U8.STRONG.SM R4, [R4], UR4, 0x0 ;
        |        /*0060*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*0070*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0080*/                   LOP3.LUT R7, R4, 0xff, RZ, 0xc0, !PT ;
        |        /*0090*/                   STG.E desc[UR4][R2.64], R7 ;
        |        /*00a0*/                   EXIT ;
        |.L_x_4:
        |        /*00b0*/                   BRA `(.L_x_4);
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_16:
        |""".stripMargin,
    "query_dimsSm90" ->
      """//--------------------- .text.query_dims          --------------------------
        |	.section	.text.query_dims,"ax",@progbits
        |	.align	128
        |        .global         query_dims
        |        .type           query_dims,@function
        |        .size           query_dims,(.L_x_17 - query_dims)
        |        .other          query_dims,@"STO_CUDA_ENTRY STV_DEFAULT"
        |query_dims:
        |.text.query_dims:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   HFMA2.MMA R7, -RZ, RZ, 0, 0 ;
        |        /*0020*/                   CS2R R4, SRZ ;
        |        /*0030*/                   ULDC UR4, c[0x0][0x210] ;
        |        /*0040*/                   UMOV UR5, URZ ;
        |        /*0050*/                   TXQ RZ, R4, R4, TEX_HEADER_DIMENSION, UR4, 0x0, 0x1 ;
        |        /*0060*/                   TXQ RZ, R5, R5, TEX_HEADER_DIMENSION, UR4, 0x0, 0x2 ;
        |        /*0070*/                   TXQ RZ, R7, R7, TEX_HEADER_DIMENSION, UR4, 0x0, 0x4 ;
        |        /*0080*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*0090*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*00a0*/                   STG.E.64 desc[UR4][R2.64], R4 ;
        |        /*00b0*/                   STG.E desc[UR4][R2.64+0x8], R7 ;
        |        /*00c0*/                   EXIT ;
        |.L_x_5:
        |        /*00d0*/                   BRA `(.L_x_5);
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
        |.L_x_17:
        |""".stripMargin,
    "gather_bSm90" ->
      """//--------------------- .text.gather_b            --------------------------
        |	.section	.text.gather_b,"ax",@progbits
        |	.align	128
        |        .global         gather_b
        |        .type           gather_b,@function
        |        .size           gather_b,(.L_x_18 - gather_b)
        |        .other          gather_b,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gather_b:
        |.text.gather_b:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   HFMA2.MMA R4, -RZ, RZ, 1.9375, 0 ;
        |        /*0020*/                   MOV R5, 0x40200000 ;
        |        /*0030*/                   ULDC UR4, c[0x0][0x210] ;
        |        /*0040*/                   UMOV UR5, URZ ;
        |        /*0050*/                   TLD4.B R6, R4, R4, RZ, UR4, 0x0, 2D;
        |        /*0060*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*0070*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0080*/                   STG.E.128 desc[UR4][R2.64], R4 ;
        |        /*0090*/                   EXIT ;
        |.L_x_6:
        |        /*00a0*/                   BRA `(.L_x_6);
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_18:
        |""".stripMargin,
    "samp2d_gSm90" ->
      """//--------------------- .text.samp2d_g            --------------------------
        |	.section	.text.samp2d_g,"ax",@progbits
        |	.align	128
        |        .global         samp2d_g
        |        .type           samp2d_g,@function
        |        .size           samp2d_g,(.L_x_19 - samp2d_g)
        |        .other          samp2d_g,@"STO_CUDA_ENTRY STV_DEFAULT"
        |samp2d_g:
        |.text.samp2d_g:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   HFMA2.MMA R5, -RZ, RZ, 2.0625, 0 ;
        |        /*0020*/                   MOV R4, 0x3fc00000 ;
        |        /*0030*/                   HFMA2.MMA R0, -RZ, RZ, -3, 0 ;
        |        /*0040*/                   ULDC UR4, c[0x0][0x210] ;
        |        /*0050*/                   UMOV UR5, URZ ;
        |        /*0060*/                   TEX.LL RZ, R5, R4, R0, UR4, 0x0, 2D, 0x2 ;
        |        /*0070*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*0080*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0090*/                   STG.E desc[UR4][R2.64], R5 ;
        |        /*00a0*/                   EXIT ;
        |.L_x_7:
        |        /*00b0*/                   BRA `(.L_x_7);
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_19:
        |""".stripMargin,
    "samp1dSm90" ->
      """//--------------------- .text.samp1d              --------------------------
        |	.section	.text.samp1d,"ax",@progbits
        |	.align	128
        |        .global         samp1d
        |        .type           samp1d,@function
        |        .size           samp1d,(.L_x_20 - samp1d)
        |        .other          samp1d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |samp1d:
        |.text.samp1d:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   HFMA2.MMA R6, -RZ, RZ, -3, 0 ;
        |        /*0020*/                   MOV R4, 0x40200000 ;
        |        /*0030*/                   HFMA2.MMA R5, -RZ, RZ, 0, 0 ;
        |        /*0040*/                   ULDC UR4, c[0x0][0x210] ;
        |        /*0050*/                   UMOV UR5, URZ ;
        |        /*0060*/                   TEX.LL R6, R4, R4, R6, UR4, 0x0, 2D ;
        |        /*0070*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*0080*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0090*/                   STG.E.128 desc[UR4][R2.64], R4 ;
        |        /*00a0*/                   EXIT ;
        |.L_x_8:
        |        /*00b0*/                   BRA `(.L_x_8);
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_20:
        |""".stripMargin,
    "samp3dSm90" ->
      """//--------------------- .text.samp3d              --------------------------
        |	.section	.text.samp3d,"ax",@progbits
        |	.align	128
        |        .global         samp3d
        |        .type           samp3d,@function
        |        .size           samp3d,(.L_x_1 - samp3d)
        |        .other          samp3d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |samp3d:
        |.text.samp3d:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   HFMA2.MMA R5, -RZ, RZ, 1.75, 0 ;
        |        /*0020*/                   IMAD.MOV.U32 R7, RZ, RZ, -0x3e000000 ;
        |        /*0030*/                   MOV R6, 0x3fc00000 ;
        |        /*0040*/                   IMAD.MOV.U32 R4, RZ, RZ, 0x3f000000 ;
        |        /*0050*/                   ULDC UR4, c[0x0][0x210] ;
        |        /*0060*/                   UMOV UR5, URZ ;
        |        /*0070*/                   TEX.LL R6, R4, R4, R7, UR4, 0x0, 3D ;
        |        /*0080*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*0090*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*00a0*/                   STG.E.128 desc[UR4][R2.64], R4 ;
        |        /*00b0*/                   EXIT ;
        |.L_x_0:
        |        /*00c0*/                   BRA `(.L_x_0);
        |        /*00d0*/                   NOP;
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
        |""".stripMargin,
    "samp3dSm100" ->
      """//--------------------- .text.samp3d              --------------------------
        |	.section	.text.samp3d,"ax",@progbits
        |	.align	128
        |        .global         samp3d
        |        .type           samp3d,@function
        |        .size           samp3d,(.L_x_1 - samp3d)
        |        .other          samp3d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |samp3d:
        |.text.samp3d:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0020*/                   HFMA2 R5, -RZ, RZ, 1.75, 0 ;
        |        /*0030*/                   IMAD.MOV.U32 R7, RZ, RZ, -0x3e000000 ;
        |        /*0040*/                   MOV R6, 0x3fc00000 ;
        |        /*0050*/                   IMAD.MOV.U32 R4, RZ, RZ, 0x3f000000 ;
        |        /*0060*/                   UMOV UR5, URZ ;
        |        /*0070*/                   TEX.LL R6, R4, R4, R7, UR4, 3D ;
        |        /*0080*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0090*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*00a0*/                   STG.E.128 desc[UR4][R2.64], R4 ;
        |        /*00b0*/                   EXIT ;
        |.L_x_0:
        |        /*00c0*/                   BRA `(.L_x_0);
        |        /*00d0*/                   NOP;
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
        |""".stripMargin,
    "fetch1dSm90" ->
      """//--------------------- .text.fetch1d             --------------------------
        |	.section	.text.fetch1d,"ax",@progbits
        |	.align	128
        |        .global         fetch1d
        |        .type           fetch1d,@function
        |        .size           fetch1d,(.L_x_21 - fetch1d)
        |        .other          fetch1d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch1d:
        |.text.fetch1d:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   ULDC UR4, c[0x0][0x210] ;
        |        /*0030*/                   UMOV UR5, URZ ;
        |        /*0040*/                   IADD3 R0, R0, 0x2, RZ ;
        |        /*0050*/                   TLD.LZ R6, R4, R0, RZ, UR4, 0x0, 1D;
        |        /*0060*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*0070*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0080*/                   STG.E.128 desc[UR4][R2.64], R4 ;
        |        /*0090*/                   EXIT ;
        |.L_x_9:
        |        /*00a0*/                   BRA `(.L_x_9);
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_21:
        |""".stripMargin,
    "fetch3dSm90" ->
      """//--------------------- .text.fetch3d             --------------------------
        |	.section	.text.fetch3d,"ax",@progbits
        |	.align	128
        |        .global         fetch3d
        |        .type           fetch3d,@function
        |        .size           fetch3d,(.L_x_22 - fetch3d)
        |        .other          fetch3d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch3d:
        |.text.fetch3d:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   HFMA2.MMA R7, -RZ, RZ, 0, 3.3438205718994140625e-05 ;
        |        /*0030*/                   MOV R5, RZ ;
        |        /*0040*/                   HFMA2.MMA R6, -RZ, RZ, 0, 0 ;
        |        /*0050*/                   ULDC UR4, c[0x0][0x210] ;
        |        /*0060*/                   UMOV UR5, URZ ;
        |        /*0070*/                   TLD.LZ.AOFFI R6, R4, R4, R7, UR4, 0x0, 3D ;
        |        /*0080*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*0090*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*00a0*/                   STG.E.128 desc[UR4][R2.64], R4 ;
        |        /*00b0*/                   EXIT ;
        |.L_x_10:
        |        /*00c0*/                   BRA `(.L_x_10);
        |        /*00d0*/                   NOP;
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
        |.L_x_22:
        |""".stripMargin,
    "fetch2dSm90" ->
      """//--------------------- .text.fetch2d             --------------------------
        |	.section	.text.fetch2d,"ax",@progbits
        |	.align	128
        |        .global         fetch2d
        |        .type           fetch2d,@function
        |        .size           fetch2d,(.L_x_23 - fetch2d)
        |        .other          fetch2d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch2d:
        |.text.fetch2d:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   HFMA2.MMA R6, -RZ, RZ, 0, 2.920627593994140625e-06 ;
        |        /*0030*/                   MOV R5, RZ ;
        |        /*0040*/                   ULDC UR4, c[0x0][0x210] ;
        |        /*0050*/                   UMOV UR5, URZ ;
        |        /*0060*/                   TLD.LZ.AOFFI R6, R4, R4, R6, UR4, 0x0, 2D ;
        |        /*0070*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*0080*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0090*/                   STG.E.128 desc[UR4][R2.64], R4 ;
        |        /*00a0*/                   EXIT ;
        |.L_x_11:
        |        /*00b0*/                   BRA `(.L_x_11);
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_23:
        |//--------------------- SYMBOLS --------------------------
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "s_redSm120" ->
      """//--------------------- .text.s_red               --------------------------
        |	.section	.text.s_red,"ax",@progbits
        |	.align	128
        |        .global         s_red
        |        .type           s_red,@function
        |        .size           s_red,(.L_x_12 - s_red)
        |        .other          s_red,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_red:
        |.text.s_red:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0030*/                   HFMA2 R5, -RZ, RZ, 0, 0 ;
        |        /*0040*/                   IADD R3, R4, 0x5 ;
        |        /*0050*/                   SURED.D.BA.2D.ADD.STRONG.SYS.TRAP [R4], R3, UR4 ;
        |        /*0060*/                   SURED.D.BA.1D.MAX.STRONG.SYS.IGN [R4], R3, UR4 ;
        |        /*0070*/                   EXIT ;
        |.L_x_0:
        |        /*0080*/                   BRA `(.L_x_0);
        |        /*0090*/                   NOP;
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_12:
        |""".stripMargin,
    "s_st32Sm120" ->
      """//--------------------- .text.s_st32              --------------------------
        |	.section	.text.s_st32,"ax",@progbits
        |	.align	128
        |        .global         s_st32
        |        .type           s_st32,@function
        |        .size           s_st32,(.L_x_13 - s_st32)
        |        .other          s_st32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_st32:
        |.text.s_st32:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0030*/                   HFMA2 R7, -RZ, RZ, -427.25, -1.7333984375 ;
        |        /*0040*/                   MOV R5, 0x1 ;
        |        /*0050*/                   HFMA2 R9, -RZ, RZ, 0, 1.1920928955078125e-07 ;
        |        /*0060*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0070*/                   IADD R4, R4, 0x8 ;
        |        /*0080*/                   SUST.D.BA.2D.STRONG.SM [R4], R7, UR4 ;
        |        /*0090*/                   MOV R8, R4 ;
        |        /*00a0*/                   SUST.D.BA.2D.STRONG.SM.IGN [R8], R7, UR4 ;
        |        /*00b0*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*00c0*/                   STG.E desc[UR4][R2.64], R7 ;
        |        /*00d0*/                   EXIT ;
        |.L_x_1:
        |        /*00e0*/                   BRA `(.L_x_1);
        |        /*00f0*/                   NOP;
        |        /*0100*/                   NOP;
        |        /*0110*/                   NOP;
        |        /*0120*/                   NOP;
        |        /*0130*/                   NOP;
        |        /*0140*/                   NOP;
        |        /*0150*/                   NOP;
        |        /*0160*/                   NOP;
        |        /*0170*/                   NOP;
        |.L_x_13:
        |""".stripMargin,
    "s_ld128Sm120" ->
      """//--------------------- .text.s_ld128             --------------------------
        |	.section	.text.s_ld128,"ax",@progbits
        |	.align	128
        |        .global         s_ld128
        |        .type           s_ld128,@function
        |        .size           s_ld128,(.L_x_14 - s_ld128)
        |        .other          s_ld128,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld128:
        |.text.s_ld128:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0030*/                   IADD R0, R0, 0x10 ;
        |        /*0040*/                   SULD.D.BA.1D.128.STRONG.SM.IGN R4, [R0], UR4 ;
        |        /*0050*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0060*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0070*/                   STG.E.128 desc[UR4][R2.64], R4 ;
        |        /*0080*/                   EXIT ;
        |.L_x_2:
        |        /*0090*/                   BRA `(.L_x_2);
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_14:
        |""".stripMargin,
    "s_ld64Sm120" ->
      """//--------------------- .text.s_ld64              --------------------------
        |	.section	.text.s_ld64,"ax",@progbits
        |	.align	128
        |        .global         s_ld64
        |        .type           s_ld64,@function
        |        .size           s_ld64,(.L_x_15 - s_ld64)
        |        .other          s_ld64,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld64:
        |.text.s_ld64:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0020*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0030*/                   IADD R2, R2, 0x8 ;
        |        /*0040*/                   SULD.D.BA.1D.64.STRONG.SM.TRAP R2, [R2], UR4 ;
        |        /*0050*/                   LDC.64 R4, c[0x0][0x388] ;
        |        /*0060*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0070*/                   STG.E.64 desc[UR4][R4.64], R2 ;
        |        /*0080*/                   EXIT ;
        |.L_x_3:
        |        /*0090*/                   BRA `(.L_x_3);
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_15:
        |""".stripMargin,
    "s_ld8Sm120" ->
      """//--------------------- .text.s_ld8               --------------------------
        |	.section	.text.s_ld8,"ax",@progbits
        |	.align	128
        |        .global         s_ld8
        |        .type           s_ld8,@function
        |        .size           s_ld8,(.L_x_16 - s_ld8)
        |        .other          s_ld8,@"STO_CUDA_ENTRY STV_DEFAULT"
        |s_ld8:
        |.text.s_ld8:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0030*/                   HFMA2 R5, -RZ, RZ, 0, 5.9604644775390625e-08 ;
        |        /*0040*/                   IADD R4, R4, 0x6 ;
        |        /*0050*/                   SULD.D.BA.2D.U8.STRONG.SM R4, [R4], UR4 ;
        |        /*0060*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0070*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0080*/                   LOP3.LUT R7, R4, 0xff, RZ, 0xc0, !PT ;
        |        /*0090*/                   STG.E desc[UR4][R2.64], R7 ;
        |        /*00a0*/                   EXIT ;
        |.L_x_4:
        |        /*00b0*/                   BRA `(.L_x_4);
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_16:
        |""".stripMargin,
    "query_dimsSm120" ->
      """//--------------------- .text.query_dims          --------------------------
        |	.section	.text.query_dims,"ax",@progbits
        |	.align	128
        |        .global         query_dims
        |        .type           query_dims,@function
        |        .size           query_dims,(.L_x_17 - query_dims)
        |        .other          query_dims,@"STO_CUDA_ENTRY STV_DEFAULT"
        |query_dims:
        |.text.query_dims:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0020*/                   CS2R R4, SRZ ;
        |        /*0030*/                   HFMA2 R7, -RZ, RZ, 0, 0 ;
        |        /*0040*/                   UMOV UR5, URZ ;
        |        /*0050*/                   TXQ RZ, R4, R4, TEX_HEADER_DIMENSION, UR4, 0x0, 0x1 ;
        |        /*0060*/                   TXQ RZ, R5, R5, TEX_HEADER_DIMENSION, UR4, 0x0, 0x2 ;
        |        /*0070*/                   TXQ RZ, R7, R7, TEX_HEADER_DIMENSION, UR4, 0x0, 0x4 ;
        |        /*0080*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0090*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*00a0*/                   STG.E.64 desc[UR4][R2.64], R4 ;
        |        /*00b0*/                   STG.E desc[UR4][R2.64+0x8], R7 ;
        |        /*00c0*/                   EXIT ;
        |.L_x_5:
        |        /*00d0*/                   BRA `(.L_x_5);
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
        |.L_x_17:
        |""".stripMargin,
    "gather_bSm120" ->
      """//--------------------- .text.gather_b            --------------------------
        |	.section	.text.gather_b,"ax",@progbits
        |	.align	128
        |        .global         gather_b
        |        .type           gather_b,@function
        |        .size           gather_b,(.L_x_18 - gather_b)
        |        .other          gather_b,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gather_b:
        |.text.gather_b:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0020*/                   HFMA2 R4, -RZ, RZ, 1.9375, 0 ;
        |        /*0030*/                   MOV R5, 0x40200000 ;
        |        /*0040*/                   UMOV UR5, URZ ;
        |        /*0050*/                   TLD4.B R6, R4, R4, RZ, UR4, 2D;
        |        /*0060*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0070*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0080*/                   STG.E.128 desc[UR4][R2.64], R4 ;
        |        /*0090*/                   EXIT ;
        |.L_x_6:
        |        /*00a0*/                   BRA `(.L_x_6);
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_18:
        |""".stripMargin,
    "samp2d_gSm120" ->
      """//--------------------- .text.samp2d_g            --------------------------
        |	.section	.text.samp2d_g,"ax",@progbits
        |	.align	128
        |        .global         samp2d_g
        |        .type           samp2d_g,@function
        |        .size           samp2d_g,(.L_x_19 - samp2d_g)
        |        .other          samp2d_g,@"STO_CUDA_ENTRY STV_DEFAULT"
        |samp2d_g:
        |.text.samp2d_g:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0020*/                   MOV R5, 0x40200000 ;
        |        /*0030*/                   HFMA2 R4, -RZ, RZ, 1.9375, 0 ;
        |        /*0040*/                   UMOV UR5, URZ ;
        |        /*0050*/                   TEX RZ, R5, R4, RZ, UR4, 2D, 0x2;
        |        /*0060*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0070*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0080*/                   STG.E desc[UR4][R2.64], R5 ;
        |        /*0090*/                   EXIT ;
        |.L_x_7:
        |        /*00a0*/                   BRA `(.L_x_7);
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_19:
        |""".stripMargin,
    "samp1dSm120" ->
      """//--------------------- .text.samp1d              --------------------------
        |	.section	.text.samp1d,"ax",@progbits
        |	.align	128
        |        .global         samp1d
        |        .type           samp1d,@function
        |        .size           samp1d,(.L_x_20 - samp1d)
        |        .other          samp1d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |samp1d:
        |.text.samp1d:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0020*/                   HFMA2 R4, -RZ, RZ, 2.0625, 0 ;
        |        /*0030*/                   MOV R5, RZ ;
        |        /*0040*/                   UMOV UR5, URZ ;
        |        /*0050*/                   TEX R6, R4, R4, RZ, UR4, 2D;
        |        /*0060*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0070*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0080*/                   STG.E.128 desc[UR4][R2.64], R4 ;
        |        /*0090*/                   EXIT ;
        |.L_x_8:
        |        /*00a0*/                   BRA `(.L_x_8);
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_20:
        |""".stripMargin,
    "fetch1dSm120" ->
      """//--------------------- .text.fetch1d             --------------------------
        |	.section	.text.fetch1d,"ax",@progbits
        |	.align	128
        |        .global         fetch1d
        |        .type           fetch1d,@function
        |        .size           fetch1d,(.L_x_21 - fetch1d)
        |        .other          fetch1d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch1d:
        |.text.fetch1d:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0030*/                   UMOV UR5, URZ ;
        |        /*0040*/                   IADD R0, R0, 0x2 ;
        |        /*0050*/                   TLD.LZ R6, R4, R0, RZ, UR4, 1D;
        |        /*0060*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0070*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0080*/                   STG.E.128 desc[UR4][R2.64], R4 ;
        |        /*0090*/                   EXIT ;
        |.L_x_9:
        |        /*00a0*/                   BRA `(.L_x_9);
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_21:
        |""".stripMargin,
    "fetch3dSm120" ->
      """//--------------------- .text.fetch3d             --------------------------
        |	.section	.text.fetch3d,"ax",@progbits
        |	.align	128
        |        .global         fetch3d
        |        .type           fetch3d,@function
        |        .size           fetch3d,(.L_x_22 - fetch3d)
        |        .other          fetch3d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch3d:
        |.text.fetch3d:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0030*/                   HFMA2 R7, -RZ, RZ, 0, 3.3438205718994140625e-05 ;
        |        /*0040*/                   MOV R5, RZ ;
        |        /*0050*/                   HFMA2 R6, -RZ, RZ, 0, 0 ;
        |        /*0060*/                   UMOV UR5, URZ ;
        |        /*0070*/                   TLD.LZ.AOFFI R6, R4, R4, R7, UR4, 3D ;
        |        /*0080*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0090*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*00a0*/                   STG.E.128 desc[UR4][R2.64], R4 ;
        |        /*00b0*/                   EXIT ;
        |.L_x_10:
        |        /*00c0*/                   BRA `(.L_x_10);
        |        /*00d0*/                   NOP;
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
        |.L_x_22:
        |""".stripMargin,
    "fetch2dSm120" ->
      """//--------------------- .text.fetch2d             --------------------------
        |	.section	.text.fetch2d,"ax",@progbits
        |	.align	128
        |        .global         fetch2d
        |        .type           fetch2d,@function
        |        .size           fetch2d,(.L_x_23 - fetch2d)
        |        .other          fetch2d,@"STO_CUDA_ENTRY STV_DEFAULT"
        |fetch2d:
        |.text.fetch2d:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0030*/                   HFMA2 R6, -RZ, RZ, 0, 2.920627593994140625e-06 ;
        |        /*0040*/                   MOV R5, RZ ;
        |        /*0050*/                   UMOV UR5, URZ ;
        |        /*0060*/                   TLD.LZ.AOFFI R6, R4, R4, R6, UR4, 2D ;
        |        /*0070*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0080*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0090*/                   STG.E.128 desc[UR4][R2.64], R4 ;
        |        /*00a0*/                   EXIT ;
        |.L_x_11:
        |        /*00b0*/                   BRA `(.L_x_11);
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_23:
        |//--------------------- SYMBOLS --------------------------
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "red_add_b32_zeroSm35" ->
      """//--------------------- .text.red_add_b32_zero    --------------------------
        |	.section	.text.red_add_b32_zero,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=13"
        |	.align	64
        |        .global         red_add_b32_zero
        |        .type           red_add_b32_zero,@function
        |        .size           red_add_b32_zero,(.L_x_36 - red_add_b32_zero)
        |        .other          red_add_b32_zero,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_add_b32_zero:
        |.text.red_add_b32_zero:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R6, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R6, 0x4;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   LDG.E.T R5, [R4];
        |        /*0060*/                   LDG.E.T R12, [R2];
        |        /*0068*/                   LDG.E.P R6, [R6];
        |        /*0070*/                   S2R R4, SR_TID.X;
        |        /*0078*/                   SHF.L R0, RZ, 0x2, R4;
        |        /*0088*/                   TEXDEPBAR 0x2;
        |        /*0090*/                   SUCLAMP.SD.R4 PT, R0, R0, R5, 0x0;
        |        /*0098*/                   SUBFM P0, R2, R0, RZ, RZ;
        |        /*00a0*/                   LOP32I.AND R3, R0, 0xffff;
        |        /*00a8*/                   TEXDEPBAR 0x1;
        |        /*00b0*/                   SUEAU R3, R3, R2, R12;
        |        /*00b8*/                   TEXDEPBAR 0x0;
        |        /*00c8*/                   ISETP.GT.OR P0, PT, RZ, R6, P0;
        |        /*00d0*/                   PRMT R0, RZ, 0x7, R3;
        |        /*00d8*/                   PRMT R2, R2, 0x6540, R3;
        |        /*00e0*/                   SEL R3, R0, RZ, !P0;
        |        /*00e8*/                   SEL R2, R2, RZ, !P0;
        |        /*00f0*/               @P0 EXIT;
        |        /*00f8*/                   IADD32I R4, R4, 0x1;
        |        /*0108*/                   RED.E.ADD [R2], R4;
        |        /*0110*/                   EXIT;
        |.L_x_13:
        |        /*0118*/                   BRA `(.L_x_13);
        |.L_x_36:
        |""".stripMargin,
    "red_add_b32_zeroSm50" ->
      """//--------------------- .text.red_add_b32_zero    --------------------------
        |	.section	.text.red_add_b32_zero,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=3"
        |	.align	32
        |        .global         red_add_b32_zero
        |        .type           red_add_b32_zero,@function
        |        .size           red_add_b32_zero,(.L_x_30 - red_add_b32_zero)
        |        .other          red_add_b32_zero,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_add_b32_zero:
        |.text.red_add_b32_zero:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   IADD32I R0, R2.reuse, 0x1 ;
        |        /*0028*/                   SHL R2, R2, 0x2 ;
        |        /*0030*/                   SURED.D.BA.1D.ADD.IGN [R2], R0, 0x50 ;
        |        /*0038*/                   NOP ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   EXIT ;
        |.L_x_12:
        |        /*0058*/                   BRA `(.L_x_12) ;
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_30:
        |""".stripMargin,
    "red_add_b32_clampSm35" ->
      """//--------------------- .text.red_add_b32_clamp   --------------------------
        |	.section	.text.red_add_b32_clamp,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=13"
        |	.align	64
        |        .global         red_add_b32_clamp
        |        .type           red_add_b32_clamp,@function
        |        .size           red_add_b32_clamp,(.L_x_35 - red_add_b32_clamp)
        |        .other          red_add_b32_clamp,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_add_b32_clamp:
        |.text.red_add_b32_clamp:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R6, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R6, 0x4;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   LDG.E.T R5, [R4];
        |        /*0060*/                   LDG.E.T R12, [R2];
        |        /*0068*/                   LDG.E.P R6, [R6];
        |        /*0070*/                   S2R R4, SR_TID.X;
        |        /*0078*/                   SHF.L R0, RZ, 0x2, R4;
        |        /*0088*/                   TEXDEPBAR 0x2;
        |        /*0090*/                   SUCLAMP.SD.R4 PT, R0, R0, R5, 0x0;
        |        /*0098*/                   SUBFM PT, R2, R0, RZ, RZ;
        |        /*00a0*/                   LOP32I.AND R3, R0, 0xffff;
        |        /*00a8*/                   TEXDEPBAR 0x1;
        |        /*00b0*/                   SUEAU R3, R3, R2, R12;
        |        /*00b8*/                   TEXDEPBAR 0x0;
        |        /*00c8*/                   ISETP.LT.AND P0, PT, R6, RZ, PT;
        |        /*00d0*/                   PRMT R0, RZ, 0x7, R3;
        |        /*00d8*/                   PRMT R2, R2, 0x6540, R3;
        |        /*00e0*/                   ICMP.GE R3, R0, RZ, R6;
        |        /*00e8*/                   ICMP.GE R2, R2, RZ, R6;
        |        /*00f0*/               @P0 EXIT;
        |        /*00f8*/                   IADD32I R4, R4, 0x1;
        |        /*0108*/                   RED.E.ADD [R2], R4;
        |        /*0110*/                   EXIT;
        |.L_x_12:
        |        /*0118*/                   BRA `(.L_x_12);
        |.L_x_35:
        |""".stripMargin,
    "red_add_b32_clampSm50" ->
      """//--------------------- .text.red_add_b32_clamp   --------------------------
        |	.section	.text.red_add_b32_clamp,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=3"
        |	.align	32
        |        .global         red_add_b32_clamp
        |        .type           red_add_b32_clamp,@function
        |        .size           red_add_b32_clamp,(.L_x_29 - red_add_b32_clamp)
        |        .other          red_add_b32_clamp,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_add_b32_clamp:
        |.text.red_add_b32_clamp:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   IADD32I R0, R2.reuse, 0x1 ;
        |        /*0028*/                   SHL R2, R2, 0x2 ;
        |        /*0030*/                   SURED.D.BA.1D.ADD [R2], R0, 0x50 ;
        |        /*0038*/                   NOP ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   EXIT ;
        |.L_x_11:
        |        /*0058*/                   BRA `(.L_x_11) ;
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_29:
        |""".stripMargin,
    "red_add_b32Sm50" ->
      """//--------------------- .text.red_add_b32         --------------------------
        |	.section	.text.red_add_b32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=3"
        |	.align	32
        |        .global         red_add_b32
        |        .type           red_add_b32,@function
        |        .size           red_add_b32,(.L_x_35 - red_add_b32)
        |        .other          red_add_b32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_add_b32:
        |.text.red_add_b32:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   IADD32I R0, R2.reuse, 0x1 ;
        |        /*0028*/                   BFI R2, R2, 0x202, RZ ;
        |        /*0030*/                   SURED.D.BA.1D.ADD.TRAP [R2], R0, 0x50 ;
        |        /*0038*/                   NOP ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   EXIT ;
        |.L_x_17:
        |        /*0058*/                   BRA `(.L_x_17) ;
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_35:
        |""".stripMargin,
    "red_min_b32_zeroSm35" ->
      """//--------------------- .text.red_min_b32_zero    --------------------------
        |	.section	.text.red_min_b32_zero,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=13"
        |	.align	64
        |        .global         red_min_b32_zero
        |        .type           red_min_b32_zero,@function
        |        .size           red_min_b32_zero,(.L_x_33 - red_min_b32_zero)
        |        .other          red_min_b32_zero,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_min_b32_zero:
        |.text.red_min_b32_zero:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R6, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R6, 0x4;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   LDG.E.T R5, [R4];
        |        /*0060*/                   LDG.E.T R12, [R2];
        |        /*0068*/                   LDG.E.P R6, [R6];
        |        /*0070*/                   S2R R4, SR_TID.X;
        |        /*0078*/                   SHF.L R0, RZ, 0x2, R4;
        |        /*0088*/                   TEXDEPBAR 0x2;
        |        /*0090*/                   SUCLAMP.SD.R4 PT, R0, R0, R5, 0x0;
        |        /*0098*/                   SUBFM P0, R2, R0, RZ, RZ;
        |        /*00a0*/                   LOP32I.AND R3, R0, 0xffff;
        |        /*00a8*/                   TEXDEPBAR 0x1;
        |        /*00b0*/                   SUEAU R3, R3, R2, R12;
        |        /*00b8*/                   TEXDEPBAR 0x0;
        |        /*00c8*/                   ISETP.GT.OR P0, PT, RZ, R6, P0;
        |        /*00d0*/                   PRMT R0, RZ, 0x7, R3;
        |        /*00d8*/                   PRMT R2, R2, 0x6540, R3;
        |        /*00e0*/                   SEL R3, R0, RZ, !P0;
        |        /*00e8*/                   SEL R2, R2, RZ, !P0;
        |        /*00f0*/               @P0 EXIT;
        |        /*00f8*/                   IADD32I R4, R4, 0x1;
        |        /*0108*/                   RED.E.MIN [R2], R4;
        |        /*0110*/                   EXIT;
        |.L_x_9:
        |        /*0118*/                   BRA `(.L_x_9);
        |.L_x_33:
        |""".stripMargin,
    "red_min_b32_zeroSm50" ->
      """//--------------------- .text.red_min_b32_zero    --------------------------
        |	.section	.text.red_min_b32_zero,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=3"
        |	.align	32
        |        .global         red_min_b32_zero
        |        .type           red_min_b32_zero,@function
        |        .size           red_min_b32_zero,(.L_x_27 - red_min_b32_zero)
        |        .other          red_min_b32_zero,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_min_b32_zero:
        |.text.red_min_b32_zero:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   IADD32I R0, R2.reuse, 0x1 ;
        |        /*0028*/                   SHL R2, R2, 0x2 ;
        |        /*0030*/                   SURED.D.BA.1D.MIN.IGN [R2], R0, 0x50 ;
        |        /*0038*/                   NOP ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   EXIT ;
        |.L_x_9:
        |        /*0058*/                   BRA `(.L_x_9) ;
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_27:
        |""".stripMargin,
    "red_min_b32_clampSm35" ->
      """//--------------------- .text.red_min_b32_clamp   --------------------------
        |	.section	.text.red_min_b32_clamp,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=13"
        |	.align	64
        |        .global         red_min_b32_clamp
        |        .type           red_min_b32_clamp,@function
        |        .size           red_min_b32_clamp,(.L_x_32 - red_min_b32_clamp)
        |        .other          red_min_b32_clamp,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_min_b32_clamp:
        |.text.red_min_b32_clamp:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R6, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R6, 0x4;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   LDG.E.T R5, [R4];
        |        /*0060*/                   LDG.E.T R12, [R2];
        |        /*0068*/                   LDG.E.P R6, [R6];
        |        /*0070*/                   S2R R4, SR_TID.X;
        |        /*0078*/                   SHF.L R0, RZ, 0x2, R4;
        |        /*0088*/                   TEXDEPBAR 0x2;
        |        /*0090*/                   SUCLAMP.SD.R4 PT, R0, R0, R5, 0x0;
        |        /*0098*/                   SUBFM PT, R2, R0, RZ, RZ;
        |        /*00a0*/                   LOP32I.AND R3, R0, 0xffff;
        |        /*00a8*/                   TEXDEPBAR 0x1;
        |        /*00b0*/                   SUEAU R3, R3, R2, R12;
        |        /*00b8*/                   TEXDEPBAR 0x0;
        |        /*00c8*/                   ISETP.LT.AND P0, PT, R6, RZ, PT;
        |        /*00d0*/                   PRMT R0, RZ, 0x7, R3;
        |        /*00d8*/                   PRMT R2, R2, 0x6540, R3;
        |        /*00e0*/                   ICMP.GE R3, R0, RZ, R6;
        |        /*00e8*/                   ICMP.GE R2, R2, RZ, R6;
        |        /*00f0*/               @P0 EXIT;
        |        /*00f8*/                   IADD32I R4, R4, 0x1;
        |        /*0108*/                   RED.E.MIN [R2], R4;
        |        /*0110*/                   EXIT;
        |.L_x_8:
        |        /*0118*/                   BRA `(.L_x_8);
        |.L_x_32:
        |""".stripMargin,
    "red_min_b32_clampSm50" ->
      """//--------------------- .text.red_min_b32_clamp   --------------------------
        |	.section	.text.red_min_b32_clamp,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=3"
        |	.align	32
        |        .global         red_min_b32_clamp
        |        .type           red_min_b32_clamp,@function
        |        .size           red_min_b32_clamp,(.L_x_26 - red_min_b32_clamp)
        |        .other          red_min_b32_clamp,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_min_b32_clamp:
        |.text.red_min_b32_clamp:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   IADD32I R0, R2.reuse, 0x1 ;
        |        /*0028*/                   SHL R2, R2, 0x2 ;
        |        /*0030*/                   SURED.D.BA.1D.MIN [R2], R0, 0x50 ;
        |        /*0038*/                   NOP ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   EXIT ;
        |.L_x_8:
        |        /*0058*/                   BRA `(.L_x_8) ;
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_26:
        |""".stripMargin,
    "red_min_b32Sm50" ->
      """//--------------------- .text.red_min_b32         --------------------------
        |	.section	.text.red_min_b32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=3"
        |	.align	32
        |        .global         red_min_b32
        |        .type           red_min_b32,@function
        |        .size           red_min_b32,(.L_x_34 - red_min_b32)
        |        .other          red_min_b32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_min_b32:
        |.text.red_min_b32:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   IADD32I R0, R2.reuse, 0x1 ;
        |        /*0028*/                   BFI R2, R2, 0x202, RZ ;
        |        /*0030*/                   SURED.D.BA.1D.MIN.TRAP [R2], R0, 0x50 ;
        |        /*0038*/                   NOP ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   EXIT ;
        |.L_x_16:
        |        /*0058*/                   BRA `(.L_x_16) ;
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_34:
        |""".stripMargin,
    "red_max_b32_zeroSm35" ->
      """//--------------------- .text.red_max_b32_zero    --------------------------
        |	.section	.text.red_max_b32_zero,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=13"
        |	.align	64
        |        .global         red_max_b32_zero
        |        .type           red_max_b32_zero,@function
        |        .size           red_max_b32_zero,(.L_x_31 - red_max_b32_zero)
        |        .other          red_max_b32_zero,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_max_b32_zero:
        |.text.red_max_b32_zero:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R6, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R6, 0x4;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   LDG.E.T R5, [R4];
        |        /*0060*/                   LDG.E.T R12, [R2];
        |        /*0068*/                   LDG.E.P R6, [R6];
        |        /*0070*/                   S2R R4, SR_TID.X;
        |        /*0078*/                   SHF.L R0, RZ, 0x2, R4;
        |        /*0088*/                   TEXDEPBAR 0x2;
        |        /*0090*/                   SUCLAMP.SD.R4 PT, R0, R0, R5, 0x0;
        |        /*0098*/                   SUBFM P0, R2, R0, RZ, RZ;
        |        /*00a0*/                   LOP32I.AND R3, R0, 0xffff;
        |        /*00a8*/                   TEXDEPBAR 0x1;
        |        /*00b0*/                   SUEAU R3, R3, R2, R12;
        |        /*00b8*/                   TEXDEPBAR 0x0;
        |        /*00c8*/                   ISETP.GT.OR P0, PT, RZ, R6, P0;
        |        /*00d0*/                   PRMT R0, RZ, 0x7, R3;
        |        /*00d8*/                   PRMT R2, R2, 0x6540, R3;
        |        /*00e0*/                   SEL R3, R0, RZ, !P0;
        |        /*00e8*/                   SEL R2, R2, RZ, !P0;
        |        /*00f0*/               @P0 EXIT;
        |        /*00f8*/                   IADD32I R4, R4, 0x1;
        |        /*0108*/                   RED.E.MAX [R2], R4;
        |        /*0110*/                   EXIT;
        |.L_x_7:
        |        /*0118*/                   BRA `(.L_x_7);
        |.L_x_31:
        |""".stripMargin,
    "red_max_b32_zeroSm50" ->
      """//--------------------- .text.red_max_b32_zero    --------------------------
        |	.section	.text.red_max_b32_zero,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=3"
        |	.align	32
        |        .global         red_max_b32_zero
        |        .type           red_max_b32_zero,@function
        |        .size           red_max_b32_zero,(.L_x_25 - red_max_b32_zero)
        |        .other          red_max_b32_zero,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_max_b32_zero:
        |.text.red_max_b32_zero:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   IADD32I R0, R2.reuse, 0x1 ;
        |        /*0028*/                   SHL R2, R2, 0x2 ;
        |        /*0030*/                   SURED.D.BA.1D.MAX.IGN [R2], R0, 0x50 ;
        |        /*0038*/                   NOP ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   EXIT ;
        |.L_x_7:
        |        /*0058*/                   BRA `(.L_x_7) ;
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_25:
        |""".stripMargin,
    "red_max_b32_clampSm35" ->
      """//--------------------- .text.red_max_b32_clamp   --------------------------
        |	.section	.text.red_max_b32_clamp,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=13"
        |	.align	64
        |        .global         red_max_b32_clamp
        |        .type           red_max_b32_clamp,@function
        |        .size           red_max_b32_clamp,(.L_x_30 - red_max_b32_clamp)
        |        .other          red_max_b32_clamp,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_max_b32_clamp:
        |.text.red_max_b32_clamp:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R6, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R6, 0x4;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   LDG.E.T R5, [R4];
        |        /*0060*/                   LDG.E.T R12, [R2];
        |        /*0068*/                   LDG.E.P R6, [R6];
        |        /*0070*/                   S2R R4, SR_TID.X;
        |        /*0078*/                   SHF.L R0, RZ, 0x2, R4;
        |        /*0088*/                   TEXDEPBAR 0x2;
        |        /*0090*/                   SUCLAMP.SD.R4 PT, R0, R0, R5, 0x0;
        |        /*0098*/                   SUBFM PT, R2, R0, RZ, RZ;
        |        /*00a0*/                   LOP32I.AND R3, R0, 0xffff;
        |        /*00a8*/                   TEXDEPBAR 0x1;
        |        /*00b0*/                   SUEAU R3, R3, R2, R12;
        |        /*00b8*/                   TEXDEPBAR 0x0;
        |        /*00c8*/                   ISETP.LT.AND P0, PT, R6, RZ, PT;
        |        /*00d0*/                   PRMT R0, RZ, 0x7, R3;
        |        /*00d8*/                   PRMT R2, R2, 0x6540, R3;
        |        /*00e0*/                   ICMP.GE R3, R0, RZ, R6;
        |        /*00e8*/                   ICMP.GE R2, R2, RZ, R6;
        |        /*00f0*/               @P0 EXIT;
        |        /*00f8*/                   IADD32I R4, R4, 0x1;
        |        /*0108*/                   RED.E.MAX [R2], R4;
        |        /*0110*/                   EXIT;
        |.L_x_6:
        |        /*0118*/                   BRA `(.L_x_6);
        |.L_x_30:
        |""".stripMargin,
    "red_max_b32_clampSm50" ->
      """//--------------------- .text.red_max_b32_clamp   --------------------------
        |	.section	.text.red_max_b32_clamp,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=3"
        |	.align	32
        |        .global         red_max_b32_clamp
        |        .type           red_max_b32_clamp,@function
        |        .size           red_max_b32_clamp,(.L_x_24 - red_max_b32_clamp)
        |        .other          red_max_b32_clamp,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_max_b32_clamp:
        |.text.red_max_b32_clamp:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   IADD32I R0, R2.reuse, 0x1 ;
        |        /*0028*/                   SHL R2, R2, 0x2 ;
        |        /*0030*/                   SURED.D.BA.1D.MAX [R2], R0, 0x50 ;
        |        /*0038*/                   NOP ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   EXIT ;
        |.L_x_6:
        |        /*0058*/                   BRA `(.L_x_6) ;
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_24:
        |""".stripMargin,
    "red_max_b32Sm50" ->
      """//--------------------- .text.red_max_b32         --------------------------
        |	.section	.text.red_max_b32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=3"
        |	.align	32
        |        .global         red_max_b32
        |        .type           red_max_b32,@function
        |        .size           red_max_b32,(.L_x_33 - red_max_b32)
        |        .other          red_max_b32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_max_b32:
        |.text.red_max_b32:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   IADD32I R0, R2.reuse, 0x1 ;
        |        /*0028*/                   BFI R2, R2, 0x202, RZ ;
        |        /*0030*/                   SURED.D.BA.1D.MAX.TRAP [R2], R0, 0x50 ;
        |        /*0038*/                   NOP ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   EXIT ;
        |.L_x_15:
        |        /*0058*/                   BRA `(.L_x_15) ;
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_33:
        |""".stripMargin,
    "red_and_b32_zeroSm35" ->
      """//--------------------- .text.red_and_b32_zero    --------------------------
        |	.section	.text.red_and_b32_zero,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=13"
        |	.align	64
        |        .global         red_and_b32_zero
        |        .type           red_and_b32_zero,@function
        |        .size           red_and_b32_zero,(.L_x_29 - red_and_b32_zero)
        |        .other          red_and_b32_zero,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_and_b32_zero:
        |.text.red_and_b32_zero:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R6, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R6, 0x4;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   LDG.E.T R5, [R4];
        |        /*0060*/                   LDG.E.T R12, [R2];
        |        /*0068*/                   LDG.E.P R6, [R6];
        |        /*0070*/                   S2R R4, SR_TID.X;
        |        /*0078*/                   SHF.L R0, RZ, 0x2, R4;
        |        /*0088*/                   TEXDEPBAR 0x2;
        |        /*0090*/                   SUCLAMP.SD.R4 PT, R0, R0, R5, 0x0;
        |        /*0098*/                   SUBFM P0, R2, R0, RZ, RZ;
        |        /*00a0*/                   LOP32I.AND R3, R0, 0xffff;
        |        /*00a8*/                   TEXDEPBAR 0x1;
        |        /*00b0*/                   SUEAU R3, R3, R2, R12;
        |        /*00b8*/                   TEXDEPBAR 0x0;
        |        /*00c8*/                   ISETP.GT.OR P0, PT, RZ, R6, P0;
        |        /*00d0*/                   PRMT R0, RZ, 0x7, R3;
        |        /*00d8*/                   PRMT R2, R2, 0x6540, R3;
        |        /*00e0*/                   SEL R3, R0, RZ, !P0;
        |        /*00e8*/                   SEL R2, R2, RZ, !P0;
        |        /*00f0*/               @P0 EXIT;
        |        /*00f8*/                   IADD32I R4, R4, 0x1;
        |        /*0108*/                   RED.E.AND [R2], R4;
        |        /*0110*/                   EXIT;
        |.L_x_5:
        |        /*0118*/                   BRA `(.L_x_5);
        |.L_x_29:
        |""".stripMargin,
    "red_and_b32_zeroSm50" ->
      """//--------------------- .text.red_and_b32_zero    --------------------------
        |	.section	.text.red_and_b32_zero,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=3"
        |	.align	32
        |        .global         red_and_b32_zero
        |        .type           red_and_b32_zero,@function
        |        .size           red_and_b32_zero,(.L_x_23 - red_and_b32_zero)
        |        .other          red_and_b32_zero,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_and_b32_zero:
        |.text.red_and_b32_zero:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   IADD32I R0, R2.reuse, 0x1 ;
        |        /*0028*/                   SHL R2, R2, 0x2 ;
        |        /*0030*/                   SURED.D.BA.1D.AND.IGN [R2], R0, 0x50 ;
        |        /*0038*/                   NOP ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   EXIT ;
        |.L_x_5:
        |        /*0058*/                   BRA `(.L_x_5) ;
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_23:
        |""".stripMargin,
    "red_and_b32_clampSm35" ->
      """//--------------------- .text.red_and_b32_clamp   --------------------------
        |	.section	.text.red_and_b32_clamp,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=13"
        |	.align	64
        |        .global         red_and_b32_clamp
        |        .type           red_and_b32_clamp,@function
        |        .size           red_and_b32_clamp,(.L_x_28 - red_and_b32_clamp)
        |        .other          red_and_b32_clamp,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_and_b32_clamp:
        |.text.red_and_b32_clamp:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R6, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R6, 0x4;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   LDG.E.T R5, [R4];
        |        /*0060*/                   LDG.E.T R12, [R2];
        |        /*0068*/                   LDG.E.P R6, [R6];
        |        /*0070*/                   S2R R4, SR_TID.X;
        |        /*0078*/                   SHF.L R0, RZ, 0x2, R4;
        |        /*0088*/                   TEXDEPBAR 0x2;
        |        /*0090*/                   SUCLAMP.SD.R4 PT, R0, R0, R5, 0x0;
        |        /*0098*/                   SUBFM PT, R2, R0, RZ, RZ;
        |        /*00a0*/                   LOP32I.AND R3, R0, 0xffff;
        |        /*00a8*/                   TEXDEPBAR 0x1;
        |        /*00b0*/                   SUEAU R3, R3, R2, R12;
        |        /*00b8*/                   TEXDEPBAR 0x0;
        |        /*00c8*/                   ISETP.LT.AND P0, PT, R6, RZ, PT;
        |        /*00d0*/                   PRMT R0, RZ, 0x7, R3;
        |        /*00d8*/                   PRMT R2, R2, 0x6540, R3;
        |        /*00e0*/                   ICMP.GE R3, R0, RZ, R6;
        |        /*00e8*/                   ICMP.GE R2, R2, RZ, R6;
        |        /*00f0*/               @P0 EXIT;
        |        /*00f8*/                   IADD32I R4, R4, 0x1;
        |        /*0108*/                   RED.E.AND [R2], R4;
        |        /*0110*/                   EXIT;
        |.L_x_4:
        |        /*0118*/                   BRA `(.L_x_4);
        |.L_x_28:
        |""".stripMargin,
    "red_and_b32_clampSm50" ->
      """//--------------------- .text.red_and_b32_clamp   --------------------------
        |	.section	.text.red_and_b32_clamp,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=3"
        |	.align	32
        |        .global         red_and_b32_clamp
        |        .type           red_and_b32_clamp,@function
        |        .size           red_and_b32_clamp,(.L_x_22 - red_and_b32_clamp)
        |        .other          red_and_b32_clamp,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_and_b32_clamp:
        |.text.red_and_b32_clamp:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   IADD32I R0, R2.reuse, 0x1 ;
        |        /*0028*/                   SHL R2, R2, 0x2 ;
        |        /*0030*/                   SURED.D.BA.1D.AND [R2], R0, 0x50 ;
        |        /*0038*/                   NOP ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   EXIT ;
        |.L_x_4:
        |        /*0058*/                   BRA `(.L_x_4) ;
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_22:
        |""".stripMargin,
    "red_and_b32Sm50" ->
      """//--------------------- .text.red_and_b32         --------------------------
        |	.section	.text.red_and_b32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=3"
        |	.align	32
        |        .global         red_and_b32
        |        .type           red_and_b32,@function
        |        .size           red_and_b32,(.L_x_32 - red_and_b32)
        |        .other          red_and_b32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_and_b32:
        |.text.red_and_b32:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   IADD32I R0, R2.reuse, 0x1 ;
        |        /*0028*/                   BFI R2, R2, 0x202, RZ ;
        |        /*0030*/                   SURED.D.BA.1D.AND.TRAP [R2], R0, 0x50 ;
        |        /*0038*/                   NOP ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   EXIT ;
        |.L_x_14:
        |        /*0058*/                   BRA `(.L_x_14) ;
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_32:
        |""".stripMargin,
    "red_or_b32_zeroSm35" ->
      """//--------------------- .text.red_or_b32_zero     --------------------------
        |	.section	.text.red_or_b32_zero,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=13"
        |	.align	64
        |        .global         red_or_b32_zero
        |        .type           red_or_b32_zero,@function
        |        .size           red_or_b32_zero,(.L_x_27 - red_or_b32_zero)
        |        .other          red_or_b32_zero,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_or_b32_zero:
        |.text.red_or_b32_zero:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R6, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R6, 0x4;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   LDG.E.T R5, [R4];
        |        /*0060*/                   LDG.E.T R12, [R2];
        |        /*0068*/                   LDG.E.P R6, [R6];
        |        /*0070*/                   S2R R4, SR_TID.X;
        |        /*0078*/                   SHF.L R0, RZ, 0x2, R4;
        |        /*0088*/                   TEXDEPBAR 0x2;
        |        /*0090*/                   SUCLAMP.SD.R4 PT, R0, R0, R5, 0x0;
        |        /*0098*/                   SUBFM P0, R2, R0, RZ, RZ;
        |        /*00a0*/                   LOP32I.AND R3, R0, 0xffff;
        |        /*00a8*/                   TEXDEPBAR 0x1;
        |        /*00b0*/                   SUEAU R3, R3, R2, R12;
        |        /*00b8*/                   TEXDEPBAR 0x0;
        |        /*00c8*/                   ISETP.GT.OR P0, PT, RZ, R6, P0;
        |        /*00d0*/                   PRMT R0, RZ, 0x7, R3;
        |        /*00d8*/                   PRMT R2, R2, 0x6540, R3;
        |        /*00e0*/                   SEL R3, R0, RZ, !P0;
        |        /*00e8*/                   SEL R2, R2, RZ, !P0;
        |        /*00f0*/               @P0 EXIT;
        |        /*00f8*/                   IADD32I R4, R4, 0x1;
        |        /*0108*/                   RED.E.OR [R2], R4;
        |        /*0110*/                   EXIT;
        |.L_x_3:
        |        /*0118*/                   BRA `(.L_x_3);
        |.L_x_27:
        |""".stripMargin,
    "red_or_b32_zeroSm50" ->
      """//--------------------- .text.red_or_b32_zero     --------------------------
        |	.section	.text.red_or_b32_zero,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=3"
        |	.align	32
        |        .global         red_or_b32_zero
        |        .type           red_or_b32_zero,@function
        |        .size           red_or_b32_zero,(.L_x_21 - red_or_b32_zero)
        |        .other          red_or_b32_zero,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_or_b32_zero:
        |.text.red_or_b32_zero:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   IADD32I R0, R2.reuse, 0x1 ;
        |        /*0028*/                   SHL R2, R2, 0x2 ;
        |        /*0030*/                   SURED.D.BA.1D.OR.IGN [R2], R0, 0x50 ;
        |        /*0038*/                   NOP ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   EXIT ;
        |.L_x_3:
        |        /*0058*/                   BRA `(.L_x_3) ;
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_21:
        |""".stripMargin,
    "red_or_b32_clampSm35" ->
      """//--------------------- .text.red_or_b32_clamp    --------------------------
        |	.section	.text.red_or_b32_clamp,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=13"
        |	.align	64
        |        .global         red_or_b32_clamp
        |        .type           red_or_b32_clamp,@function
        |        .size           red_or_b32_clamp,(.L_x_26 - red_or_b32_clamp)
        |        .other          red_or_b32_clamp,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_or_b32_clamp:
        |.text.red_or_b32_clamp:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R6, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R6, 0x4;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   LDG.E.T R5, [R4];
        |        /*0060*/                   LDG.E.T R12, [R2];
        |        /*0068*/                   LDG.E.P R6, [R6];
        |        /*0070*/                   S2R R4, SR_TID.X;
        |        /*0078*/                   SHF.L R0, RZ, 0x2, R4;
        |        /*0088*/                   TEXDEPBAR 0x2;
        |        /*0090*/                   SUCLAMP.SD.R4 PT, R0, R0, R5, 0x0;
        |        /*0098*/                   SUBFM PT, R2, R0, RZ, RZ;
        |        /*00a0*/                   LOP32I.AND R3, R0, 0xffff;
        |        /*00a8*/                   TEXDEPBAR 0x1;
        |        /*00b0*/                   SUEAU R3, R3, R2, R12;
        |        /*00b8*/                   TEXDEPBAR 0x0;
        |        /*00c8*/                   ISETP.LT.AND P0, PT, R6, RZ, PT;
        |        /*00d0*/                   PRMT R0, RZ, 0x7, R3;
        |        /*00d8*/                   PRMT R2, R2, 0x6540, R3;
        |        /*00e0*/                   ICMP.GE R3, R0, RZ, R6;
        |        /*00e8*/                   ICMP.GE R2, R2, RZ, R6;
        |        /*00f0*/               @P0 EXIT;
        |        /*00f8*/                   IADD32I R4, R4, 0x1;
        |        /*0108*/                   RED.E.OR [R2], R4;
        |        /*0110*/                   EXIT;
        |.L_x_2:
        |        /*0118*/                   BRA `(.L_x_2);
        |.L_x_26:
        |""".stripMargin,
    "red_or_b32_clampSm50" ->
      """//--------------------- .text.red_or_b32_clamp    --------------------------
        |	.section	.text.red_or_b32_clamp,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=3"
        |	.align	32
        |        .global         red_or_b32_clamp
        |        .type           red_or_b32_clamp,@function
        |        .size           red_or_b32_clamp,(.L_x_20 - red_or_b32_clamp)
        |        .other          red_or_b32_clamp,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_or_b32_clamp:
        |.text.red_or_b32_clamp:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   IADD32I R0, R2.reuse, 0x1 ;
        |        /*0028*/                   SHL R2, R2, 0x2 ;
        |        /*0030*/                   SURED.D.BA.1D.OR [R2], R0, 0x50 ;
        |        /*0038*/                   NOP ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   EXIT ;
        |.L_x_2:
        |        /*0058*/                   BRA `(.L_x_2) ;
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_20:
        |""".stripMargin,
    "red_or_b32Sm50" ->
      """//--------------------- .text.red_or_b32          --------------------------
        |	.section	.text.red_or_b32,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=3"
        |	.align	32
        |        .global         red_or_b32
        |        .type           red_or_b32,@function
        |        .size           red_or_b32,(.L_x_31 - red_or_b32)
        |        .other          red_or_b32,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_or_b32:
        |.text.red_or_b32:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0018*/                   IADD32I R0, R2.reuse, 0x1 ;
        |        /*0028*/                   BFI R2, R2, 0x202, RZ ;
        |        /*0030*/                   SURED.D.BA.1D.OR.TRAP [R2], R0, 0x50 ;
        |        /*0038*/                   NOP ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   EXIT ;
        |.L_x_13:
        |        /*0058*/                   BRA `(.L_x_13) ;
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_31:
        |""".stripMargin,
    "red_add_b64_zeroSm35" ->
      """//--------------------- .text.red_add_b64_zero    --------------------------
        |	.section	.text.red_add_b64_zero,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	64
        |        .global         red_add_b64_zero
        |        .type           red_add_b64_zero,@function
        |        .size           red_add_b64_zero,(.L_x_25 - red_add_b64_zero)
        |        .other          red_add_b64_zero,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_add_b64_zero:
        |.text.red_add_b64_zero:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R6, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R6, 0x4;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   LDG.E.T R4, [R4];
        |        /*0060*/                   LDG.E.T R5, [R2];
        |        /*0068*/                   LDG.E.P R6, [R6];
        |        /*0070*/                   S2R R2, SR_TID.X;
        |        /*0078*/                   SHF.L R0, RZ, 0x3, R2;
        |        /*0088*/                   IADD32I R2, R2, 0x1;
        |        /*0090*/                   TEXDEPBAR 0x2;
        |        /*0098*/                   SUCLAMP.SD.R8 PT, R0, R0, R4, 0x0;
        |        /*00a0*/                   SUBFM P0, R3, R0, RZ, RZ;
        |        /*00a8*/                   LOP32I.AND R0, R0, 0xffff;
        |        /*00b0*/                   TEXDEPBAR 0x1;
        |        /*00b8*/                   SUEAU R0, R0, R3, R5;
        |        /*00c8*/                   TEXDEPBAR 0x0;
        |        /*00d0*/                   ISETP.GT.OR P0, PT, RZ, R6, P0;
        |        /*00d8*/                   PRMT R4, RZ, 0x7, R0;
        |        /*00e0*/                   PRMT R0, R3, 0x6540, R0;
        |        /*00e8*/                   MOV R3, RZ;
        |        /*00f0*/                   SEL R5, R4, RZ, !P0;
        |        /*00f8*/                   SEL R4, R0, RZ, !P0;
        |        /*0108*/               @P0 EXIT;
        |        /*0110*/                   RED.E.ADD.U64 [R4], R2;
        |        /*0118*/                   EXIT;
        |.L_x_1:
        |        /*0120*/                   BRA `(.L_x_1);
        |.L_x_25:
        |""".stripMargin,
    "red_add_b64_zeroSm50" ->
      """//--------------------- .text.red_add_b64_zero    --------------------------
        |	.section	.text.red_add_b64_zero,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=4"
        |	.align	32
        |        .global         red_add_b64_zero
        |        .type           red_add_b64_zero,@function
        |        .size           red_add_b64_zero,(.L_x_19 - red_add_b64_zero)
        |        .other          red_add_b64_zero,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_add_b64_zero:
        |.text.red_add_b64_zero:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/         {         MOV R3, RZ ;
        |        /*0018*/                   S2R R0, SR_TID.X         }
        |        /*0028*/                   IADD32I R2, R0.reuse, 0x1 ;
        |        /*0030*/                   SHL R0, R0, 0x3 ;
        |        /*0038*/                   SURED.D.BA.1D.ADD.U64.IGN [R0], R2, 0x50 ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   NOP ;
        |        /*0058*/                   EXIT ;
        |.L_x_1:
        |        /*0068*/                   BRA `(.L_x_1) ;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_19:
        |""".stripMargin,
    "red_add_b64_clampSm35" ->
      """//--------------------- .text.red_add_b64_clamp   --------------------------
        |	.section	.text.red_add_b64_clamp,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	64
        |        .global         red_add_b64_clamp
        |        .type           red_add_b64_clamp,@function
        |        .size           red_add_b64_clamp,(.L_x_24 - red_add_b64_clamp)
        |        .other          red_add_b64_clamp,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_add_b64_clamp:
        |.text.red_add_b64_clamp:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV R6, c[0x0][0x140];
        |        /*0018*/                   MOV R2, c[0x0][0x144];
        |        /*0020*/                   IADD32I R4.CC, R6, 0x8;
        |        /*0028*/                   LOP32I.AND R3, R2, 0xff;
        |        /*0030*/                   MOV R2, c[0x0][0x140];
        |        /*0038*/                   IADD.X R5, RZ, R3;
        |        /*0048*/                   IADD32I R6.CC, R6, 0x4;
        |        /*0050*/                   IADD.X R7, RZ, R3;
        |        /*0058*/                   LDG.E.T R4, [R4];
        |        /*0060*/                   LDG.E.T R5, [R2];
        |        /*0068*/                   LDG.E.P R7, [R6];
        |        /*0070*/                   S2R R2, SR_TID.X;
        |        /*0078*/                   SHF.L R0, RZ, 0x3, R2;
        |        /*0088*/                   IADD32I R2, R2, 0x1;
        |        /*0090*/                   TEXDEPBAR 0x2;
        |        /*0098*/                   SUCLAMP.SD.R8 PT, R0, R0, R4, 0x0;
        |        /*00a0*/                   SUBFM PT, R3, R0, RZ, RZ;
        |        /*00a8*/                   LOP32I.AND R0, R0, 0xffff;
        |        /*00b0*/                   TEXDEPBAR 0x1;
        |        /*00b8*/                   SUEAU R0, R0, R3, R5;
        |        /*00c8*/                   TEXDEPBAR 0x0;
        |        /*00d0*/                   ISETP.LT.AND P0, PT, R7, RZ, PT;
        |        /*00d8*/                   PRMT R4, RZ, 0x7, R0;
        |        /*00e0*/                   PRMT R0, R3, 0x6540, R0;
        |        /*00e8*/                   MOV R3, RZ;
        |        /*00f0*/                   ICMP.GE R5, R4, RZ, R7;
        |        /*00f8*/                   ICMP.GE R4, R0, RZ, R7;
        |        /*0108*/               @P0 EXIT;
        |        /*0110*/                   RED.E.ADD.U64 [R4], R2;
        |        /*0118*/                   EXIT;
        |.L_x_0:
        |        /*0120*/                   BRA `(.L_x_0);
        |.L_x_24:
        |""".stripMargin,
    "red_add_b64_clampSm50" ->
      """//--------------------- .text.red_add_b64_clamp   --------------------------
        |	.section	.text.red_add_b64_clamp,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=4"
        |	.align	32
        |        .global         red_add_b64_clamp
        |        .type           red_add_b64_clamp,@function
        |        .size           red_add_b64_clamp,(.L_x_18 - red_add_b64_clamp)
        |        .other          red_add_b64_clamp,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_add_b64_clamp:
        |.text.red_add_b64_clamp:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/         {         MOV R3, RZ ;
        |        /*0018*/                   S2R R0, SR_TID.X         }
        |        /*0028*/                   IADD32I R2, R0.reuse, 0x1 ;
        |        /*0030*/                   SHL R0, R0, 0x3 ;
        |        /*0038*/                   SURED.D.BA.1D.ADD.U64 [R0], R2, 0x50 ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   NOP ;
        |        /*0058*/                   EXIT ;
        |.L_x_0:
        |        /*0068*/                   BRA `(.L_x_0) ;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_18:
        |""".stripMargin,
    "red_add_b64Sm50" ->
      """//--------------------- .text.red_add_b64         --------------------------
        |	.section	.text.red_add_b64,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=4"
        |	.align	32
        |        .global         red_add_b64
        |        .type           red_add_b64,@function
        |        .size           red_add_b64,(.L_x_28 - red_add_b64)
        |        .other          red_add_b64,@"STO_CUDA_ENTRY STV_DEFAULT"
        |red_add_b64:
        |.text.red_add_b64:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/         {         MOV R3, RZ ;
        |        /*0018*/                   S2R R0, SR_TID.X         }
        |        /*0028*/                   IADD32I R2, R0.reuse, 0x1 ;
        |        /*0030*/                   BFI R0, R0, 0x203, RZ ;
        |        /*0038*/                   SURED.D.BA.1D.ADD.U64.TRAP [R0], R2, 0x50 ;
        |        /*0048*/                   NOP ;
        |        /*0050*/                   NOP ;
        |        /*0058*/                   EXIT ;
        |.L_x_10:
        |        /*0068*/                   BRA `(.L_x_10) ;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_28:
        |""".stripMargin,
    "lodKernelSm35" ->
      """//--------------------- .text.lodKernel           --------------------------
        |	.section	.text.lodKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	64
        |        .global         lodKernel
        |        .type           lodKernel,@function
        |        .size           lodKernel,(.L_x_3 - lodKernel)
        |        .other          lodKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |lodKernel:
        |.text.lodKernel:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV32I R4, 0x3ec00000;
        |        /*0018*/                   MOV32I R5, 0x3f200000;
        |        /*0020*/                   MOV R6, c[0x0][0x150];
        |        /*0028*/                   TEX.LL.P R4, R4, 0x50, 2D, 0x1;
        |        /*0030*/                   MOV R2, c[0x0][0x148];
        |        /*0038*/                   MOV R3, c[0x0][0x14c];
        |        /*0048*/                   TEXDEPBAR 0x0;
        |        /*0050*/                   ST.E [R2], R4;
        |        /*0058*/                   EXIT;
        |.L_x_1:
        |        /*0060*/                   BRA `(.L_x_1);
        |        /*0068*/                   NOP;
        |        /*0070*/                   NOP;
        |        /*0078*/                   NOP;
        |.L_x_3:
        |""".stripMargin,
    "lodKernelSm50" ->
      """//--------------------- .text.lodKernel           --------------------------
        |	.section	.text.lodKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	32
        |        .global         lodKernel
        |        .type           lodKernel,@function
        |        .size           lodKernel,(.L_x_3 - lodKernel)
        |        .other          lodKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |lodKernel:
        |.text.lodKernel:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   MOV R6, c[0x0][0x150] ;
        |        /*0018*/                   MOV32I R4, 0x3ec00000 ;
        |        /*0028*/                   MOV32I R5, 0x3f200000 ;
        |        /*0030*/                   TEXS.LL.P RZ, R0, R4, R6, 0x50, 2D, R ;
        |        /*0038*/                   MOV R2, c[0x0][0x148] ;
        |        /*0048*/                   MOV R3, c[0x0][0x14c] ;
        |        /*0050*/                   STG.E [R2], R0 ;
        |        /*0058*/                   NOP ;
        |        /*0068*/                   NOP ;
        |        /*0070*/                   NOP ;
        |        /*0078*/                   EXIT ;
        |.L_x_1:
        |        /*0088*/                   BRA `(.L_x_1) ;
        |        /*0090*/                   NOP;
        |        /*0098*/                   NOP;
        |        /*00a8*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00b8*/                   NOP;
        |.L_x_3:
        |""".stripMargin,
    "lodKernelSm60" ->
      """//--------------------- .text.lodKernel           --------------------------
        |	.section	.text.lodKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=7"
        |	.align	32
        |        .global         lodKernel
        |        .type           lodKernel,@function
        |        .size           lodKernel,(.L_x_3 - lodKernel)
        |        .other          lodKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |lodKernel:
        |.text.lodKernel:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   MOV R6, c[0x0][0x150] ;
        |        /*0018*/                   MOV32I R4, 0x3ec00000 ;
        |        /*0028*/                   MOV32I R5, 0x3f200000 ;
        |        /*0030*/                   TEXS.LL.P RZ, R0, R4, R6, 0x50, 2D, R ;
        |        /*0038*/                   MOV R2, c[0x0][0x148] ;
        |        /*0048*/                   MOV R3, c[0x0][0x14c] ;
        |        /*0050*/                   STG.E [R2], R0 ;
        |        /*0058*/                   NOP ;
        |        /*0068*/                   NOP ;
        |        /*0070*/                   NOP ;
        |        /*0078*/                   EXIT ;
        |.L_x_1:
        |        /*0088*/                   BRA `(.L_x_1) ;
        |        /*0090*/                   NOP;
        |        /*0098*/                   NOP;
        |        /*00a8*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00b8*/                   NOP;
        |.L_x_3:
        |""".stripMargin,
    "lodKernelSm70" ->
      """//--------------------- .text.lodKernel           --------------------------
        |	.section	.text.lodKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         lodKernel
        |        .type           lodKernel,@function
        |        .size           lodKernel,(.L_x_3 - lodKernel)
        |        .other          lodKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |lodKernel:
        |.text.lodKernel:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;
        |        /*0020*/                   IMAD.MOV.U32 R0, RZ, RZ, c[0x0][0x170] ;
        |        /*0030*/                   MOV R4, 0x3ec00000 ;
        |        /*0040*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x3f200000 ;
        |        /*0050*/                   TEX.SCR.LL RZ, R5, R4, R0, 0x0, 0x58, 2D, 0x1 ;
        |        /*0060*/                   MOV R2, c[0x0][0x168] ;
        |        /*0070*/                   MOV R3, c[0x0][0x16c] ;
        |        /*0080*/                   STG.E.SYS [R2], R5 ;
        |        /*0090*/                   EXIT ;
        |.L_x_1:
        |        /*00a0*/                   BRA `(.L_x_1);
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_3:
        |""".stripMargin,
    "lodKernelSm75" ->
      """//--------------------- .text.lodKernel           --------------------------
        |	.section	.text.lodKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	128
        |        .global         lodKernel
        |        .type           lodKernel,@function
        |        .size           lodKernel,(.L_x_3 - lodKernel)
        |        .other          lodKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |lodKernel:
        |.text.lodKernel:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   IMAD.MOV.U32 R0, RZ, RZ, c[0x0][0x170] ;
        |        /*0020*/                   MOV R2, 0x3ec00000 ;
        |        /*0030*/                   MOV R3, 0x3f200000 ;
        |        /*0040*/                   TEX.SCR.LL RZ, R0, R2, R0, 0x0, 0x58, 2D, 0x1 ;
        |        /*0050*/                   ULDC.64 UR4, c[0x0][0x168] ;
        |        /*0060*/                   STG.E.SYS [UR4], R0 ;
        |        /*0070*/                   EXIT ;
        |.L_x_1:
        |        /*0080*/                   BRA `(.L_x_1);
        |        /*0090*/                   NOP;
        |        /*00a0*/                   NOP;
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_3:
        |""".stripMargin,
    "lodKernelSm80" ->
      """//--------------------- .text.lodKernel           --------------------------
        |	.section	.text.lodKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         lodKernel
        |        .type           lodKernel,@function
        |        .size           lodKernel,(.L_x_3 - lodKernel)
        |        .other          lodKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |lodKernel:
        |.text.lodKernel:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   HFMA2.MMA R4, -RZ, RZ, 1.6875, 0 ;
        |        /*0020*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x3f200000 ;
        |        /*0030*/                   IMAD.MOV.U32 R0, RZ, RZ, c[0x0][0x170] ;
        |        /*0040*/                   TEX.SCR.LL RZ, R5, R4, R0, 0x0, 0x58, 2D, 0x1 ;
        |        /*0050*/                   MOV R2, c[0x0][0x168] ;
        |        /*0060*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0070*/                   MOV R3, c[0x0][0x16c] ;
        |        /*0080*/                   STG.E [R2.64], R5 ;
        |        /*0090*/                   EXIT ;
        |.L_x_1:
        |        /*00a0*/                   BRA `(.L_x_1);
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_3:
        |""".stripMargin,
    "lodKernelSm86" ->
      """//--------------------- .text.lodKernel           --------------------------
        |	.section	.text.lodKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         lodKernel
        |        .type           lodKernel,@function
        |        .size           lodKernel,(.L_x_3 - lodKernel)
        |        .other          lodKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |lodKernel:
        |.text.lodKernel:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x3f200000 ;
        |        /*0020*/                   MOV R4, 0x3ec00000 ;
        |        /*0030*/                   IMAD.MOV.U32 R0, RZ, RZ, c[0x0][0x170] ;
        |        /*0040*/                   TEX.SCR.LL RZ, R5, R4, R0, 0x0, 0x58, 2D, 0x1 ;
        |        /*0050*/                   MOV R2, c[0x0][0x168] ;
        |        /*0060*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0070*/                   MOV R3, c[0x0][0x16c] ;
        |        /*0080*/                   STG.E [R2.64], R5 ;
        |        /*0090*/                   EXIT ;
        |.L_x_1:
        |        /*00a0*/                   BRA `(.L_x_1);
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |.L_x_3:
        |""".stripMargin,
    "gradKernelSm35" ->
      """//--------------------- .text.gradKernel          --------------------------
        |	.section	.text.gradKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	64
        |        .global         gradKernel
        |        .type           gradKernel,@function
        |        .size           gradKernel,(.L_x_2 - gradKernel)
        |        .other          gradKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gradKernel:
        |.text.gradKernel:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   MOV32I R4, 0x3ec00000;
        |        /*0018*/                   MOV32I R5, 0x3f200000;
        |        /*0020*/                   MOV R6, c[0x0][0x150];
        |        /*0028*/                   MOV R7, c[0x0][0x158];
        |        /*0030*/                   MOV R8, c[0x0][0x154];
        |        /*0038*/                   MOV R9, c[0x0][0x15c];
        |        /*0048*/                   TXD.P R4, R4, R8, 0x50, 2D, 0x1;
        |        /*0050*/                   MOV R2, c[0x0][0x148];
        |        /*0058*/                   MOV R3, c[0x0][0x14c];
        |        /*0060*/                   TEXDEPBAR 0x0;
        |        /*0068*/                   ST.E [R2], R4;
        |        /*0070*/                   EXIT;
        |.L_x_0:
        |        /*0078*/                   BRA `(.L_x_0);
        |.L_x_2:
        |""".stripMargin,
    "gradKernelSm50" ->
      """//--------------------- .text.gradKernel          --------------------------
        |	.section	.text.gradKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	32
        |        .global         gradKernel
        |        .type           gradKernel,@function
        |        .size           gradKernel,(.L_x_2 - gradKernel)
        |        .other          gradKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gradKernel:
        |.text.gradKernel:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   MOV32I R4, 0x3ec00000 ;
        |        /*0018*/                   MOV32I R5, 0x3f200000 ;
        |        /*0028*/                   MOV R8, c[0x0][0x150] ;
        |        /*0030*/                   MOV R9, c[0x0][0x158] ;
        |        /*0038*/                   MOV R10, c[0x0][0x154] ;
        |        /*0048*/                   MOV R11, c[0x0][0x15c] ;
        |        /*0050*/                   TXD.P R0, R4, R8, 0x50, 2D, 0x1 ;
        |        /*0058*/                   MOV R2, c[0x0][0x148] ;
        |        /*0068*/                   MOV R3, c[0x0][0x14c] ;
        |        /*0070*/                   STG.E [R2], R0 ;
        |        /*0078*/                   NOP ;
        |        /*0088*/                   NOP ;
        |        /*0090*/                   NOP ;
        |        /*0098*/                   EXIT ;
        |.L_x_0:
        |        /*00a8*/                   BRA `(.L_x_0) ;
        |        /*00b0*/                   NOP;
        |        /*00b8*/                   NOP;
        |.L_x_2:
        |""".stripMargin,
    "gradKernelSm60" ->
      """//--------------------- .text.gradKernel          --------------------------
        |	.section	.text.gradKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	32
        |        .global         gradKernel
        |        .type           gradKernel,@function
        |        .size           gradKernel,(.L_x_2 - gradKernel)
        |        .other          gradKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gradKernel:
        |.text.gradKernel:
        |        /*0008*/                   MOV R1, c[0x0][0x20] ;
        |        /*0010*/                   MOV32I R4, 0x3ec00000 ;
        |        /*0018*/                   MOV32I R5, 0x3f200000 ;
        |        /*0028*/                   MOV R8, c[0x0][0x150] ;
        |        /*0030*/                   MOV R9, c[0x0][0x158] ;
        |        /*0038*/                   MOV R10, c[0x0][0x154] ;
        |        /*0048*/                   MOV R11, c[0x0][0x15c] ;
        |        /*0050*/                   TXD.P R0, R4, R8, 0x50, 2D, 0x1 ;
        |        /*0058*/                   MOV R2, c[0x0][0x148] ;
        |        /*0068*/                   MOV R3, c[0x0][0x14c] ;
        |        /*0070*/                   STG.E [R2], R0 ;
        |        /*0078*/                   NOP ;
        |        /*0088*/                   NOP ;
        |        /*0090*/                   NOP ;
        |        /*0098*/                   EXIT ;
        |.L_x_0:
        |        /*00a8*/                   BRA `(.L_x_0) ;
        |        /*00b0*/                   NOP;
        |        /*00b8*/                   NOP;
        |.L_x_2:
        |""".stripMargin,
    "gradKernelSm70" ->
      """//--------------------- .text.gradKernel          --------------------------
        |	.section	.text.gradKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         gradKernel
        |        .type           gradKernel,@function
        |        .size           gradKernel,(.L_x_2 - gradKernel)
        |        .other          gradKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gradKernel:
        |.text.gradKernel:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/              @!PT SHFL.IDX PT, RZ, RZ, RZ, RZ ;
        |        /*0020*/                   MOV R4, 0x3ec00000 ;
        |        /*0030*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x3f200000 ;
        |        /*0040*/                   MOV R8, c[0x0][0x170] ;
        |        /*0050*/                   IMAD.MOV.U32 R9, RZ, RZ, c[0x0][0x178] ;
        |        /*0060*/                   MOV R10, c[0x0][0x174] ;
        |        /*0070*/                   IMAD.MOV.U32 R11, RZ, RZ, c[0x0][0x17c] ;
        |        /*0080*/                   TXD RZ, R5, R4, R8, 0x0, 0x58, 2D, 0x1 ;
        |        /*0090*/                   MOV R2, c[0x0][0x168] ;
        |        /*00a0*/                   IMAD.MOV.U32 R3, RZ, RZ, c[0x0][0x16c] ;
        |        /*00b0*/                   STG.E.SYS [R2], R5 ;
        |        /*00c0*/                   EXIT ;
        |.L_x_0:
        |        /*00d0*/                   BRA `(.L_x_0);
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_2:
        |""".stripMargin,
    "gradKernelSm75" ->
      """//--------------------- .text.gradKernel          --------------------------
        |	.section	.text.gradKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         gradKernel
        |        .type           gradKernel,@function
        |        .size           gradKernel,(.L_x_2 - gradKernel)
        |        .other          gradKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gradKernel:
        |.text.gradKernel:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   MOV R2, 0x3ec00000 ;
        |        /*0020*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x3f200000 ;
        |        /*0030*/                   MOV R4, c[0x0][0x170] ;
        |        /*0040*/                   IMAD.MOV.U32 R5, RZ, RZ, c[0x0][0x178] ;
        |        /*0050*/                   MOV R6, c[0x0][0x174] ;
        |        /*0060*/                   IMAD.MOV.U32 R7, RZ, RZ, c[0x0][0x17c] ;
        |        /*0070*/                   TXD RZ, R0, R2, R4, 0x0, 0x58, 2D, 0x1 ;
        |        /*0080*/                   ULDC.64 UR4, c[0x0][0x168] ;
        |        /*0090*/                   STG.E.SYS [UR4], R0 ;
        |        /*00a0*/                   EXIT ;
        |.L_x_0:
        |        /*00b0*/                   BRA `(.L_x_0);
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
        |        /*00e0*/                   NOP;
        |        /*00f0*/                   NOP;
        |.L_x_2:
        |""".stripMargin,
    "gradKernelSm80" ->
      """//--------------------- .text.gradKernel          --------------------------
        |	.section	.text.gradKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         gradKernel
        |        .type           gradKernel,@function
        |        .size           gradKernel,(.L_x_2 - gradKernel)
        |        .other          gradKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gradKernel:
        |.text.gradKernel:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   HFMA2.MMA R4, -RZ, RZ, 1.6875, 0 ;
        |        /*0020*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x3f200000 ;
        |        /*0030*/                   MOV R8, c[0x0][0x170] ;
        |        /*0040*/                   IMAD.MOV.U32 R9, RZ, RZ, c[0x0][0x178] ;
        |        /*0050*/                   MOV R10, c[0x0][0x174] ;
        |        /*0060*/                   IMAD.MOV.U32 R11, RZ, RZ, c[0x0][0x17c] ;
        |        /*0070*/                   TXD RZ, R5, R4, R8, 0x0, 0x58, 2D, 0x1 ;
        |        /*0080*/                   MOV R2, c[0x0][0x168] ;
        |        /*0090*/                   IMAD.MOV.U32 R3, RZ, RZ, c[0x0][0x16c] ;
        |        /*00a0*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*00b0*/                   STG.E [R2.64], R5 ;
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
        |.L_x_2:
        |""".stripMargin,
    "gradKernelSm86" ->
      """//--------------------- .text.gradKernel          --------------------------
        |	.section	.text.gradKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         gradKernel
        |        .type           gradKernel,@function
        |        .size           gradKernel,(.L_x_2 - gradKernel)
        |        .other          gradKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gradKernel:
        |.text.gradKernel:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x3f200000 ;
        |        /*0020*/                   MOV R4, 0x3ec00000 ;
        |        /*0030*/                   IMAD.MOV.U32 R9, RZ, RZ, c[0x0][0x178] ;
        |        /*0040*/                   MOV R8, c[0x0][0x170] ;
        |        /*0050*/                   IMAD.MOV.U32 R11, RZ, RZ, c[0x0][0x17c] ;
        |        /*0060*/                   MOV R10, c[0x0][0x174] ;
        |        /*0070*/                   TXD RZ, R5, R4, R8, 0x0, 0x58, 2D, 0x1 ;
        |        /*0080*/                   MOV R2, c[0x0][0x168] ;
        |        /*0090*/                   IMAD.MOV.U32 R3, RZ, RZ, c[0x0][0x16c] ;
        |        /*00a0*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*00b0*/                   STG.E [R2.64], R5 ;
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
        |.L_x_2:
        |""".stripMargin,
    // sm_89 and later listings from CUDA 13.3 (nvcc V13.3.73)
    "lodKernelSm89" ->
      """//--------------------- .text.lodKernel           --------------------------
        |	.section	.text.lodKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         lodKernel
        |        .type           lodKernel,@function
        |        .size           lodKernel,(.L_x_1 - lodKernel)
        |        .other          lodKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |lodKernel:
        |.text.lodKernel:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x3f200000 ;
        |        /*0020*/                   MOV R4, 0x3ec00000 ;
        |        /*0030*/                   IMAD.MOV.U32 R0, RZ, RZ, c[0x0][0x170] ;
        |        /*0040*/                   TEX.SCR.LL RZ, R5, R4, R0, 0x0, 0x58, 2D, 0x1 ;
        |        /*0050*/                   MOV R2, c[0x0][0x168] ;
        |        /*0060*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0070*/                   MOV R3, c[0x0][0x16c] ;
        |        /*0080*/                   STG.E [R2.64], R5 ;
        |        /*0090*/                   EXIT ;
        |.L_x_0:
        |        /*00a0*/                   BRA `(.L_x_0);
        |        /*00b0*/                   NOP;
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |""".stripMargin,
    "lodKernelSm90" ->
      """//--------------------- .text.lodKernel           --------------------------
        |	.section	.text.lodKernel,"ax",@progbits
        |	.align	128
        |        .global         lodKernel
        |        .type           lodKernel,@function
        |        .size           lodKernel,(.L_x_1 - lodKernel)
        |        .other          lodKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |lodKernel:
        |.text.lodKernel:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   LDC R0, c[0x0][0x220] ;
        |        /*0020*/                   HFMA2.MMA R4, -RZ, RZ, 1.6875, 0 ;
        |        /*0030*/                   MOV R5, 0x3f200000 ;
        |        /*0040*/                   ULDC UR4, c[0x0][0x210] ;
        |        /*0050*/                   UMOV UR5, URZ ;
        |        /*0060*/                   TEX.LL RZ, R5, R4, R0, UR4, 0x0, 2D, 0x1 ;
        |        /*0070*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*0080*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0090*/                   STG.E desc[UR4][R2.64], R5 ;
        |        /*00a0*/                   EXIT ;
        |.L_x_0:
        |        /*00b0*/                   BRA `(.L_x_0);
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |""".stripMargin,
    "lodKernelSm100" ->
      """//--------------------- .text.lodKernel           --------------------------
        |	.section	.text.lodKernel,"ax",@progbits
        |	.align	128
        |        .global         lodKernel
        |        .type           lodKernel,@function
        |        .size           lodKernel,(.L_x_1 - lodKernel)
        |        .other          lodKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |lodKernel:
        |.text.lodKernel:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   LDC R0, c[0x0][0x390] ;
        |        /*0020*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0030*/                   HFMA2 R4, -RZ, RZ, 1.6875, 0 ;
        |        /*0040*/                   MOV R5, 0x3f200000 ;
        |        /*0050*/                   UMOV UR5, URZ ;
        |        /*0060*/                   TEX.LL RZ, R5, R4, R0, UR4, 2D, 0x1 ;
        |        /*0070*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0080*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0090*/                   STG.E desc[UR4][R2.64], R5 ;
        |        /*00a0*/                   EXIT ;
        |.L_x_0:
        |        /*00b0*/                   BRA `(.L_x_0);
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |""".stripMargin,
    "lodKernelSm120" ->
      """//--------------------- .text.lodKernel           --------------------------
        |	.section	.text.lodKernel,"ax",@progbits
        |	.align	128
        |        .global         lodKernel
        |        .type           lodKernel,@function
        |        .size           lodKernel,(.L_x_1 - lodKernel)
        |        .other          lodKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |lodKernel:
        |.text.lodKernel:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   LDC R0, c[0x0][0x390] ;
        |        /*0020*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0030*/                   MOV R5, 0x3f200000 ;
        |        /*0040*/                   HFMA2 R4, -RZ, RZ, 1.6875, 0 ;
        |        /*0050*/                   UMOV UR5, URZ ;
        |        /*0060*/                   TEX.LL RZ, R5, R4, R0, UR4, 2D, 0x1 ;
        |        /*0070*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0080*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0090*/                   STG.E desc[UR4][R2.64], R5 ;
        |        /*00a0*/                   EXIT ;
        |.L_x_0:
        |        /*00b0*/                   BRA `(.L_x_0);
        |        /*00c0*/                   NOP;
        |        /*00d0*/                   NOP;
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
        |""".stripMargin,
    "gradKernelSm89" ->
      """//--------------------- .text.gradKernel          --------------------------
        |	.section	.text.gradKernel,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=14"
        |	.align	128
        |        .global         gradKernel
        |        .type           gradKernel,@function
        |        .size           gradKernel,(.L_x_1 - gradKernel)
        |        .other          gradKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gradKernel:
        |.text.gradKernel:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x3f200000 ;
        |        /*0020*/                   MOV R4, 0x3ec00000 ;
        |        /*0030*/                   IMAD.MOV.U32 R9, RZ, RZ, c[0x0][0x178] ;
        |        /*0040*/                   MOV R8, c[0x0][0x170] ;
        |        /*0050*/                   IMAD.MOV.U32 R11, RZ, RZ, c[0x0][0x17c] ;
        |        /*0060*/                   MOV R10, c[0x0][0x174] ;
        |        /*0070*/                   TXD RZ, R5, R4, R8, 0x0, 0x58, 2D, 0x1 ;
        |        /*0080*/                   MOV R2, c[0x0][0x168] ;
        |        /*0090*/                   IMAD.MOV.U32 R3, RZ, RZ, c[0x0][0x16c] ;
        |        /*00a0*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*00b0*/                   STG.E [R2.64], R5 ;
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
        |""".stripMargin,
    "gradKernelSm90" ->
      """//--------------------- .text.gradKernel          --------------------------
        |	.section	.text.gradKernel,"ax",@progbits
        |	.align	128
        |        .global         gradKernel
        |        .type           gradKernel,@function
        |        .size           gradKernel,(.L_x_1 - gradKernel)
        |        .other          gradKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gradKernel:
        |.text.gradKernel:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   LDC R8, c[0x0][0x220] ;
        |        /*0020*/                   HFMA2.MMA R4, -RZ, RZ, 1.6875, 0 ;
        |        /*0030*/                   MOV R5, 0x3f200000 ;
        |        /*0040*/                   ULDC UR4, c[0x0][0x210] ;
        |        /*0050*/                   UMOV UR5, URZ ;
        |        /*0060*/                   LDC R9, c[0x0][0x228] ;
        |        /*0070*/                   LDC R10, c[0x0][0x224] ;
        |        /*0080*/                   LDC R11, c[0x0][0x22c] ;
        |        /*0090*/                   TXD RZ, R5, R4, R8, UR4, 0x0, 2D, 0x1 ;
        |        /*00a0*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*00b0*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*00c0*/                   STG.E desc[UR4][R2.64], R5 ;
        |        /*00d0*/                   EXIT ;
        |.L_x_0:
        |        /*00e0*/                   BRA `(.L_x_0);
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
        |""".stripMargin,
    "gradKernelSm100" ->
      """//--------------------- .text.gradKernel          --------------------------
        |	.section	.text.gradKernel,"ax",@progbits
        |	.align	128
        |        .global         gradKernel
        |        .type           gradKernel,@function
        |        .size           gradKernel,(.L_x_1 - gradKernel)
        |        .other          gradKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gradKernel:
        |.text.gradKernel:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   LDC R8, c[0x0][0x390] ;
        |        /*0020*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0030*/                   HFMA2 R4, -RZ, RZ, 1.6875, 0 ;
        |        /*0040*/                   MOV R5, 0x3f200000 ;
        |        /*0050*/                   UMOV UR5, URZ ;
        |        /*0060*/                   LDC R9, c[0x0][0x398] ;
        |        /*0070*/                   LDC R10, c[0x0][0x394] ;
        |        /*0080*/                   LDC R11, c[0x0][0x39c] ;
        |        /*0090*/                   TXD RZ, R5, R4, R8, UR4, 2D, 0x1 ;
        |        /*00a0*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*00b0*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*00c0*/                   STG.E desc[UR4][R2.64], R5 ;
        |        /*00d0*/                   EXIT ;
        |.L_x_0:
        |        /*00e0*/                   BRA `(.L_x_0);
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
        |""".stripMargin,
    "gradKernelSm120" ->
      """//--------------------- .text.gradKernel          --------------------------
        |	.section	.text.gradKernel,"ax",@progbits
        |	.align	128
        |        .global         gradKernel
        |        .type           gradKernel,@function
        |        .size           gradKernel,(.L_x_1 - gradKernel)
        |        .other          gradKernel,@"STO_CUDA_ENTRY STV_DEFAULT"
        |gradKernel:
        |.text.gradKernel:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   LDC R8, c[0x0][0x390] ;
        |        /*0020*/                   LDCU UR4, c[0x0][0x380] ;
        |        /*0030*/                   MOV R5, 0x3f200000 ;
        |        /*0040*/                   HFMA2 R4, -RZ, RZ, 1.6875, 0 ;
        |        /*0050*/                   UMOV UR5, URZ ;
        |        /*0060*/                   LDC R9, c[0x0][0x398] ;
        |        /*0070*/                   LDC R10, c[0x0][0x394] ;
        |        /*0080*/                   LDC R11, c[0x0][0x39c] ;
        |        /*0090*/                   TXD RZ, R5, R4, R8, UR4, 2D, 0x1 ;
        |        /*00a0*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*00b0*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*00c0*/                   STG.E desc[UR4][R2.64], R5 ;
        |        /*00d0*/                   EXIT ;
        |.L_x_0:
        |        /*00e0*/                   BRA `(.L_x_0);
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
  )
}
