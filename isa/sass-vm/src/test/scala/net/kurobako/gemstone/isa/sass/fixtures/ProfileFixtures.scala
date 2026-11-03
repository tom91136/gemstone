package net.kurobako.gemstone.isa.sass.fixtures

object ProfileFixtures {

  val listings: Map[String, String] = Map(
    "ldStrideSm89" ->
      """//--------------------- .text.ld_stride           --------------------------
        |	.section	.text.ld_stride,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         ld_stride
        |        .type           ld_stride,@function
        |        .size           ld_stride,(.L_x_27 - ld_stride)
        |        .other          ld_stride,@"STO_CUDA_ENTRY STV_DEFAULT"
        |ld_stride:
        |.text.ld_stride:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   MOV R5, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD R2, R4, c[0x0][0x170], RZ ;
        |        /*0050*/                   IMAD.WIDE.U32 R2, R2, R5, c[0x0][0x160] ;
        |        /*0060*/                   LDG.E.CONSTANT R3, [R2.64] ;
        |        /*0070*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x168] ;
        |        /*0080*/                   STG.E [R4.64], R3 ;
        |        /*0090*/                   EXIT ;
        |.L_x_27:
        |""".stripMargin,
    "ldOffsetSm89" ->
      """//--------------------- .text.ld_offset           --------------------------
        |	.section	.text.ld_offset,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         ld_offset
        |        .type           ld_offset,@function
        |        .size           ld_offset,(.L_x_26 - ld_offset)
        |        .other          ld_offset,@"STO_CUDA_ENTRY STV_DEFAULT"
        |ld_offset:
        |.text.ld_offset:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   MOV R5, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IADD3 R2, R4, c[0x0][0x170], RZ ;
        |        /*0050*/                   IMAD.WIDE.U32 R2, R2, R5, c[0x0][0x160] ;
        |        /*0060*/                   LDG.E.CONSTANT R3, [R2.64] ;
        |        /*0070*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x168] ;
        |        /*0080*/                   STG.E [R4.64], R3 ;
        |        /*0090*/                   EXIT ;
        |.L_x_26:
        |""".stripMargin,
    "ldDoubleSm89" ->
      """//--------------------- .text.ld_double           --------------------------
        |	.section	.text.ld_double,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         ld_double
        |        .type           ld_double,@function
        |        .size           ld_double,(.L_x_25 - ld_double)
        |        .other          ld_double,@"STO_CUDA_ENTRY STV_DEFAULT"
        |ld_double:
        |.text.ld_double:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   MOV R5, 0x8 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD R2, R4, c[0x0][0x170], RZ ;
        |        /*0050*/                   IMAD.WIDE.U32 R2, R2, R5, c[0x0][0x160] ;
        |        /*0060*/                   LDG.E.64.CONSTANT R2, [R2.64] ;
        |        /*0070*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x168] ;
        |        /*0080*/                   STG.E.64 [R4.64], R2 ;
        |        /*0090*/                   EXIT ;
        |.L_x_25:
        |""".stripMargin,
    "ldFloat4Sm89" ->
      """//--------------------- .text.ld_float4           --------------------------
        |	.section	.text.ld_float4,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         ld_float4
        |        .type           ld_float4,@function
        |        .size           ld_float4,(.L_x_24 - ld_float4)
        |        .other          ld_float4,@"STO_CUDA_ENTRY STV_DEFAULT"
        |ld_float4:
        |.text.ld_float4:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0020*/                   MOV R3, 0x10 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R4, R2, R3, c[0x0][0x160] ;
        |        /*0050*/                   LDG.E.128.CONSTANT R4, [R4.64] ;
        |        /*0060*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x168] ;
        |        /*0070*/                   STG.E.128 [R2.64], R4 ;
        |        /*0080*/                   EXIT ;
        |.L_x_24:
        |""".stripMargin,
    "sharedStrideSm89" ->
      """//--------------------- .text.shared_stride       --------------------------
        |	.section	.text.shared_stride,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         shared_stride
        |        .type           shared_stride,@function
        |        .size           shared_stride,(.L_x_23 - shared_stride)
        |        .other          shared_stride,@"STO_CUDA_ENTRY STV_DEFAULT"
        |shared_stride:
        |.text.shared_stride:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0020*/                   MOV R3, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   I2FP.F32.U32 R0, R2 ;
        |        /*0050*/                   IMAD R5, R2.reuse, c[0x0][0x168], RZ ;
        |        /*0060*/                   IMAD.WIDE.U32 R2, R2, R3, c[0x0][0x160] ;
        |        /*0070*/                   STS [R5.X4], R0 ;
        |        /*0080*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0090*/                   LDS R7, [R5.X4] ;
        |        /*00a0*/                   STG.E [R2.64], R7 ;
        |        /*00b0*/                   EXIT ;
        |.L_x_23:
        |""".stripMargin,
    "sharedBroadcastSm89" ->
      """//--------------------- .text.shared_broadcast    --------------------------
        |	.section	.text.shared_broadcast,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         shared_broadcast
        |        .type           shared_broadcast,@function
        |        .size           shared_broadcast,(.L_x_22 - shared_broadcast)
        |        .other          shared_broadcast,@"STO_CUDA_ENTRY STV_DEFAULT"
        |shared_broadcast:
        |.text.shared_broadcast:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0030*/                   BSSY B0, `(.L_x_12) ;
        |        /*0040*/                   ISETP.NE.AND P0, PT, R0, RZ, PT ;
        |        /*0050*/               @P0 BRA `(.L_x_13) ;
        |        /*0060*/                   IMAD.MOV.U32 R2, RZ, RZ, c[0x0][0x160] ;
        |        /*0070*/                   MOV R3, c[0x0][0x164] ;
        |        /*0080*/                   LDG.E.CONSTANT R2, [R2.64] ;
        |        /*0090*/                   STS [RZ], R2 ;
        |.L_x_13:
        |        /*00a0*/                   BSYNC B0 ;
        |.L_x_12:
        |        /*00b0*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*00c0*/                   MOV R3, 0x4 ;
        |        /*00d0*/                   IMAD.WIDE.U32 R2, R0, R3, c[0x0][0x168] ;
        |        /*00e0*/                   LDS R5, [RZ] ;
        |        /*00f0*/                   STG.E [R2.64], R5 ;
        |        /*0100*/                   EXIT ;
        |.L_x_22:
        |""".stripMargin,
    "sharedDoubleSm89" ->
      """//--------------------- .text.shared_double       --------------------------
        |	.section	.text.shared_double,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=12"
        |	.align	128
        |        .global         shared_double
        |        .type           shared_double,@function
        |        .size           shared_double,(.L_x_21 - shared_double)
        |        .other          shared_double,@"STO_CUDA_ENTRY STV_DEFAULT"
        |shared_double:
        |.text.shared_double:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   MOV R7, 0x8 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   I2F.F64.U32 R2, R0 ;
        |        /*0050*/                   IMAD R9, R0.reuse, c[0x0][0x168], RZ ;
        |        /*0060*/                   IMAD.WIDE.U32 R6, R0, R7, c[0x0][0x160] ;
        |        /*0070*/                   STS.64 [R9.X8], R2 ;
        |        /*0080*/                   BAR.SYNC.DEFER_BLOCKING 0x0 ;
        |        /*0090*/                   LDS.64 R4, [R9.X8] ;
        |        /*00a0*/                   STG.E.64 [R6.64], R4 ;
        |        /*00b0*/                   EXIT ;
        |.L_x_21:
        |""".stripMargin,
    "divergentSm89" ->
      """//--------------------- .text.divergent           --------------------------
        |	.section	.text.divergent,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=13"
        |	.align	128
        |        .global         divergent
        |        .type           divergent,@function
        |        .size           divergent,(.L_x_20 - divergent)
        |        .other          divergent,@"STO_CUDA_ENTRY STV_DEFAULT"
        |divergent:
        |.text.divergent:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R6, SR_TID.X ;
        |        /*0020*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x4 ;
        |        /*0030*/                   ULDC.64 UR6, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R6, R3, c[0x0][0x160] ;
        |        /*0050*/                   LDG.E.CONSTANT R5, [R2.64] ;
        |        /*0060*/                   LOP3.LUT P0, RZ, R6, c[0x0][0x170], RZ, 0xc0, !PT ;
        |        /*0070*/                   BSSY B0, `(.L_x_0) ;
        |        /*0080*/              @!P0 BRA `(.L_x_1) ;
        |        /*0090*/                   IMAD.MOV.U32 R2, RZ, RZ, c[0x0][0x160] ;
        |        /*00a0*/                   IMAD.MOV.U32 R3, RZ, RZ, c[0x0][0x164] ;
        |        /*00b0*/                   LDG.E.CONSTANT R2, [R2.64+0x80] ;
        |        /*00c0*/                   ISETP.GE.AND P0, PT, R2, -0x3, PT ;
        |        /*00d0*/              @!P0 BRA `(.L_x_2) ;
        |        /*00e0*/                   IADD3 R2, R2, 0x3, RZ ;
        |        /*00f0*/                   IMAD.MOV.U32 R4, RZ, RZ, RZ ;
        |        /*0100*/                   IMNMX R2, RZ, R2, !PT ;
        |        /*0110*/                   ISETP.GE.U32.AND P0, PT, R2.reuse, 0x3, PT ;
        |        /*0120*/                   IADD3 R0, R2, 0x1, RZ ;
        |        /*0130*/                   LOP3.LUT R3, R0, 0x3, RZ, 0xc0, !PT ;
        |        /*0140*/              @!P0 BRA `(.L_x_3) ;
        |        /*0150*/                   IMAD.IADD R2, R2, 0x1, -R3 ;
        |        /*0160*/                   UMOV UR4, 0xffffff72 ;
        |        /*0170*/                   IMAD.MOV.U32 R4, RZ, RZ, RZ ;
        |        /*0180*/                   IMAD.MOV.U32 R0, RZ, RZ, 0x12 ;
        |        /*0190*/                   ISETP.GT.AND P0, PT, R2, -0x1, PT ;
        |        /*01a0*/              @!P0 BRA `(.L_x_4) ;
        |        /*01b0*/                   IADD3 R7, R2, 0x1, RZ ;
        |        /*01c0*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x80, 0x0 ;
        |        /*01d0*/                   ISETP.GT.AND P1, PT, R7, 0xc, PT ;
        |        /*01e0*/              @!P1 BRA `(.L_x_5) ;
        |        /*01f0*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x8, 0x0 ;
        |.L_x_6:
        |        /*0200*/                   IADD3 R8, R0, 0xa0, RZ ;
        |        /*0210*/                   IMAD R5, R5, 0x51, R0 ;
        |        /*0220*/                   IADD3 R2, R2, -0x10, RZ ;
        |        /*0230*/                   UIADD3 UR4, UR4, 0x280, URZ ;
        |        /*0240*/                   IADD3 R10, R0, 0x140, RZ ;
        |        /*0250*/                   IMAD R5, R5, 0x51, R8 ;
        |        /*0260*/                   ISETP.GT.AND P1, PT, R2, 0xb, PT ;
        |        /*0270*/                   IADD3 R4, R4, 0x10, RZ ;
        |        /*0280*/                   IMAD R5, R5, 0x51, R10 ;
        |        /*0290*/                   IMAD R7, R5, 0x51, RZ ;
        |        /*02a0*/                   IADD3 R5, R7, 0x1e0, R0 ;
        |        /*02b0*/                   IADD3 R0, R0, 0x280, RZ ;
        |        /*02c0*/               @P1 BRA `(.L_x_6) ;
        |.L_x_5:
        |        /*02d0*/                   IADD3 R8, R2, 0x1, RZ ;
        |        /*02e0*/                   ISETP.GT.AND P1, PT, R8, 0x4, PT ;
        |        /*02f0*/              @!P1 BRA `(.L_x_7) ;
        |        /*0300*/                   IMAD R5, R5, 0x51, R0 ;
        |        /*0310*/                   PLOP3.LUT P0, PT, PT, PT, PT, 0x8, 0x0 ;
        |        /*0320*/                   UIADD3 UR4, UR4, 0x140, URZ ;
        |        /*0330*/                   IADD3 R4, R4, 0x8, RZ ;
        |        /*0340*/                   IMAD R7, R5, 0x51, RZ ;
        |        /*0350*/                   IADD3 R2, R2, -0x8, RZ ;
        |        /*0360*/                   IADD3 R5, R7, 0xa0, R0 ;
        |        /*0370*/                   IADD3 R0, R0, 0x140, RZ ;
        |.L_x_7:
        |        /*0380*/                   ISETP.NE.OR P0, PT, R2, -0x1, P0 ;
        |        /*0390*/              @!P0 BRA `(.L_x_8) ;
        |.L_x_4:
        |        /*03a0*/                   IADD3 R2, R2, -0x4, RZ ;
        |        /*03b0*/                   IMAD R7, R5, 0x51, RZ ;
        |        /*03c0*/                   UIADD3 UR4, UR4, 0xa0, URZ ;
        |        /*03d0*/                   IADD3 R4, R4, 0x4, RZ ;
        |        /*03e0*/                   ISETP.NE.AND P0, PT, R2, -0x1, PT ;
        |        /*03f0*/                   IMAD.IADD R5, R7, 0x1, R0 ;
        |        /*0400*/                   IADD3 R0, R0, 0xa0, RZ ;
        |        /*0410*/               @P0 BRA `(.L_x_4) ;
        |.L_x_8:
        |        /*0420*/                   IADD3 R5, R7, UR4, RZ ;
        |.L_x_3:
        |        /*0430*/                   ISETP.NE.AND P0, PT, R3, RZ, PT ;
        |        /*0440*/              @!P0 BRA `(.L_x_2) ;
        |.L_x_9:
        |        /*0450*/                   IADD3 R3, R3, -0x1, RZ ;
        |        /*0460*/                   IMAD.MOV.U32 R7, RZ, RZ, R5 ;
        |        /*0470*/                   IMAD R5, R5, 0x3, R4 ;
        |        /*0480*/                   ISETP.NE.AND P0, PT, R3, RZ, PT ;
        |        /*0490*/                   IADD3 R4, R4, 0x1, RZ ;
        |        /*04a0*/               @P0 BRA `(.L_x_9) ;
        |        /*04b0*/                   IMAD R4, R7, 0x3, R4 ;
        |        /*04c0*/                   IADD3 R5, R4, -0x1, RZ ;
        |        /*04d0*/                   BRA `(.L_x_2) ;
        |.L_x_1:
        |        /*04e0*/                   LEA.HI R5, R5, R5, RZ, 0x1 ;
        |        /*04f0*/                   SHF.R.S32.HI R5, RZ, 0x1, R5 ;
        |.L_x_2:
        |        /*0500*/                   BSYNC B0 ;
        |.L_x_0:
        |        /*0510*/                   LEA R2, P0, R6, c[0x0][0x168], 0x2 ;
        |        /*0520*/                   LEA.HI.X R3, R6, c[0x0][0x16c], RZ, 0x2, P0 ;
        |        /*0530*/                   STG.E [R2.64], R5 ;
        |        /*0540*/                   EXIT ;
        |.L_x_20:
        |""".stripMargin
  )
}
