package net.kurobako.gemstone.isa.sass.fixtures

/** `nvdisasm -c` listings of the matrix probes in [[net.kurobako.gemstone.isa.nvidia.fixtures.MatrixProbes]]. */
object MatrixFixtures {
  val listings: Map[String, String] = Map(
    "immaS8SatSm89" ->
      """//--------------------- .text.immaS8SatSm89       --------------------------
        |	.section	.text.immaS8SatSm89,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=18"
        |	.align	128
        |        .global         immaS8SatSm89
        |        .type           immaS8SatSm89,@function
        |        .size           immaS8SatSm89,(.L_x_1 - immaS8SatSm89)
        |        .other          immaS8SatSm89,@"STO_CUDA_ENTRY STV_DEFAULT"
        |immaS8SatSm89:
        |.text.immaS8SatSm89:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   MOV R3, 0x28 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R0, R3, c[0x0][0x168] ;
        |        /*0050*/                   LDG.E R8, [R2.64+0x10] ;
        |        /*0060*/                   LDG.E R9, [R2.64+0x14] ;
        |        /*0070*/                   LDG.E R4, [R2.64] ;
        |        /*0080*/                   LDG.E R5, [R2.64+0x4] ;
        |        /*0090*/                   LDG.E R6, [R2.64+0x8] ;
        |        /*00a0*/                   LDG.E R7, [R2.64+0xc] ;
        |        /*00b0*/                   LDG.E R12, [R2.64+0x18] ;
        |        /*00c0*/                   LDG.E R13, [R2.64+0x1c] ;
        |        /*00d0*/                   LDG.E R14, [R2.64+0x20] ;
        |        /*00e0*/                   LDG.E R15, [R2.64+0x24] ;
        |        /*00f0*/                   IMMA.16832.S8.S8.SAT R12, R4.ROW, R8.COL, R12 ;
        |        /*0100*/                   MOV R5, 0x10 ;
        |        /*0110*/                   IMAD.WIDE.U32 R4, R0, R5, c[0x0][0x160] ;
        |        /*0120*/                   STG.E [R4.64], R12 ;
        |        /*0130*/                   STG.E [R4.64+0x4], R13 ;
        |        /*0140*/                   STG.E [R4.64+0x8], R14 ;
        |        /*0150*/                   STG.E [R4.64+0xc], R15 ;
        |        /*0160*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "immaU4Sm89" ->
      """//--------------------- .text.immaU4Sm89          --------------------------
        |	.section	.text.immaU4Sm89,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         immaU4Sm89
        |        .type           immaU4Sm89,@function
        |        .size           immaU4Sm89,(.L_x_1 - immaU4Sm89)
        |        .other          immaU4Sm89,@"STO_CUDA_ENTRY STV_DEFAULT"
        |immaU4Sm89:
        |.text.immaU4Sm89:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   MOV R3, 0x10 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R4, R3, c[0x0][0x168] ;
        |        /*0050*/                   LDG.E R0, [R2.64] ;
        |        /*0060*/                   LDG.E R5, [R2.64+0x4] ;
        |        /*0070*/                   LDG.E R6, [R2.64+0x8] ;
        |        /*0080*/                   LDG.E R7, [R2.64+0xc] ;
        |        /*0090*/                   IMMA.8832.U4.U4 R6, R0.ROW, R5.COL, R6 ;
        |        /*00a0*/                   MOV R5, 0x8 ;
        |        /*00b0*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x160] ;
        |        /*00c0*/                   STG.E [R4.64], R6 ;
        |        /*00d0*/                   STG.E [R4.64+0x4], R7 ;
        |        /*00e0*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "bmmaAndSm89" ->
      """//--------------------- .text.bmmaAndSm89         --------------------------
        |	.section	.text.bmmaAndSm89,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=18"
        |	.align	128
        |        .global         bmmaAndSm89
        |        .type           bmmaAndSm89,@function
        |        .size           bmmaAndSm89,(.L_x_1 - bmmaAndSm89)
        |        .other          bmmaAndSm89,@"STO_CUDA_ENTRY STV_DEFAULT"
        |bmmaAndSm89:
        |.text.bmmaAndSm89:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   MOV R3, 0x28 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R0, R3, c[0x0][0x168] ;
        |        /*0050*/                   LDG.E R8, [R2.64+0x10] ;
        |        /*0060*/                   LDG.E R9, [R2.64+0x14] ;
        |        /*0070*/                   LDG.E R4, [R2.64] ;
        |        /*0080*/                   LDG.E R5, [R2.64+0x4] ;
        |        /*0090*/                   LDG.E R6, [R2.64+0x8] ;
        |        /*00a0*/                   LDG.E R7, [R2.64+0xc] ;
        |        /*00b0*/                   LDG.E R12, [R2.64+0x18] ;
        |        /*00c0*/                   LDG.E R13, [R2.64+0x1c] ;
        |        /*00d0*/                   LDG.E R14, [R2.64+0x20] ;
        |        /*00e0*/                   LDG.E R15, [R2.64+0x24] ;
        |        /*00f0*/                   BMMA.168256.AND.POPC R12, R4.ROW, R8.COL, R12 ;
        |        /*0100*/                   MOV R5, 0x10 ;
        |        /*0110*/                   IMAD.WIDE.U32 R4, R0, R5, c[0x0][0x160] ;
        |        /*0120*/                   STG.E [R4.64], R12 ;
        |        /*0130*/                   STG.E [R4.64+0x4], R13 ;
        |        /*0140*/                   STG.E [R4.64+0x8], R14 ;
        |        /*0150*/                   STG.E [R4.64+0xc], R15 ;
        |        /*0160*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "bmmaXorSm89" ->
      """//--------------------- .text.bmmaXorSm89         --------------------------
        |	.section	.text.bmmaXorSm89,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         bmmaXorSm89
        |        .type           bmmaXorSm89,@function
        |        .size           bmmaXorSm89,(.L_x_1 - bmmaXorSm89)
        |        .other          bmmaXorSm89,@"STO_CUDA_ENTRY STV_DEFAULT"
        |bmmaXorSm89:
        |.text.bmmaXorSm89:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   MOV R3, 0x10 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R4, R3, c[0x0][0x168] ;
        |        /*0050*/                   LDG.E R0, [R2.64] ;
        |        /*0060*/                   LDG.E R5, [R2.64+0x4] ;
        |        /*0070*/                   LDG.E R6, [R2.64+0x8] ;
        |        /*0080*/                   LDG.E R7, [R2.64+0xc] ;
        |        /*0090*/                   BMMA.88128.XOR.POPC R6, R0.ROW, R5.COL, R6 ;
        |        /*00a0*/                   MOV R5, 0x8 ;
        |        /*00b0*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x160] ;
        |        /*00c0*/                   STG.E [R4.64], R6 ;
        |        /*00d0*/                   STG.E [R4.64+0x4], R7 ;
        |        /*00e0*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "qmmaE4m3E5m2Sm89" ->
      """//--------------------- .text.qmmaE4m3E5m2Sm89    --------------------------
        |	.section	.text.qmmaE4m3E5m2Sm89,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=18"
        |	.align	128
        |        .global         qmmaE4m3E5m2Sm89
        |        .type           qmmaE4m3E5m2Sm89,@function
        |        .size           qmmaE4m3E5m2Sm89,(.L_x_1 - qmmaE4m3E5m2Sm89)
        |        .other          qmmaE4m3E5m2Sm89,@"STO_CUDA_ENTRY STV_DEFAULT"
        |qmmaE4m3E5m2Sm89:
        |.text.qmmaE4m3E5m2Sm89:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   MOV R3, 0x28 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R0, R3, c[0x0][0x168] ;
        |        /*0050*/                   LDG.E R8, [R2.64+0x10] ;
        |        /*0060*/                   LDG.E R9, [R2.64+0x14] ;
        |        /*0070*/                   LDG.E R4, [R2.64] ;
        |        /*0080*/                   LDG.E R5, [R2.64+0x4] ;
        |        /*0090*/                   LDG.E R6, [R2.64+0x8] ;
        |        /*00a0*/                   LDG.E R7, [R2.64+0xc] ;
        |        /*00b0*/                   LDG.E R12, [R2.64+0x18] ;
        |        /*00c0*/                   LDG.E R13, [R2.64+0x1c] ;
        |        /*00d0*/                   LDG.E R14, [R2.64+0x20] ;
        |        /*00e0*/                   LDG.E R15, [R2.64+0x24] ;
        |        /*00f0*/                   QMMA.16832.F32.E4M3.E5M2 R12, R4.ROW, R8.COL, R12 ;
        |        /*0100*/                   MOV R5, 0x10 ;
        |        /*0110*/                   IMAD.WIDE.U32 R4, R0, R5, c[0x0][0x160] ;
        |        /*0120*/                   STG.E [R4.64], R12 ;
        |        /*0130*/                   STG.E [R4.64+0x4], R13 ;
        |        /*0140*/                   STG.E [R4.64+0x8], R14 ;
        |        /*0150*/                   STG.E [R4.64+0xc], R15 ;
        |        /*0160*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "qmmaE5m2F16Sm89" ->
      """//--------------------- .text.qmmaE5m2F16Sm89     --------------------------
        |	.section	.text.qmmaE5m2F16Sm89,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=11"
        |	.align	128
        |        .global         qmmaE5m2F16Sm89
        |        .type           qmmaE5m2F16Sm89,@function
        |        .size           qmmaE5m2F16Sm89,(.L_x_1 - qmmaE5m2F16Sm89)
        |        .other          qmmaE5m2F16Sm89,@"STO_CUDA_ENTRY STV_DEFAULT"
        |qmmaE5m2F16Sm89:
        |.text.qmmaE5m2F16Sm89:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R8, SR_TID.X ;
        |        /*0020*/                   MOV R3, 0x14 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R8, R3, c[0x0][0x168] ;
        |        /*0050*/                   LDG.E R0, [R2.64+0x8] ;
        |        /*0060*/                   LDG.E R4, [R2.64] ;
        |        /*0070*/                   LDG.E R5, [R2.64+0x4] ;
        |        /*0080*/                   LDG.E R6, [R2.64+0xc] ;
        |        /*0090*/                   LDG.E R7, [R2.64+0x10] ;
        |        /*00a0*/                   QMMA.16816.F16.E5M2.E5M2 R6, R4.ROW, R0.COL, R6 ;
        |        /*00b0*/                   MOV R5, 0x8 ;
        |        /*00c0*/                   IMAD.WIDE.U32 R4, R8, R5, c[0x0][0x160] ;
        |        /*00d0*/                   STG.E [R4.64], R6 ;
        |        /*00e0*/                   STG.E [R4.64+0x4], R7 ;
        |        /*00f0*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hmmaBf16Sm89" ->
      """//--------------------- .text.hmmaBf16Sm89        --------------------------
        |	.section	.text.hmmaBf16Sm89,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=18"
        |	.align	128
        |        .global         hmmaBf16Sm89
        |        .type           hmmaBf16Sm89,@function
        |        .size           hmmaBf16Sm89,(.L_x_1 - hmmaBf16Sm89)
        |        .other          hmmaBf16Sm89,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmmaBf16Sm89:
        |.text.hmmaBf16Sm89:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   MOV R3, 0x28 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R0, R3, c[0x0][0x168] ;
        |        /*0050*/                   LDG.E R8, [R2.64+0x10] ;
        |        /*0060*/                   LDG.E R9, [R2.64+0x14] ;
        |        /*0070*/                   LDG.E R4, [R2.64] ;
        |        /*0080*/                   LDG.E R5, [R2.64+0x4] ;
        |        /*0090*/                   LDG.E R6, [R2.64+0x8] ;
        |        /*00a0*/                   LDG.E R7, [R2.64+0xc] ;
        |        /*00b0*/                   LDG.E R12, [R2.64+0x18] ;
        |        /*00c0*/                   LDG.E R13, [R2.64+0x1c] ;
        |        /*00d0*/                   LDG.E R14, [R2.64+0x20] ;
        |        /*00e0*/                   LDG.E R15, [R2.64+0x24] ;
        |        /*00f0*/                   HMMA.16816.F32.BF16 R12, R4, R8, R12 ;
        |        /*0100*/                   MOV R5, 0x10 ;
        |        /*0110*/                   IMAD.WIDE.U32 R4, R0, R5, c[0x0][0x160] ;
        |        /*0120*/                   STG.E [R4.64], R12 ;
        |        /*0130*/                   STG.E [R4.64+0x4], R13 ;
        |        /*0140*/                   STG.E [R4.64+0x8], R14 ;
        |        /*0150*/                   STG.E [R4.64+0xc], R15 ;
        |        /*0160*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hmmaTf32Sm89" ->
      """//--------------------- .text.hmmaTf32Sm89        --------------------------
        |	.section	.text.hmmaTf32Sm89,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=18"
        |	.align	128
        |        .global         hmmaTf32Sm89
        |        .type           hmmaTf32Sm89,@function
        |        .size           hmmaTf32Sm89,(.L_x_1 - hmmaTf32Sm89)
        |        .other          hmmaTf32Sm89,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmmaTf32Sm89:
        |.text.hmmaTf32Sm89:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   MOV R3, 0x28 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R0, R3, c[0x0][0x168] ;
        |        /*0050*/                   LDG.E R8, [R2.64+0x10] ;
        |        /*0060*/                   LDG.E R9, [R2.64+0x14] ;
        |        /*0070*/                   LDG.E R4, [R2.64] ;
        |        /*0080*/                   LDG.E R5, [R2.64+0x4] ;
        |        /*0090*/                   LDG.E R6, [R2.64+0x8] ;
        |        /*00a0*/                   LDG.E R7, [R2.64+0xc] ;
        |        /*00b0*/                   LDG.E R12, [R2.64+0x18] ;
        |        /*00c0*/                   LDG.E R13, [R2.64+0x1c] ;
        |        /*00d0*/                   LDG.E R14, [R2.64+0x20] ;
        |        /*00e0*/                   LDG.E R15, [R2.64+0x24] ;
        |        /*00f0*/                   HMMA.1688.F32.TF32 R12, R4, R8, R12 ;
        |        /*0100*/                   MOV R5, 0x10 ;
        |        /*0110*/                   IMAD.WIDE.U32 R4, R0, R5, c[0x0][0x160] ;
        |        /*0120*/                   STG.E [R4.64], R12 ;
        |        /*0130*/                   STG.E [R4.64+0x4], R13 ;
        |        /*0140*/                   STG.E [R4.64+0x8], R14 ;
        |        /*0150*/                   STG.E [R4.64+0xc], R15 ;
        |        /*0160*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hmmaSparseF16Sm89" ->
      """//--------------------- .text.hmmaSparseF16Sm89   --------------------------
        |	.section	.text.hmmaSparseF16Sm89,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=17"
        |	.align	128
        |        .global         hmmaSparseF16Sm89
        |        .type           hmmaSparseF16Sm89,@function
        |        .size           hmmaSparseF16Sm89,(.L_x_1 - hmmaSparseF16Sm89)
        |        .other          hmmaSparseF16Sm89,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmmaSparseF16Sm89:
        |.text.hmmaSparseF16Sm89:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   MOV R3, 0x28 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R0, R3, c[0x0][0x168] ;
        |        /*0050*/                   LDG.E R12, [R2.64+0x20] ;
        |        /*0060*/                   LDG.E R13, [R2.64+0x24] ;
        |        /*0070*/                   LDG.E R4, [R2.64] ;
        |        /*0080*/                   LDG.E R5, [R2.64+0x4] ;
        |        /*0090*/                   LDG.E R6, [R2.64+0x8] ;
        |        /*00a0*/                   LDG.E R7, [R2.64+0xc] ;
        |        /*00b0*/                   LDG.E R8, [R2.64+0x10] ;
        |        /*00c0*/                   LDG.E R9, [R2.64+0x14] ;
        |        /*00d0*/                   LDG.E R10, [R2.64+0x18] ;
        |        /*00e0*/                   LDG.E R11, [R2.64+0x1c] ;
        |        /*00f0*/                   MOV R14, 0xe9c84d8e ;
        |        /*0100*/                   HMMA.SP.16832.F16 R6, R4, R8, R12, R14, 0x1 ;
        |        /*0110*/                   MOV R5, 0x8 ;
        |        /*0120*/                   IMAD.WIDE.U32 R4, R0, R5, c[0x0][0x160] ;
        |        /*0130*/                   STG.E [R4.64], R6 ;
        |        /*0140*/                   STG.E [R4.64+0x4], R7 ;
        |        /*0150*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "immaSparseS8Sm89" ->
      """//--------------------- .text.immaSparseS8Sm89    --------------------------
        |	.section	.text.immaSparseS8Sm89,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=15"
        |	.align	128
        |        .global         immaSparseS8Sm89
        |        .type           immaSparseS8Sm89,@function
        |        .size           immaSparseS8Sm89,(.L_x_1 - immaSparseS8Sm89)
        |        .other          immaSparseS8Sm89,@"STO_CUDA_ENTRY STV_DEFAULT"
        |immaSparseS8Sm89:
        |.text.immaSparseS8Sm89:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   MOV R3, 0x20 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R0, R3, c[0x0][0x168] ;
        |        /*0050*/                   LDG.E R4, [R2.64] ;
        |        /*0060*/                   LDG.E R5, [R2.64+0x4] ;
        |        /*0070*/                   LDG.E R6, [R2.64+0x8] ;
        |        /*0080*/                   LDG.E R7, [R2.64+0xc] ;
        |        /*0090*/                   LDG.E R8, [R2.64+0x10] ;
        |        /*00a0*/                   LDG.E R9, [R2.64+0x14] ;
        |        /*00b0*/                   LDG.E R10, [R2.64+0x18] ;
        |        /*00c0*/                   LDG.E R11, [R2.64+0x1c] ;
        |        /*00d0*/                   MOV R12, 0xe9c84d8e ;
        |        /*00e0*/                   IMMA.SP.16832.S8.S8.SAT R8, R4.ROW, R6.COL, R8, R12, 0x0 ;
        |        /*00f0*/                   MOV R5, 0x10 ;
        |        /*0100*/                   IMAD.WIDE.U32 R4, R0, R5, c[0x0][0x160] ;
        |        /*0110*/                   STG.E [R4.64], R8 ;
        |        /*0120*/                   STG.E [R4.64+0x4], R9 ;
        |        /*0130*/                   STG.E [R4.64+0x8], R10 ;
        |        /*0140*/                   STG.E [R4.64+0xc], R11 ;
        |        /*0150*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "qmmaSparseE4m3Sm89" ->
      """//--------------------- .text.qmmaSparseE4m3Sm89  --------------------------
        |	.section	.text.qmmaSparseE4m3Sm89,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=19"
        |	.align	128
        |        .global         qmmaSparseE4m3Sm89
        |        .type           qmmaSparseE4m3Sm89,@function
        |        .size           qmmaSparseE4m3Sm89,(.L_x_1 - qmmaSparseE4m3Sm89)
        |        .other          qmmaSparseE4m3Sm89,@"STO_CUDA_ENTRY STV_DEFAULT"
        |qmmaSparseE4m3Sm89:
        |.text.qmmaSparseE4m3Sm89:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   MOV R3, 0x30 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R0, R3, c[0x0][0x168] ;
        |        /*0050*/                   LDG.E R4, [R2.64] ;
        |        /*0060*/                   LDG.E R5, [R2.64+0x4] ;
        |        /*0070*/                   LDG.E R6, [R2.64+0x8] ;
        |        /*0080*/                   LDG.E R7, [R2.64+0xc] ;
        |        /*0090*/                   LDG.E R8, [R2.64+0x10] ;
        |        /*00a0*/                   LDG.E R9, [R2.64+0x14] ;
        |        /*00b0*/                   LDG.E R10, [R2.64+0x18] ;
        |        /*00c0*/                   LDG.E R11, [R2.64+0x1c] ;
        |        /*00d0*/                   LDG.E R12, [R2.64+0x20] ;
        |        /*00e0*/                   LDG.E R13, [R2.64+0x24] ;
        |        /*00f0*/                   LDG.E R14, [R2.64+0x28] ;
        |        /*0100*/                   LDG.E R15, [R2.64+0x2c] ;
        |        /*0110*/                   MOV R16, 0xe9c84d8e ;
        |        /*0120*/                   QMMA.SP.16864.F32.E4M3.E4M3 R8, R4.ROW, R8.COL, R12, R16, 0x0 ;
        |        /*0130*/                   MOV R5, 0x10 ;
        |        /*0140*/                   IMAD.WIDE.U32 R4, R0, R5, c[0x0][0x160] ;
        |        /*0150*/                   STG.E [R4.64], R8 ;
        |        /*0160*/                   STG.E [R4.64+0x4], R9 ;
        |        /*0170*/                   STG.E [R4.64+0x8], R10 ;
        |        /*0180*/                   STG.E [R4.64+0xc], R11 ;
        |        /*0190*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "hmmaSparseTf32Sm89" ->
      """//--------------------- .text.hmmaSparseTf32Sm89  --------------------------
        |	.section	.text.hmmaSparseTf32Sm89,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=19"
        |	.align	128
        |        .global         hmmaSparseTf32Sm89
        |        .type           hmmaSparseTf32Sm89,@function
        |        .size           hmmaSparseTf32Sm89,(.L_x_1 - hmmaSparseTf32Sm89)
        |        .other          hmmaSparseTf32Sm89,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmmaSparseTf32Sm89:
        |.text.hmmaSparseTf32Sm89:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_TID.X ;
        |        /*0020*/                   MOV R3, 0x30 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R0, R3, c[0x0][0x168] ;
        |        /*0050*/                   LDG.E R4, [R2.64] ;
        |        /*0060*/                   LDG.E R5, [R2.64+0x4] ;
        |        /*0070*/                   LDG.E R6, [R2.64+0x8] ;
        |        /*0080*/                   LDG.E R7, [R2.64+0xc] ;
        |        /*0090*/                   LDG.E R8, [R2.64+0x10] ;
        |        /*00a0*/                   LDG.E R9, [R2.64+0x14] ;
        |        /*00b0*/                   LDG.E R10, [R2.64+0x18] ;
        |        /*00c0*/                   LDG.E R11, [R2.64+0x1c] ;
        |        /*00d0*/                   LDG.E R12, [R2.64+0x20] ;
        |        /*00e0*/                   LDG.E R13, [R2.64+0x24] ;
        |        /*00f0*/                   LDG.E R14, [R2.64+0x28] ;
        |        /*0100*/                   LDG.E R15, [R2.64+0x2c] ;
        |        /*0110*/                   MOV R16, 0xe4e44e4e ;
        |        /*0120*/                   HMMA.SP.16816.F32.TF32 R8, R4, R8, R12, R16, 0x1 ;
        |        /*0130*/                   MOV R5, 0x10 ;
        |        /*0140*/                   IMAD.WIDE.U32 R4, R0, R5, c[0x0][0x160] ;
        |        /*0150*/                   STG.E [R4.64], R8 ;
        |        /*0160*/                   STG.E [R4.64+0x4], R9 ;
        |        /*0170*/                   STG.E [R4.64+0x8], R10 ;
        |        /*0180*/                   STG.E [R4.64+0xc], R11 ;
        |        /*0190*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "movmSm89" ->
      """//--------------------- .text.movmSm89            --------------------------
        |	.section	.text.movmSm89,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=10"
        |	.align	128
        |        .global         movmSm89
        |        .type           movmSm89,@function
        |        .size           movmSm89,(.L_x_1 - movmSm89)
        |        .other          movmSm89,@"STO_CUDA_ENTRY STV_DEFAULT"
        |movmSm89:
        |.text.movmSm89:
        |        /*0000*/                   MOV R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R4, SR_TID.X ;
        |        /*0020*/                   MOV R5, 0x4 ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R4, R5, c[0x0][0x168] ;
        |        /*0050*/                   LDG.E R2, [R2.64] ;
        |        /*0060*/                   IMAD.WIDE.U32 R4, R4, R5, c[0x0][0x160] ;
        |        /*0070*/                   MOVM.16.MT88 R7, R2 ;
        |        /*0080*/                   STG.E [R4.64], R7 ;
        |        /*0090*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin,
    "dmma16816Sm90" ->
      """//--------------------- .text.dmma16816Sm90       --------------------------
        |	.section	.text.dmma16816Sm90,"ax",@progbits
        |	.align	128
        |        .global         dmma16816Sm90
        |        .type           dmma16816Sm90,@function
        |        .size           dmma16816Sm90,(.L_x_1 - dmma16816Sm90)
        |        .other          dmma16816Sm90,@"STO_CUDA_ENTRY STV_DEFAULT"
        |dmma16816Sm90:
        |.text.dmma16816Sm90:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_LANEID ;
        |        /*0020*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R0, 0x80, R2 ;
        |        /*0050*/                   LDG.E R16, desc[UR4][R2.64] ;
        |        /*0060*/                   LDG.E R17, desc[UR4][R2.64+0x4] ;
        |        /*0070*/                   LDG.E R18, desc[UR4][R2.64+0x8] ;
        |        /*0080*/                   LDG.E R19, desc[UR4][R2.64+0xc] ;
        |        /*0090*/                   LDG.E R20, desc[UR4][R2.64+0x10] ;
        |        /*00a0*/                   LDG.E R21, desc[UR4][R2.64+0x14] ;
        |        /*00b0*/                   LDG.E R22, desc[UR4][R2.64+0x18] ;
        |        /*00c0*/                   LDG.E R23, desc[UR4][R2.64+0x1c] ;
        |        /*00d0*/                   LDG.E R32, desc[UR4][R2.64+0x40] ;
        |        /*00e0*/                   LDG.E R33, desc[UR4][R2.64+0x44] ;
        |        /*00f0*/                   LDG.E R34, desc[UR4][R2.64+0x48] ;
        |        /*0100*/                   LDG.E R35, desc[UR4][R2.64+0x4c] ;
        |        /*0110*/                   LDG.E R36, desc[UR4][R2.64+0x50] ;
        |        /*0120*/                   LDG.E R37, desc[UR4][R2.64+0x54] ;
        |        /*0130*/                   LDG.E R38, desc[UR4][R2.64+0x58] ;
        |        /*0140*/                   LDG.E R39, desc[UR4][R2.64+0x5c] ;
        |        /*0150*/                   LDG.E R8, desc[UR4][R2.64+0x60] ;
        |        /*0160*/                   LDG.E R9, desc[UR4][R2.64+0x64] ;
        |        /*0170*/                   LDG.E R10, desc[UR4][R2.64+0x68] ;
        |        /*0180*/                   LDG.E R11, desc[UR4][R2.64+0x6c] ;
        |        /*0190*/                   LDG.E R12, desc[UR4][R2.64+0x70] ;
        |        /*01a0*/                   LDG.E R13, desc[UR4][R2.64+0x74] ;
        |        /*01b0*/                   LDG.E R14, desc[UR4][R2.64+0x78] ;
        |        /*01c0*/                   LDG.E R15, desc[UR4][R2.64+0x7c] ;
        |        /*01d0*/                   LDG.E R24, desc[UR4][R2.64+0x20] ;
        |        /*01e0*/                   LDG.E R25, desc[UR4][R2.64+0x24] ;
        |        /*01f0*/                   LDG.E R26, desc[UR4][R2.64+0x28] ;
        |        /*0200*/                   LDG.E R27, desc[UR4][R2.64+0x2c] ;
        |        /*0210*/                   LDG.E R28, desc[UR4][R2.64+0x30] ;
        |        /*0220*/                   LDG.E R29, desc[UR4][R2.64+0x34] ;
        |        /*0230*/                   LDG.E R30, desc[UR4][R2.64+0x38] ;
        |        /*0240*/                   LDG.E R31, desc[UR4][R2.64+0x3c] ;
        |        /*0250*/                   LDC.64 R2, c[0x0][0x210] ;
        |        /*0260*/                   IMAD.WIDE.U32 R2, R0, 0x20, R2 ;
        |        /*0270*/                   DMMA.16x8x16 R8, R16, R32, R8 ;
        |        /*0280*/                   STG.E desc[UR4][R2.64], R8 ;
        |        /*0290*/                   STG.E desc[UR4][R2.64+0x4], R9 ;
        |        /*02a0*/                   STG.E desc[UR4][R2.64+0x8], R10 ;
        |        /*02b0*/                   STG.E desc[UR4][R2.64+0xc], R11 ;
        |        /*02c0*/                   STG.E desc[UR4][R2.64+0x10], R12 ;
        |        /*02d0*/                   STG.E desc[UR4][R2.64+0x14], R13 ;
        |        /*02e0*/                   STG.E desc[UR4][R2.64+0x18], R14 ;
        |        /*02f0*/                   STG.E desc[UR4][R2.64+0x1c], R15 ;
        |        /*0300*/                   EXIT ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "hmmaE4m3Sm90" ->
      """//--------------------- .text.hmmaE4m3Sm90        --------------------------
        |	.section	.text.hmmaE4m3Sm90,"ax",@progbits
        |	.align	128
        |        .global         hmmaE4m3Sm90
        |        .type           hmmaE4m3Sm90,@function
        |        .size           hmmaE4m3Sm90,(.L_x_1 - hmmaE4m3Sm90)
        |        .other          hmmaE4m3Sm90,@"STO_CUDA_ENTRY STV_DEFAULT"
        |hmmaE4m3Sm90:
        |.text.hmmaE4m3Sm90:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_LANEID ;
        |        /*0020*/                   LDC.64 R10, c[0x0][0x218] ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0040*/                   IMAD.WIDE.U32 R10, R0, 0x28, R10 ;
        |        /*0050*/                   LDG.E R14, desc[UR4][R10.64+0x10] ;
        |        /*0060*/                   LDG.E R15, desc[UR4][R10.64+0x14] ;
        |        /*0070*/                   LDG.E R8, desc[UR4][R10.64] ;
        |        /*0080*/                   LDG.E R9, desc[UR4][R10.64+0x4] ;
        |        /*0090*/                   LDG.E R16, desc[UR4][R10.64+0x8] ;
        |        /*00a0*/                   LDG.E R17, desc[UR4][R10.64+0xc] ;
        |        /*00b0*/                   LDG.E R13, desc[UR4][R10.64+0x18] ;
        |        /*00c0*/                   LDG.E R2, desc[UR4][R10.64+0x1c] ;
        |        /*00d0*/                   LDG.E R3, desc[UR4][R10.64+0x20] ;
        |        /*00e0*/                   LDG.E R12, desc[UR4][R10.64+0x24] ;
        |        /*00f0*/                   F2FP.F16.E4M3.UNPACK_B R18, R14 ;
        |        /*0100*/                   F2FP.F16.E4M3.UNPACK_B R14, R14.H1 ;
        |        /*0110*/                   F2FP.F16.E4M3.UNPACK_B R19, R15.reuse ;
        |        /*0120*/                   F2FP.F16.E4M3.UNPACK_B R15, R15.H1 ;
        |        /*0130*/                   F2FP.F16.E4M3.UNPACK_B R4, R8.reuse ;
        |        /*0140*/                   F2FP.F16.E4M3.UNPACK_B R8, R8.H1 ;
        |        /*0150*/                   F2FP.F16.E4M3.UNPACK_B R5, R9 ;
        |        /*0160*/                   F2FP.F16.E4M3.UNPACK_B R9, R9.H1 ;
        |        /*0170*/                   F2FP.F16.E4M3.UNPACK_B R6, R16.reuse ;
        |        /*0180*/                   F2FP.F16.E4M3.UNPACK_B R10, R16.H1 ;
        |        /*0190*/                   F2FP.F16.E4M3.UNPACK_B R7, R17.reuse ;
        |        /*01a0*/                   F2FP.F16.E4M3.UNPACK_B R11, R17.H1 ;
        |        /*01b0*/                   HMMA.16816.F32 R4, R4, R18, RZ ;
        |        /*01c0*/                   @!UPT UIADD3 URZ, URZ, URZ, URZ ;
        |        /*01d0*/                   @!UPT UIADD3 URZ, URZ, URZ, URZ ;
        |        /*01e0*/                   HMMA.16816.F32 R4, R8, R14, R4 ;
        |        /*01f0*/                   LDC.64 R8, c[0x0][0x210] ;
        |        /*0200*/                   @!UPT UIADD3 URZ, URZ, URZ, URZ ;
        |        /*0210*/                   @!UPT UIADD3 URZ, URZ, URZ, URZ ;
        |        /*0220*/                   FADD R13, R13, R4 ;
        |        /*0230*/                   IMAD.WIDE.U32 R8, R0, 0x10, R8 ;
        |        /*0240*/                   FADD R5, R2, R5 ;
        |        /*0250*/                   FADD R3, R3, R6 ;
        |        /*0260*/                   FADD R7, R12, R7 ;
        |        /*0270*/                   STG.E desc[UR4][R8.64], R13 ;
        |        /*0280*/                   STG.E desc[UR4][R8.64+0x4], R5 ;
        |        /*0290*/                   STG.E desc[UR4][R8.64+0x8], R3 ;
        |        /*02a0*/                   STG.E desc[UR4][R8.64+0xc], R7 ;
        |        /*02b0*/                   EXIT ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "immaS4SatSm90" ->
      """//--------------------- .text.immaS4SatSm90       --------------------------
        |	.section	.text.immaS4SatSm90,"ax",@progbits
        |	.align	128
        |        .global         immaS4SatSm90
        |        .type           immaS4SatSm90,@function
        |        .size           immaS4SatSm90,(.L_x_1 - immaS4SatSm90)
        |        .other          immaS4SatSm90,@"STO_CUDA_ENTRY STV_DEFAULT"
        |immaS4SatSm90:
        |.text.immaS4SatSm90:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R0, SR_LANEID ;
        |        /*0020*/                   LDC.64 R6, c[0x0][0x218] ;
        |        /*0030*/                   ULDC.64 UR4, c[0x0][0x208] ;
        |        /*0040*/                   IMAD.WIDE.U32 R6, R0, 0x10, R6 ;
        |        /*0050*/                   LDG.E R3, desc[UR4][R6.64] ;
        |        /*0060*/                   LDG.E R4, desc[UR4][R6.64+0x4] ;
        |        /*0070*/                   LDG.E R2, desc[UR4][R6.64+0x8] ;
        |        /*0080*/                   LDG.E R5, desc[UR4][R6.64+0xc] ;
        |        /*0090*/                   MOV R6, 0xb0 ;
        |        /*00a0*/                   CALL.REL.NOINC `($__internal_0_$__cuda_sm_9x_mma_sub_byte_internal_m8n8k32_s4_s4_satfinite) ;
        |        /*00b0*/                   LDC.64 R2, c[0x0][0x210] ;
        |        /*00c0*/                   IMAD.WIDE.U32 R2, R0, 0x8, R2 ;
        |        /*00d0*/                   STG.E desc[UR4][R2.64], R5 ;
        |        /*00e0*/                   STG.E desc[UR4][R2.64+0x4], R9 ;
        |        /*00f0*/                   EXIT ;
        |        .type           $__internal_0_$__cuda_sm_9x_mma_sub_byte_internal_m8n8k32_s4_s4_satfinite,@function
        |        .size           $__internal_0_$__cuda_sm_9x_mma_sub_byte_internal_m8n8k32_s4_s4_satfinite,(.L_x_1 - $__internal_0_$__cuda_sm_9x_mma_sub_byte_internal_m8n8k32_s4_s4_satfinite)
        |$__internal_0_$__cuda_sm_9x_mma_sub_byte_internal_m8n8k32_s4_s4_satfinite:
        |        /*0100*/                   LOP3.LUT R8, R3.reuse, 0xf00, RZ, 0xc0, !PT ;
        |        /*0110*/                   IMAD.SHL.U32 R7, R3.reuse, 0x10000000, RZ ;
        |        /*0120*/                   LOP3.LUT R12, R4.reuse, 0xf00, RZ, 0xc0, !PT ;
        |        /*0130*/                   IMAD.SHL.U32 R11, R4, 0x10000000, RZ ;
        |        /*0140*/                   LOP3.LUT R9, R3, 0xf000, RZ, 0xc0, !PT ;
        |        /*0150*/                   IMAD.SHL.U32 R8, R8, 0x100000, RZ ;
        |        /*0160*/                   SHF.R.S32.HI R7, RZ, 0x1c, R7 ;
        |        /*0170*/                   IMAD.SHL.U32 R12, R12, 0x100000, RZ ;
        |        /*0180*/                   SHF.R.S32.HI R11, RZ, 0x1c, R11 ;
        |        /*0190*/                   IMAD.U32 R9, R9, 0x10000, RZ ;
        |        /*01a0*/                   LOP3.LUT R7, R7, 0xff, RZ, 0xc0, !PT ;
        |        /*01b0*/                   SHF.R.S32.HI R10, RZ, 0x14, R8 ;
        |        /*01c0*/                   LOP3.LUT R11, R11, 0xff, RZ, 0xc0, !PT ;
        |        /*01d0*/                   LOP3.LUT R7, R7, 0xff00, R10, 0xf8, !PT ;
        |        /*01e0*/                   LOP3.LUT R10, R3.reuse, 0xf0000, RZ, 0xc0, !PT ;
        |        /*01f0*/                   SHF.R.S32.HI R12, RZ, 0x14, R12 ;
        |        /*0200*/                   LOP3.LUT R8, R3, 0xf0, RZ, 0xc0, !PT ;
        |        /*0210*/                   IMAD.SHL.U32 R10, R10, 0x1000, RZ ;
        |        /*0220*/                   LOP3.LUT R12, R11, 0xff00, R12, 0xf8, !PT ;
        |        /*0230*/                   LOP3.LUT R11, R4, 0xf0, RZ, 0xc0, !PT ;
        |        /*0240*/                   IMAD.SHL.U32 R8, R8, 0x1000000, RZ ;
        |        /*0250*/                   SHF.R.S32.HI R10, RZ, 0xc, R10 ;
        |        /*0260*/                   LOP3.LUT R13, R4.reuse, 0xf0000, RZ, 0xc0, !PT ;
        |        /*0270*/                   IMAD.SHL.U32 R11, R11, 0x1000000, RZ ;
        |        /*0280*/                   LOP3.LUT R7, R7, 0xff0000, R10, 0xf8, !PT ;
        |        /*0290*/                   LOP3.LUT R10, R4, 0xf000, RZ, 0xc0, !PT ;
        |        /*02a0*/                   IMAD.SHL.U32 R13, R13, 0x1000, RZ ;
        |        /*02b0*/                   SHF.R.S32.HI R8, RZ, 0x1c, R8 ;
        |        /*02c0*/                   SHF.R.S32.HI R9, RZ, 0x14, R9 ;
        |        /*02d0*/                   IMAD.U32 R10, R10, 0x10000, RZ ;
        |        /*02e0*/                   LOP3.LUT R8, R8, 0xff, RZ, 0xc0, !PT ;
        |        /*02f0*/                   SHF.R.S32.HI R11, RZ, 0x1c, R11 ;
        |        /*0300*/                   LOP3.LUT R8, R8, 0xff00, R9, 0xf8, !PT ;
        |        /*0310*/                   SHF.R.S32.HI R10, RZ, 0x14, R10 ;
        |        /*0320*/                   LOP3.LUT R11, R11, 0xff, RZ, 0xc0, !PT ;
        |        /*0330*/                   SHF.R.S32.HI R13, RZ, 0xc, R13 ;
        |        /*0340*/                   LOP3.LUT R9, R3, 0xf00000, RZ, 0xc0, !PT ;
        |        /*0350*/                   LOP3.LUT R14, R4, 0xf00000, RZ, 0xc0, !PT ;
        |        /*0360*/                   LOP3.LUT R11, R11, 0xff00, R10, 0xf8, !PT ;
        |        /*0370*/                   IMAD.SHL.U32 R9, R9, 0x100, RZ ;
        |        /*0380*/                   LOP3.LUT R12, R12, 0xff0000, R13, 0xf8, !PT ;
        |        /*0390*/                   IMAD.SHL.U32 R14, R14, 0x100, RZ ;
        |        /*03a0*/                   LOP3.LUT R10, R3, 0xf000000, RZ, 0xc0, !PT ;
        |        /*03b0*/                   LOP3.LUT R13, R4, 0xf000000, RZ, 0xc0, !PT ;
        |        /*03c0*/                   SHF.R.S32.HI R9, RZ, 0xc, R9 ;
        |        /*03d0*/                   IMAD.SHL.U32 R10, R10, 0x10, RZ ;
        |        /*03e0*/                   SHF.R.S32.HI R14, RZ, 0xc, R14 ;
        |        /*03f0*/                   IMAD.SHL.U32 R13, R13, 0x10, RZ ;
        |        /*0400*/                   LOP3.LUT R3, R3, 0xf0000000, RZ, 0xc0, !PT ;
        |        /*0410*/                   LOP3.LUT R4, R4, 0xf0000000, RZ, 0xc0, !PT ;
        |        /*0420*/                   LOP3.LUT R8, R8, 0xff0000, R9, 0xf8, !PT ;
        |        /*0430*/                   LOP3.LUT R11, R11, 0xff0000, R14, 0xf8, !PT ;
        |        /*0440*/                   SHF.R.S32.HI R10, RZ, 0x4, R10 ;
        |        /*0450*/                   SHF.R.S32.HI R13, RZ, 0x4, R13 ;
        |        /*0460*/                   SHF.R.S32.HI R3, RZ, 0x4, R3 ;
        |        /*0470*/                   SHF.R.S32.HI R4, RZ, 0x4, R4 ;
        |        /*0480*/                   LOP3.LUT R7, R7, 0xff000000, R10, 0xf8, !PT ;
        |        /*0490*/                   LOP3.LUT R12, R12, 0xff000000, R13, 0xf8, !PT ;
        |        /*04a0*/                   LOP3.LUT R3, R8, 0xff000000, R3, 0xf8, !PT ;
        |        /*04b0*/                   LOP3.LUT R4, R11, 0xff000000, R4, 0xf8, !PT ;
        |        /*04c0*/                   IMMA.8816.S8.S8 R12, R7.ROW, R12.COL, RZ ;
        |        /*04d0*/                   IMMA.8816.S8.S8 R8, R3.ROW, R4.COL, RZ ;
        |        /*04e0*/                   @!UPT UIADD3 URZ, URZ, URZ, URZ ;
        |        /*04f0*/                   IMAD.IADD R3, R12, 0x1, R8 ;
        |        /*0500*/                   IMAD.IADD R4, R13, 0x1, R9 ;
        |        /*0510*/                   IMAD.IADD R7, R2, 0x1, R3 ;
        |        /*0520*/                   IMAD.IADD R8, R5, 0x1, R4 ;
        |        /*0530*/                   PLOP3.LUT P0, PT, R3.reuse.SIGN, R2.reuse.SIGN, R7.reuse.SIGN, 0x2, 0x0 ;
        |        /*0540*/                   PLOP3.LUT P2, PT, R4.reuse.SIGN, R5.reuse.SIGN, R8.reuse.SIGN, 0x2, 0x0 ;
        |        /*0550*/                   PLOP3.LUT P3, PT, R4.SIGN, R5.SIGN, R8.SIGN, 0x40, 0x0 ;
        |        /*0560*/                   PLOP3.LUT P1, PT, R3.SIGN, R2.SIGN, R7.SIGN, 0x40, 0x0 ;
        |        /*0570*/                   SEL R5, R7, 0x7fffffff, !P0 ;
        |        /*0580*/                   IMAD.MOV.U32 R7, RZ, RZ, 0x0 ;
        |        /*0590*/                   SEL R9, R8, 0x7fffffff, !P2 ;
        |        /*05a0*/                   SEL R5, R5, 0x80000000, !P1 ;
        |        /*05b0*/                   SEL R9, R9, 0x80000000, !P3 ;
        |        /*05c0*/                   RET.REL.NODEC R6 `(immaS4SatSm90) ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "bmmaXorSm100" ->
      """//--------------------- .text.bmmaXorSm100        --------------------------
        |	.section	.text.bmmaXorSm100,"ax",@progbits
        |	.align	128
        |        .global         bmmaXorSm100
        |        .type           bmmaXorSm100,@function
        |        .size           bmmaXorSm100,(.L_x_2 - bmmaXorSm100)
        |        .other          bmmaXorSm100,@"STO_CUDA_ENTRY STV_DEFAULT"
        |bmmaXorSm100:
        |.text.bmmaXorSm100:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R2, SR_LANEID ;
        |        /*0020*/                   LDC.64 R4, c[0x0][0x388] ;
        |        /*0030*/                   LDCU.64 UR6, c[0x0][0x358] ;
        |        /*0040*/                   IMAD.WIDE.U32 R4, R2, 0x10, R4 ;
        |        /*0050*/                   LDG.E R19, desc[UR6][R4.64] ;
        |        /*0060*/                   LDG.E R17, desc[UR6][R4.64+0x4] ;
        |        /*0070*/                   LDG.E R16, desc[UR6][R4.64+0x8] ;
        |        /*0080*/                   LDG.E R7, desc[UR6][R4.64+0xc] ;
        |        /*0090*/                   MOV R0, 0xb0 ;
        |        /*00a0*/                   CALL.REL.NOINC `($__internal_1_$__cuda_sm_10x_mma_bit_internal_xor_m8n8k128) ;
        |        /*00b0*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*00c0*/                   IMAD.WIDE.U32 R2, R2, 0x8, R4 ;
        |        /*00d0*/                   STG.E desc[UR6][R2.64], R11 ;
        |        /*00e0*/                   STG.E desc[UR6][R2.64+0x4], R7 ;
        |        /*00f0*/                   EXIT ;
        |        .type           $__internal_0_$__cuda_sm_10x_mma_bit_internal_and_m8n8k128,@function
        |        .size           $__internal_0_$__cuda_sm_10x_mma_bit_internal_and_m8n8k128,($__internal_1_$__cuda_sm_10x_mma_bit_internal_xor_m8n8k128 - $__internal_0_$__cuda_sm_10x_mma_bit_internal_and_m8n8k128)
        |$__internal_0_$__cuda_sm_10x_mma_bit_internal_and_m8n8k128:
        |        /*0100*/                   LOP3.LUT R8, R19, 0x88888888, RZ, 0xc0, !PT ;
        |        /*0110*/                   LOP3.LUT R14, R18.reuse, 0x44444444, RZ, 0xc0, !PT ;
        |        /*0120*/                   SHF.R.U32.HI R11, RZ, 0x3, R8 ;
        |        /*0130*/                   LOP3.LUT R8, R18, 0x88888888, RZ, 0xc0, !PT ;
        |        /*0140*/                   SHF.R.U32.HI R22, RZ, 0x2, R14 ;
        |        /*0150*/                   CS2R R14, SRZ ;
        |        /*0160*/                   MOVM.U4TO8.M832 R10, R11 ;
        |        /*0170*/                   SHF.R.U32.HI R26, RZ, 0x3, R8 ;
        |        /*0180*/                   LOP3.LUT R8, R19.reuse, 0x44444444, RZ, 0xc0, !PT ;
        |        /*0190*/                   MOVM.U4TO8.M832 R22, R22 ;
        |        /*01a0*/                   LOP3.LUT R24, R19, 0x22222222, RZ, 0xc0, !PT ;
        |        /*01b0*/                   SHF.R.U32.HI R20, RZ, 0x2, R8 ;
        |        /*01c0*/                   MOVM.U4TO8.M832 R26, R26 ;
        |        /*01d0*/                   SHF.R.U32.HI R24, RZ, 0x1, R24 ;
        |        /*01e0*/                   MOVM.U4TO8.M832 R20, R20 ;
        |        /*01f0*/                   MOVM.U4TO8.M832 R24, R24 ;
        |        /*0200*/                   IMAD.MOV.U32 R8, RZ, RZ, R10 ;
        |        /*0210*/                   IMAD.MOV.U32 R9, RZ, RZ, R11.reuse ;
        |        /*0220*/                   IMAD.MOV.U32 R10, RZ, RZ, R11 ;
        |        /*0230*/                   IMAD.MOV.U32 R11, RZ, RZ, RZ ;
        |        /*0240*/                   IMMA.16832.U8.U8 R8, R8.ROW, R26.COL, R12 ;
        |        /*0250*/                   LOP3.LUT R12, R18.reuse, 0x22222222, RZ, 0xc0, !PT ;
        |        /*0260*/                   IMAD.MOV.U32 R13, RZ, RZ, R21.reuse ;
        |        /*0270*/                   LOP3.LUT R18, R18, 0x11111111, RZ, 0xc0, !PT ;
        |        /*0280*/                   IMAD.MOV.U32 R14, RZ, RZ, R21 ;
        |        /*0290*/                   SHF.R.U32.HI R26, RZ, 0x1, R12 ;
        |        /*02a0*/                   IMAD.MOV.U32 R12, RZ, RZ, R20 ;
        |        /*02b0*/                   MOVM.U4TO8.M832 R20, R26 ;
        |        /*02c0*/                   IMMA.16832.U8.U8 R8, R12.ROW, R22.COL, R8 ;
        |        /*02d0*/                   LOP3.LUT R22, R19, 0x11111111, RZ, 0xc0, !PT ;
        |        /*02e0*/                   IMAD.MOV.U32 R12, RZ, RZ, R24 ;
        |        /*02f0*/                   MOVM.U4TO8.M832 R18, R18 ;
        |        /*0300*/                   IMAD.MOV.U32 R13, RZ, RZ, R25 ;
        |        /*0310*/                   IMAD.MOV.U32 R14, RZ, RZ, R25 ;
        |        /*0320*/                   MOVM.U4TO8.M832 R22, R22 ;
        |        /*0330*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*0340*/                   IMMA.16832.U8.U8 R8, R12.ROW, R20.COL, R8 ;
        |        /*0350*/                   IMAD.MOV.U32 R12, RZ, RZ, R22 ;
        |        /*0360*/                   MOV R13, R23 ;
        |        /*0370*/                   IMAD.MOV.U32 R14, RZ, RZ, R23 ;
        |        /*0380*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*0390*/                   IMMA.16832.U8.U8 R8, R12.ROW, R18.COL, R8 ;
        |        /*03a0*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*03b0*/                   IMAD.MOV.U32 R10, RZ, RZ, R6 ;
        |        /*03c0*/                   IMAD.MOV.U32 R11, RZ, RZ, 0x0 ;
        |        /*03d0*/                   RET.REL.NODEC R10 `(bmmaXorSm100) ;
        |        .type           $__internal_1_$__cuda_sm_10x_mma_bit_internal_xor_m8n8k128,@function
        |        .size           $__internal_1_$__cuda_sm_10x_mma_bit_internal_xor_m8n8k128,(.L_x_2 - $__internal_1_$__cuda_sm_10x_mma_bit_internal_xor_m8n8k128)
        |$__internal_1_$__cuda_sm_10x_mma_bit_internal_xor_m8n8k128:
        |        /*03e0*/                   UMOV UR4, URZ ;
        |        /*03f0*/                   UMOV UR5, URZ ;
        |        /*0400*/                   LOP3.LUT R18, RZ, R17, RZ, 0x33, !PT ;
        |        /*0410*/                   IMAD.U32 R12, RZ, RZ, UR4 ;
        |        /*0420*/                   LOP3.LUT R5, RZ, R19, RZ, 0x33, !PT ;
        |        /*0430*/                   IMAD.U32 R13, RZ, RZ, UR5 ;
        |        /*0440*/                   MOV R6, 0x460 ;
        |        /*0450*/                   CALL.REL.NOINC `($__internal_0_$__cuda_sm_10x_mma_bit_internal_and_m8n8k128) ;
        |        /*0460*/                   IMAD.MOV.U32 R3, RZ, RZ, R8 ;
        |        /*0470*/                   MOV R4, R9 ;
        |        /*0480*/                   IMAD.MOV.U32 R19, RZ, RZ, R5 ;
        |        /*0490*/                   CS2R R12, SRZ ;
        |        /*04a0*/                   IMAD.MOV.U32 R18, RZ, RZ, R17 ;
        |        /*04b0*/                   MOV R6, 0x4d0 ;
        |        /*04c0*/                   CALL.REL.NOINC `($__internal_0_$__cuda_sm_10x_mma_bit_internal_and_m8n8k128) ;
        |        /*04d0*/                   IADD3 R7, PT, PT, R7, R4, R9 ;
        |        /*04e0*/                   IMAD.MOV.U32 R4, RZ, RZ, R0 ;
        |        /*04f0*/                   IADD3 R11, PT, PT, R16, R3, R8 ;
        |        /*0500*/                   IMAD.MOV.U32 R5, RZ, RZ, 0x0 ;
        |        /*0510*/                   RET.REL.NODEC R4 `(bmmaXorSm100) ;
        |.L_x_2:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "bmmaAndSm100" ->
      """//--------------------- .text.bmmaAndSm100        --------------------------
        |	.section	.text.bmmaAndSm100,"ax",@progbits
        |	.align	128
        |        .global         bmmaAndSm100
        |        .type           bmmaAndSm100,@function
        |        .size           bmmaAndSm100,(.L_x_1 - bmmaAndSm100)
        |        .other          bmmaAndSm100,@"STO_CUDA_ENTRY STV_DEFAULT"
        |bmmaAndSm100:
        |.text.bmmaAndSm100:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R0, SR_LANEID ;
        |        /*0020*/                   LDC.64 R12, c[0x0][0x388] ;
        |        /*0030*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0040*/                   IMAD.WIDE.U32 R12, R0, 0x28, R12 ;
        |        /*0050*/                   LDG.E R7, desc[UR4][R12.64] ;
        |        /*0060*/                   LDG.E R16, desc[UR4][R12.64+0x4] ;
        |        /*0070*/                   LDG.E R5, desc[UR4][R12.64+0x8] ;
        |        /*0080*/                   LDG.E R3, desc[UR4][R12.64+0xc] ;
        |        /*0090*/                   LDG.E R6, desc[UR4][R12.64+0x10] ;
        |        /*00a0*/                   LDG.E R4, desc[UR4][R12.64+0x14] ;
        |        /*00b0*/                   LDG.E R8, desc[UR4][R12.64+0x18] ;
        |        /*00c0*/                   LDG.E R9, desc[UR4][R12.64+0x1c] ;
        |        /*00d0*/                   LDG.E R10, desc[UR4][R12.64+0x20] ;
        |        /*00e0*/                   LDG.E R11, desc[UR4][R12.64+0x24] ;
        |        /*00f0*/                   MOV R2, 0x110 ;
        |        /*0100*/                   CALL.REL.NOINC `($__internal_0_$__cuda_sm_10x_mma_bit_internal_and_m16n8k256) ;
        |        /*0110*/                   LDC.64 R2, c[0x0][0x380] ;
        |        /*0120*/                   IMAD.WIDE.U32 R2, R0, 0x10, R2 ;
        |        /*0130*/                   STG.E desc[UR4][R2.64], R4 ;
        |        /*0140*/                   STG.E desc[UR4][R2.64+0x4], R5 ;
        |        /*0150*/                   STG.E desc[UR4][R2.64+0x8], R6 ;
        |        /*0160*/                   STG.E desc[UR4][R2.64+0xc], R7 ;
        |        /*0170*/                   EXIT ;
        |        .type           $__internal_0_$__cuda_sm_10x_mma_bit_internal_and_m16n8k256,@function
        |        .size           $__internal_0_$__cuda_sm_10x_mma_bit_internal_and_m16n8k256,(.L_x_1 - $__internal_0_$__cuda_sm_10x_mma_bit_internal_and_m16n8k256)
        |$__internal_0_$__cuda_sm_10x_mma_bit_internal_and_m16n8k256:
        |        /*0180*/                   LOP3.LUT R12, R7, 0x88888888, RZ, 0xc0, !PT ;
        |        /*0190*/                   LOP3.LUT R14, R16, 0x88888888, RZ, 0xc0, !PT ;
        |        /*01a0*/                   SHF.R.U32.HI R13, RZ, 0x3, R12 ;
        |        /*01b0*/                   SHF.R.U32.HI R22, RZ, 0x3, R14 ;
        |        /*01c0*/                   LOP3.LUT R14, R6, 0x88888888, RZ, 0xc0, !PT ;
        |        /*01d0*/                   MOVM.U4TO8.M832 R12, R13 ;
        |        /*01e0*/                   LOP3.LUT R15, R16, 0x44444444, RZ, 0xc0, !PT ;
        |        /*01f0*/                   SHF.R.U32.HI R20, RZ, 0x3, R14 ;
        |        /*0200*/                   MOVM.U4TO8.M832 R22, R22 ;
        |        /*0210*/                   LOP3.LUT R14, R7, 0x44444444, RZ, 0xc0, !PT ;
        |        /*0220*/                   SHF.R.U32.HI R18, RZ, 0x2, R15 ;
        |        /*0230*/                   MOVM.U4TO8.M832 R20, R20 ;
        |        /*0240*/                   SHF.R.U32.HI R26, RZ, 0x2, R14 ;
        |        /*0250*/                   LOP3.LUT R17, R6, 0x44444444, RZ, 0xc0, !PT ;
        |        /*0260*/                   MOVM.U4TO8.M832 R18, R18 ;
        |        /*0270*/                   MOVM.U4TO8.M832 R26, R26 ;
        |        /*0280*/                   IMAD.MOV.U32 R14, RZ, RZ, R13 ;
        |        /*0290*/                   IMAD.MOV.U32 R13, RZ, RZ, R22 ;
        |        /*02a0*/                   SHF.R.U32.HI R22, RZ, 0x2, R17 ;
        |        /*02b0*/                   IMAD.MOV.U32 R15, RZ, RZ, R23 ;
        |        /*02c0*/                   LOP3.LUT R17, R7, 0x22222222, RZ, 0xc0, !PT ;
        |        /*02d0*/                   MOVM.U4TO8.M832 R22, R22 ;
        |        /*02e0*/                   SHF.R.U32.HI R17, RZ, 0x1, R17 ;
        |        /*02f0*/                   IMMA.16832.U8.U8 R8, R12.ROW, R20.COL, R8 ;
        |        /*0300*/                   LOP3.LUT R12, R16, 0x22222222, RZ, 0xc0, !PT ;
        |        /*0310*/                   IMAD.MOV.U32 R15, RZ, RZ, R19 ;
        |        /*0320*/                   LOP3.LUT R13, R6, 0x22222222, RZ, 0xc0, !PT ;
        |        /*0330*/                   MOVM.U4TO8.M832 R24, R17 ;
        |        /*0340*/                   SHF.R.U32.HI R20, RZ, 0x1, R12 ;
        |        /*0350*/                   IMAD.MOV.U32 R12, RZ, RZ, R26 ;
        |        /*0360*/                   SHF.R.U32.HI R26, RZ, 0x1, R13 ;
        |        /*0370*/                   IMAD.MOV.U32 R14, RZ, RZ, R27 ;
        |        /*0380*/                   LOP3.LUT R16, R16, 0x11111111, RZ, 0xc0, !PT ;
        |        /*0390*/                   IMAD.MOV.U32 R13, RZ, RZ, R18 ;
        |        /*03a0*/                   MOVM.U4TO8.M832 R20, R20 ;
        |        /*03b0*/                   LOP3.LUT R6, R6, 0x11111111, RZ, 0xc0, !PT ;
        |        /*03c0*/                   MOVM.U4TO8.M832 R18, R26 ;
        |        /*03d0*/                   MOVM.U4TO8.M832 R16, R16 ;
        |        /*03e0*/                   IMMA.16832.U8.U8 R8, R12.ROW, R22.COL, R8 ;
        |        /*03f0*/                   LOP3.LUT R22, R7, 0x11111111, RZ, 0xc0, !PT ;
        |        /*0400*/                   MOVM.U4TO8.M832 R6, R6 ;
        |        /*0410*/                   IMAD.MOV.U32 R12, RZ, RZ, R24 ;
        |        /*0420*/                   MOVM.U4TO8.M832 R22, R22 ;
        |        /*0430*/                   IMAD.MOV.U32 R14, RZ, RZ, R25 ;
        |        /*0440*/                   IMAD.MOV.U32 R13, RZ, RZ, R20 ;
        |        /*0450*/                   LOP3.LUT R20, R5, 0x88888888, RZ, 0xc0, !PT ;
        |        /*0460*/                   IMAD.MOV.U32 R15, RZ, RZ, R21 ;
        |        /*0470*/                   LOP3.LUT R21, R3, 0x88888888, RZ, 0xc0, !PT ;
        |        /*0480*/                   SHF.R.U32.HI R20, RZ, 0x3, R20 ;
        |        /*0490*/                   IMMA.16832.U8.U8 R8, R12.ROW, R18.COL, R8 ;
        |        /*04a0*/                   SHF.R.U32.HI R18, RZ, 0x3, R21 ;
        |        /*04b0*/                   IMAD.MOV.U32 R15, RZ, RZ, R17 ;
        |        /*04c0*/                   MOVM.U4TO8.M832 R20, R20 ;
        |        /*04d0*/                   LOP3.LUT R13, R4, 0x88888888, RZ, 0xc0, !PT ;
        |        /*04e0*/                   MOVM.U4TO8.M832 R18, R18 ;
        |        /*04f0*/                   SHF.R.U32.HI R24, RZ, 0x3, R13 ;
        |        /*0500*/                   IMAD.MOV.U32 R12, RZ, RZ, R22 ;
        |        /*0510*/                   LOP3.LUT R22, R5, 0x44444444, RZ, 0xc0, !PT ;
        |        /*0520*/                   IMAD.MOV.U32 R14, RZ, RZ, R23 ;
        |        /*0530*/                   IMAD.MOV.U32 R13, RZ, RZ, R16 ;
        |        /*0540*/                   SHF.R.U32.HI R22, RZ, 0x2, R22 ;
        |        /*0550*/                   MOVM.U4TO8.M832 R16, R24 ;
        |        /*0560*/                   IMMA.16832.U8.U8 R8, R12.ROW, R6.COL, R8 ;
        |        /*0570*/                   MOVM.U4TO8.M832 R22, R22 ;
        |        /*0580*/                   LOP3.LUT R6, R3, 0x44444444, RZ, 0xc0, !PT ;
        |        /*0590*/                   SHF.R.U32.HI R6, RZ, 0x2, R6 ;
        |        /*05a0*/                   IMAD.MOV.U32 R12, RZ, RZ, R20 ;
        |        /*05b0*/                   LOP3.LUT R20, R4, 0x44444444, RZ, 0xc0, !PT ;
        |        /*05c0*/                   IMAD.MOV.U32 R14, RZ, RZ, R21 ;
        |        /*05d0*/                   IMAD.MOV.U32 R13, RZ, RZ, R18 ;
        |        /*05e0*/                   MOVM.U4TO8.M832 R6, R6 ;
        |        /*05f0*/                   SHF.R.U32.HI R18, RZ, 0x2, R20 ;
        |        /*0600*/                   IMAD.MOV.U32 R15, RZ, RZ, R19 ;
        |        /*0610*/                   LOP3.LUT R20, R5, 0x22222222, RZ, 0xc0, !PT ;
        |        /*0620*/                   MOVM.U4TO8.M832 R18, R18 ;
        |        /*0630*/                   SHF.R.U32.HI R20, RZ, 0x1, R20 ;
        |        /*0640*/                   IMMA.16832.U8.U8 R8, R12.ROW, R16.COL, R8 ;
        |        /*0650*/                   LOP3.LUT R12, R3, 0x22222222, RZ, 0xc0, !PT ;
        |        /*0660*/                   LOP3.LUT R13, R4, 0x22222222, RZ, 0xc0, !PT ;
        |        /*0670*/                   MOVM.U4TO8.M832 R20, R20 ;
        |        /*0680*/                   SHF.R.U32.HI R16, RZ, 0x1, R12 ;
        |        /*0690*/                   IMAD.MOV.U32 R12, RZ, RZ, R22 ;
        |        /*06a0*/                   SHF.R.U32.HI R24, RZ, 0x1, R13 ;
        |        /*06b0*/                   IMAD.MOV.U32 R14, RZ, RZ, R23 ;
        |        /*06c0*/                   LOP3.LUT R22, R5, 0x11111111, RZ, 0xc0, !PT ;
        |        /*06d0*/                   MOVM.U4TO8.M832 R16, R16 ;
        |        /*06e0*/                   IMAD.MOV.U32 R13, RZ, RZ, R6 ;
        |        /*06f0*/                   MOVM.U4TO8.M832 R22, R22 ;
        |        /*0700*/                   IMAD.MOV.U32 R15, RZ, RZ, R7 ;
        |        /*0710*/                   MOVM.U4TO8.M832 R6, R24 ;
        |        /*0720*/                   IMMA.16832.U8.U8 R8, R12.ROW, R18.COL, R8 ;
        |        /*0730*/                   LOP3.LUT R18, R3, 0x11111111, RZ, 0xc0, !PT ;
        |        /*0740*/                   IMAD.MOV.U32 R3, RZ, RZ, 0x0 ;
        |        /*0750*/                   IMAD.MOV.U32 R12, RZ, RZ, R20 ;
        |        /*0760*/                   LOP3.LUT R20, R4, 0x11111111, RZ, 0xc0, !PT ;
        |        /*0770*/                   IMAD.MOV.U32 R14, RZ, RZ, R21 ;
        |        /*0780*/                   MOVM.U4TO8.M832 R18, R18 ;
        |        /*0790*/                   IMAD.MOV.U32 R13, RZ, RZ, R16 ;
        |        /*07a0*/                   IMAD.MOV.U32 R15, RZ, RZ, R17 ;
        |        /*07b0*/                   MOVM.U4TO8.M832 R16, R20 ;
        |        /*07c0*/                   IMAD.MOV.U32 R4, RZ, RZ, R22 ;
        |        /*07d0*/                   IMMA.16832.U8.U8 R8, R12.ROW, R6.COL, R8 ;
        |        /*07e0*/                   IMAD.MOV.U32 R6, RZ, RZ, R23 ;
        |        /*07f0*/                   IMAD.MOV.U32 R5, RZ, RZ, R18 ;
        |        /*0800*/                   IMAD.MOV.U32 R7, RZ, RZ, R19 ;
        |        /*0810*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*0820*/                   IMMA.16832.U8.U8 R4, R4.ROW, R16.COL, R8 ;
        |        /*0830*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*0840*/                   RET.REL.NODEC R2 `(bmmaAndSm100) ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "immaU4SatSm120" ->
      """//--------------------- .text.immaU4SatSm120      --------------------------
        |	.section	.text.immaU4SatSm120,"ax",@progbits
        |	.align	128
        |        .global         immaU4SatSm120
        |        .type           immaU4SatSm120,@function
        |        .size           immaU4SatSm120,(.L_x_1 - immaU4SatSm120)
        |        .other          immaU4SatSm120,@"STO_CUDA_ENTRY STV_DEFAULT"
        |immaU4SatSm120:
        |.text.immaU4SatSm120:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R11, SR_LANEID ;
        |        /*0020*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0030*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0040*/                   IMAD.WIDE.U32 R2, R11, 0x10, R2 ;
        |        /*0050*/                   LDG.E R0, desc[UR4][R2.64] ;
        |        /*0060*/                   LDG.E R8, desc[UR4][R2.64+0x4] ;
        |        /*0070*/                   LDG.E R9, desc[UR4][R2.64+0x8] ;
        |        /*0080*/                   LDG.E R10, desc[UR4][R2.64+0xc] ;
        |        /*0090*/                   MOV R16, 0xb0 ;
        |        /*00a0*/                   CALL.REL.NOINC `($__internal_0_$__cuda_sm_9x_mma_sub_byte_internal_m8n8k32_u4_u4_satfinite) ;
        |        /*00b0*/                   LDC.64 R2, c[0x0][0x380] ;
        |        /*00c0*/                   IMAD.WIDE.U32 R2, R11, 0x8, R2 ;
        |        /*00d0*/                   STG.E desc[UR4][R2.64], R9 ;
        |        /*00e0*/                   STG.E desc[UR4][R2.64+0x4], R5 ;
        |        /*00f0*/                   EXIT ;
        |        .type           $__internal_0_$__cuda_sm_9x_mma_sub_byte_internal_m8n8k32_u4_u4_satfinite,@function
        |        .size           $__internal_0_$__cuda_sm_9x_mma_sub_byte_internal_m8n8k32_u4_u4_satfinite,(.L_x_1 - $__internal_0_$__cuda_sm_9x_mma_sub_byte_internal_m8n8k32_u4_u4_satfinite)
        |$__internal_0_$__cuda_sm_9x_mma_sub_byte_internal_m8n8k32_u4_u4_satfinite:
        |        /*0100*/                   LOP3.LUT R6, R8, 0xf0f0f0f, RZ, 0xc0, !PT ;
        |        /*0110*/                   LOP3.LUT R4, R0, 0xf0f0f0f, RZ, 0xc0, !PT ;
        |        /*0120*/                   LOP3.LUT R8, R8, 0xf0f0f0f0, RZ, 0xc0, !PT ;
        |        /*0130*/                   LOP3.LUT R0, R0, 0xf0f0f0f0, RZ, 0xc0, !PT ;
        |        /*0140*/                   MOV R5, RZ ;
        |        /*0150*/                   MOV R3, RZ ;
        |        /*0160*/                   SHF.R.U32.HI R8, RZ, 0x4, R8 ;
        |        /*0170*/                   SHF.R.U32.HI R2, RZ, 0x4, R0 ;
        |        /*0180*/                   IMMA.16816.U8.U8 R4, R4.ROW, R6.COL, RZ ;
        |        /*0190*/                   IMMA.16816.U8.U8 R12, R2.ROW, R8.COL, RZ ;
        |        /*01a0*/                   MOV R2, R16 ;
        |        /*01b0*/                   MOV R3, 0x0 ;
        |        /*01c0*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*01d0*/                   IADD R4, R4, R12 ;
        |        /*01e0*/                   IADD R5, R5, R13 ;
        |        /*01f0*/                   VIADD.S32.ISAT R9, R9, R4 ;
        |        /*0200*/                   VIADD.S32.ISAT R5, R10, R5 ;
        |        /*0210*/                   RET.REL.NODEC R2 `(immaU4SatSm120) ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "qmmaE4m3E5m2Sm120" ->
      """//--------------------- .text.qmmaE4m3E5m2Sm120   --------------------------
        |	.section	.text.qmmaE4m3E5m2Sm120,"ax",@progbits
        |	.align	128
        |        .global         qmmaE4m3E5m2Sm120
        |        .type           qmmaE4m3E5m2Sm120,@function
        |        .size           qmmaE4m3E5m2Sm120,(.L_x_1 - qmmaE4m3E5m2Sm120)
        |        .other          qmmaE4m3E5m2Sm120,@"STO_CUDA_ENTRY STV_DEFAULT"
        |qmmaE4m3E5m2Sm120:
        |.text.qmmaE4m3E5m2Sm120:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R17, SR_LANEID ;
        |        /*0020*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0030*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0040*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*0050*/                   IMAD.WIDE.U32 R2, R17, 0x28, R2 ;
        |        /*0060*/                   LDG.E R6, desc[UR4][R2.64+0x10] ;
        |        /*0070*/                   LDG.E R7, desc[UR4][R2.64+0x14] ;
        |        /*0080*/                   LDG.E R8, desc[UR4][R2.64] ;
        |        /*0090*/                   LDG.E R9, desc[UR4][R2.64+0x4] ;
        |        /*00a0*/                   LDG.E R10, desc[UR4][R2.64+0x8] ;
        |        /*00b0*/                   LDG.E R11, desc[UR4][R2.64+0xc] ;
        |        /*00c0*/                   LDG.E R12, desc[UR4][R2.64+0x18] ;
        |        /*00d0*/                   LDG.E R13, desc[UR4][R2.64+0x1c] ;
        |        /*00e0*/                   LDG.E R14, desc[UR4][R2.64+0x20] ;
        |        /*00f0*/                   LDG.E R15, desc[UR4][R2.64+0x24] ;
        |        /*0100*/                   IMAD.WIDE.U32 R4, R17, 0x10, R4 ;
        |        /*0110*/                   QMMA.16832.F32.E4M3.E5M2 R8, R8, R6, R12 ;
        |        /*0120*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*0130*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*0140*/                   STG.E desc[UR4][R4.64], R8 ;
        |        /*0150*/                   STG.E desc[UR4][R4.64+0x4], R9 ;
        |        /*0160*/                   STG.E desc[UR4][R4.64+0x8], R10 ;
        |        /*0170*/                   STG.E desc[UR4][R4.64+0xc], R11 ;
        |        /*0180*/                   EXIT ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "qmmaKindE2m1E3m2Sm120a" ->
      """//--------------------- .text.qmmaKindE2m1E3m2Sm120a --------------------------
        |	.section	.text.qmmaKindE2m1E3m2Sm120a,"ax",@progbits
        |	.align	128
        |        .global         qmmaKindE2m1E3m2Sm120a
        |        .type           qmmaKindE2m1E3m2Sm120a,@function
        |        .size           qmmaKindE2m1E3m2Sm120a,(.L_x_1 - qmmaKindE2m1E3m2Sm120a)
        |        .other          qmmaKindE2m1E3m2Sm120a,@"STO_CUDA_ENTRY STV_DEFAULT"
        |qmmaKindE2m1E3m2Sm120a:
        |.text.qmmaKindE2m1E3m2Sm120a:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R15, SR_LANEID ;
        |        /*0020*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0030*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0040*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*0050*/                   IMAD.WIDE.U32 R2, R15, 0x20, R2 ;
        |        /*0060*/                   LDG.E R6, desc[UR4][R2.64+0x10] ;
        |        /*0070*/                   LDG.E R7, desc[UR4][R2.64+0x14] ;
        |        /*0080*/                   LDG.E R12, desc[UR4][R2.64+0x18] ;
        |        /*0090*/                   LDG.E R13, desc[UR4][R2.64+0x1c] ;
        |        /*00a0*/                   LDG.E R8, desc[UR4][R2.64] ;
        |        /*00b0*/                   LDG.E R9, desc[UR4][R2.64+0x4] ;
        |        /*00c0*/                   LDG.E R10, desc[UR4][R2.64+0x8] ;
        |        /*00d0*/                   LDG.E R11, desc[UR4][R2.64+0xc] ;
        |        /*00e0*/                   IMAD.WIDE.U32 R4, R15, 0x8, R4 ;
        |        /*00f0*/                   QMMA.16832.F16.E2M1.E3M2 R6, R8, R6, R12 ;
        |        /*0100*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*0110*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*0120*/                   STG.E desc[UR4][R4.64], R6 ;
        |        /*0130*/                   STG.E desc[UR4][R4.64+0x4], R7 ;
        |        /*0140*/                   EXIT ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "qmmaScaleSm120a" ->
      """//--------------------- .text.qmmaScaleSm120a     --------------------------
        |	.section	.text.qmmaScaleSm120a,"ax",@progbits
        |	.align	128
        |        .global         qmmaScaleSm120a
        |        .type           qmmaScaleSm120a,@function
        |        .size           qmmaScaleSm120a,(.L_x_1 - qmmaScaleSm120a)
        |        .other          qmmaScaleSm120a,@"STO_CUDA_ENTRY STV_DEFAULT"
        |qmmaScaleSm120a:
        |.text.qmmaScaleSm120a:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R19, SR_LANEID ;
        |        /*0020*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0030*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0040*/                   UMOV UR6, 0x5d ;
        |        /*0050*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*0060*/                   IMAD.WIDE.U32 R2, R19, 0x30, R2 ;
        |        /*0070*/                   LDG.E R0, desc[UR4][R2.64+0x28] ;
        |        /*0080*/                   LDG.E R17, desc[UR4][R2.64+0x2c] ;
        |        /*0090*/                   LDG.E R6, desc[UR4][R2.64+0x10] ;
        |        /*00a0*/                   LDG.E R7, desc[UR4][R2.64+0x14] ;
        |        /*00b0*/                   LDG.E R8, desc[UR4][R2.64] ;
        |        /*00c0*/                   LDG.E R9, desc[UR4][R2.64+0x4] ;
        |        /*00d0*/                   LDG.E R10, desc[UR4][R2.64+0x8] ;
        |        /*00e0*/                   LDG.E R11, desc[UR4][R2.64+0xc] ;
        |        /*00f0*/                   LDG.E R12, desc[UR4][R2.64+0x18] ;
        |        /*0100*/                   LDG.E R13, desc[UR4][R2.64+0x1c] ;
        |        /*0110*/                   LDG.E R14, desc[UR4][R2.64+0x20] ;
        |        /*0120*/                   LDG.E R15, desc[UR4][R2.64+0x24] ;
        |        /*0130*/                   IMAD.WIDE.U32 R4, R19, 0x10, R4 ;
        |        /*0140*/                   QMMA.SF.16832.F32.E4M3.E2M3.E8 R8, R8, R6, R12, R0, R17, UR6 ;
        |        /*0150*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*0160*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*0170*/                   STG.E desc[UR4][R4.64], R8 ;
        |        /*0180*/                   STG.E desc[UR4][R4.64+0x4], R9 ;
        |        /*0190*/                   STG.E desc[UR4][R4.64+0x8], R10 ;
        |        /*01a0*/                   STG.E desc[UR4][R4.64+0xc], R11 ;
        |        /*01b0*/                   EXIT ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "ommaNvf4Sm120a" ->
      """//--------------------- .text.ommaNvf4Sm120a      --------------------------
        |	.section	.text.ommaNvf4Sm120a,"ax",@progbits
        |	.align	128
        |        .global         ommaNvf4Sm120a
        |        .type           ommaNvf4Sm120a,@function
        |        .size           ommaNvf4Sm120a,(.L_x_1 - ommaNvf4Sm120a)
        |        .other          ommaNvf4Sm120a,@"STO_CUDA_ENTRY STV_DEFAULT"
        |ommaNvf4Sm120a:
        |.text.ommaNvf4Sm120a:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R19, SR_LANEID ;
        |        /*0020*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0030*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0040*/                   UMOV UR6, 0x64 ;
        |        /*0050*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*0060*/                   IMAD.WIDE.U32 R2, R19, 0x30, R2 ;
        |        /*0070*/                   LDG.E R0, desc[UR4][R2.64+0x28] ;
        |        /*0080*/                   LDG.E R17, desc[UR4][R2.64+0x2c] ;
        |        /*0090*/                   LDG.E R6, desc[UR4][R2.64+0x10] ;
        |        /*00a0*/                   LDG.E R7, desc[UR4][R2.64+0x14] ;
        |        /*00b0*/                   LDG.E R8, desc[UR4][R2.64] ;
        |        /*00c0*/                   LDG.E R9, desc[UR4][R2.64+0x4] ;
        |        /*00d0*/                   LDG.E R10, desc[UR4][R2.64+0x8] ;
        |        /*00e0*/                   LDG.E R11, desc[UR4][R2.64+0xc] ;
        |        /*00f0*/                   LDG.E R12, desc[UR4][R2.64+0x18] ;
        |        /*0100*/                   LDG.E R13, desc[UR4][R2.64+0x1c] ;
        |        /*0110*/                   LDG.E R14, desc[UR4][R2.64+0x20] ;
        |        /*0120*/                   LDG.E R15, desc[UR4][R2.64+0x24] ;
        |        /*0130*/                   IMAD.WIDE.U32 R4, R19, 0x10, R4 ;
        |        /*0140*/                   OMMA.SF.16864.F32.E2M1.E2M1.UE4M3.4X R8, R8, R6, R12, R0, R17, UR6 ;
        |        /*0150*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*0160*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*0170*/                   STG.E desc[UR4][R4.64], R8 ;
        |        /*0180*/                   STG.E desc[UR4][R4.64+0x4], R9 ;
        |        /*0190*/                   STG.E desc[UR4][R4.64+0x8], R10 ;
        |        /*01a0*/                   STG.E desc[UR4][R4.64+0xc], R11 ;
        |        /*01b0*/                   EXIT ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "ommaMxf4Sm120a" ->
      """//--------------------- .text.ommaMxf4Sm120a      --------------------------
        |	.section	.text.ommaMxf4Sm120a,"ax",@progbits
        |	.align	128
        |        .global         ommaMxf4Sm120a
        |        .type           ommaMxf4Sm120a,@function
        |        .size           ommaMxf4Sm120a,(.L_x_1 - ommaMxf4Sm120a)
        |        .other          ommaMxf4Sm120a,@"STO_CUDA_ENTRY STV_DEFAULT"
        |ommaMxf4Sm120a:
        |.text.ommaMxf4Sm120a:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R19, SR_LANEID ;
        |        /*0020*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0030*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0040*/                   UMOV UR6, 0x22 ;
        |        /*0050*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*0060*/                   IMAD.WIDE.U32 R2, R19, 0x30, R2 ;
        |        /*0070*/                   LDG.E R0, desc[UR4][R2.64+0x28] ;
        |        /*0080*/                   LDG.E R17, desc[UR4][R2.64+0x2c] ;
        |        /*0090*/                   LDG.E R6, desc[UR4][R2.64+0x10] ;
        |        /*00a0*/                   LDG.E R7, desc[UR4][R2.64+0x14] ;
        |        /*00b0*/                   LDG.E R8, desc[UR4][R2.64] ;
        |        /*00c0*/                   LDG.E R9, desc[UR4][R2.64+0x4] ;
        |        /*00d0*/                   LDG.E R10, desc[UR4][R2.64+0x8] ;
        |        /*00e0*/                   LDG.E R11, desc[UR4][R2.64+0xc] ;
        |        /*00f0*/                   LDG.E R12, desc[UR4][R2.64+0x18] ;
        |        /*0100*/                   LDG.E R13, desc[UR4][R2.64+0x1c] ;
        |        /*0110*/                   LDG.E R14, desc[UR4][R2.64+0x20] ;
        |        /*0120*/                   LDG.E R15, desc[UR4][R2.64+0x24] ;
        |        /*0130*/                   IMAD.WIDE.U32 R4, R19, 0x10, R4 ;
        |        /*0140*/                   OMMA.SF.16864.F32.E2M1.E2M1.E8 R8, R8, R6, R12, R0, R17, UR6 ;
        |        /*0150*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*0160*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*0170*/                   STG.E desc[UR4][R4.64], R8 ;
        |        /*0180*/                   STG.E desc[UR4][R4.64+0x4], R9 ;
        |        /*0190*/                   STG.E desc[UR4][R4.64+0x8], R10 ;
        |        /*01a0*/                   STG.E desc[UR4][R4.64+0xc], R11 ;
        |        /*01b0*/                   EXIT ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "qmmaSparseScaleSm120a" ->
      """//--------------------- .text.qmmaSparseScaleSm120a --------------------------
        |	.section	.text.qmmaSparseScaleSm120a,"ax",@progbits
        |	.align	128
        |        .global         qmmaSparseScaleSm120a
        |        .type           qmmaSparseScaleSm120a,@function
        |        .size           qmmaSparseScaleSm120a,(.L_x_1 - qmmaSparseScaleSm120a)
        |        .other          qmmaSparseScaleSm120a,@"STO_CUDA_ENTRY STV_DEFAULT"
        |qmmaSparseScaleSm120a:
        |.text.qmmaSparseScaleSm120a:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R7, SR_LANEID ;
        |        /*0020*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0030*/                   LDCU.64 UR4, c[0x0][0x358] ;
        |        /*0040*/                   MOV R20, 0xe9c84d8e ;
        |        /*0050*/                   UMOV UR6, 0x34 ;
        |        /*0060*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*0070*/                   IMAD.WIDE.U32 R2, R7, 0x38, R2 ;
        |        /*0080*/                   LDG.E R0, desc[UR4][R2.64+0x34] ;
        |        /*0090*/                   LDG.E R21, desc[UR4][R2.64+0x30] ;
        |        /*00a0*/                   LDG.E R8, desc[UR4][R2.64] ;
        |        /*00b0*/                   LDG.E R9, desc[UR4][R2.64+0x4] ;
        |        /*00c0*/                   LDG.E R10, desc[UR4][R2.64+0x8] ;
        |        /*00d0*/                   LDG.E R11, desc[UR4][R2.64+0xc] ;
        |        /*00e0*/                   LDG.E R12, desc[UR4][R2.64+0x10] ;
        |        /*00f0*/                   LDG.E R13, desc[UR4][R2.64+0x14] ;
        |        /*0100*/                   LDG.E R14, desc[UR4][R2.64+0x18] ;
        |        /*0110*/                   LDG.E R15, desc[UR4][R2.64+0x1c] ;
        |        /*0120*/                   LDG.E R16, desc[UR4][R2.64+0x20] ;
        |        /*0130*/                   LDG.E R17, desc[UR4][R2.64+0x24] ;
        |        /*0140*/                   LDG.E R18, desc[UR4][R2.64+0x28] ;
        |        /*0150*/                   LDG.E R19, desc[UR4][R2.64+0x2c] ;
        |        /*0160*/                   IMAD.WIDE.U32 R4, R7, 0x10, R4 ;
        |        /*0170*/                   QMMA.SF.SP.16864.F32.E3M2.E2M1.E8 R8, R8, R12, R16, R20, R0, UR6, 0x0 ;
        |        /*0180*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*0190*/                   @!UPT UIADD3 URZ, UPT, UPT, URZ, URZ, URZ ;
        |        /*01a0*/                   STG.E desc[UR4][R4.64], R8 ;
        |        /*01b0*/                   STG.E desc[UR4][R4.64+0x4], R9 ;
        |        /*01c0*/                   STG.E desc[UR4][R4.64+0x8], R10 ;
        |        /*01d0*/                   STG.E desc[UR4][R4.64+0xc], R11 ;
        |        /*01e0*/                   EXIT ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin,
    "ldsmU4Sm100a" ->
      """//--------------------- .text.ldsmU4Sm100a        --------------------------
        |	.section	.text.ldsmU4Sm100a,"ax",@progbits
        |	.align	128
        |        .global         ldsmU4Sm100a
        |        .type           ldsmU4Sm100a,@function
        |        .size           ldsmU4Sm100a,(.L_x_1 - ldsmU4Sm100a)
        |        .other          ldsmU4Sm100a,@"STO_CUDA_ENTRY STV_DEFAULT"
        |ldsmU4Sm100a:
        |.text.ldsmU4Sm100a:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R11, SR_LANEID ;
        |        /*0020*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0030*/                   LDCU.64 UR6, c[0x0][0x358] ;
        |        /*0040*/                   S2UR UR5, SR_CgaCtaId ;
        |        /*0050*/                   UMOV UR4, 0x400 ;
        |        /*0060*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*0070*/                   IMAD.WIDE.U32 R2, R11, 0x10, R2 ;
        |        /*0080*/                   LDG.E R6, desc[UR6][R2.64] ;
        |        /*0090*/                   LDG.E R7, desc[UR6][R2.64+0x4] ;
        |        /*00a0*/                   LDG.E R8, desc[UR6][R2.64+0x8] ;
        |        /*00b0*/                   LDG.E R9, desc[UR6][R2.64+0xc] ;
        |        /*00c0*/                   ULEA UR4, UR5, UR4, 0x18 ;
        |        /*00d0*/                   LEA R0, R11.reuse, UR4, 0x3 ;
        |        /*00e0*/                   LEA R10, R11.reuse, UR4, 0x4 ;
        |        /*00f0*/                   IMAD.WIDE.U32 R2, R11, 0x10, R4 ;
        |        /*0100*/                   STS.64 [R0], R6 ;
        |        /*0110*/                   STS.64 [R0+0x100], R8 ;
        |        /*0120*/                   NOP ;
        |        /*0130*/                   LDSM.U4x16P64TO8.MT1616.2 R12, [R10] ;
        |        /*0140*/                   STG.E desc[UR6][R2.64], R12 ;
        |        /*0150*/                   STG.E desc[UR6][R2.64+0x4], R13 ;
        |        /*0160*/                   STG.E desc[UR6][R2.64+0x8], R14 ;
        |        /*0170*/                   STG.E desc[UR6][R2.64+0xc], R15 ;
        |        /*0180*/                   EXIT ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |""".stripMargin,
    "ldsmU6Sm120a" ->
      """//--------------------- .text.ldsmU6Sm120a        --------------------------
        |	.section	.text.ldsmU6Sm120a,"ax",@progbits
        |	.align	128
        |        .global         ldsmU6Sm120a
        |        .type           ldsmU6Sm120a,@function
        |        .size           ldsmU6Sm120a,(.L_x_1 - ldsmU6Sm120a)
        |        .other          ldsmU6Sm120a,@"STO_CUDA_ENTRY STV_DEFAULT"
        |ldsmU6Sm120a:
        |.text.ldsmU6Sm120a:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R11, SR_LANEID ;
        |        /*0020*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0030*/                   LDCU.64 UR6, c[0x0][0x358] ;
        |        /*0040*/                   S2UR UR5, SR_CgaCtaId ;
        |        /*0050*/                   UMOV UR4, 0x400 ;
        |        /*0060*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*0070*/                   IMAD.WIDE.U32 R2, R11, 0x10, R2 ;
        |        /*0080*/                   LDG.E R6, desc[UR6][R2.64] ;
        |        /*0090*/                   LDG.E R7, desc[UR6][R2.64+0x4] ;
        |        /*00a0*/                   LDG.E R8, desc[UR6][R2.64+0x8] ;
        |        /*00b0*/                   LDG.E R9, desc[UR6][R2.64+0xc] ;
        |        /*00c0*/                   ULEA UR4, UR5, UR4, 0x18 ;
        |        /*00d0*/                   LEA R0, R11.reuse, UR4, 0x3 ;
        |        /*00e0*/                   LEA R10, R11.reuse, UR4, 0x4 ;
        |        /*00f0*/                   IMAD.WIDE.U32 R2, R11, 0x10, R4 ;
        |        /*0100*/                   STS.64 [R0], R6 ;
        |        /*0110*/                   STS.64 [R0+0x100], R8 ;
        |        /*0120*/                   NOP ;
        |        /*0130*/                   LDSM.U6x16P32TO8.M816.4 R12, [R10] ;
        |        /*0140*/                   STG.E desc[UR6][R2.64], R12 ;
        |        /*0150*/                   STG.E desc[UR6][R2.64+0x4], R13 ;
        |        /*0160*/                   STG.E desc[UR6][R2.64+0x8], R14 ;
        |        /*0170*/                   STG.E desc[UR6][R2.64+0xc], R15 ;
        |        /*0180*/                   EXIT ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |""".stripMargin,
    "stsmB8Sm120a" ->
      """//--------------------- .text.stsmB8Sm120a        --------------------------
        |	.section	.text.stsmB8Sm120a,"ax",@progbits
        |	.align	128
        |        .global         stsmB8Sm120a
        |        .type           stsmB8Sm120a,@function
        |        .size           stsmB8Sm120a,(.L_x_1 - stsmB8Sm120a)
        |        .other          stsmB8Sm120a,@"STO_CUDA_ENTRY STV_DEFAULT"
        |stsmB8Sm120a:
        |.text.stsmB8Sm120a:
        |        /*0000*/                   LDC R1, c[0x0][0x37c] ;
        |        /*0010*/                   S2R R11, SR_LANEID ;
        |        /*0020*/                   LDC.64 R2, c[0x0][0x388] ;
        |        /*0030*/                   LDCU.64 UR6, c[0x0][0x358] ;
        |        /*0040*/                   S2UR UR5, SR_CgaCtaId ;
        |        /*0050*/                   UMOV UR4, 0x400 ;
        |        /*0060*/                   LDC.64 R4, c[0x0][0x380] ;
        |        /*0070*/                   IMAD.WIDE.U32 R2, R11, 0x8, R2 ;
        |        /*0080*/                   LDG.E R6, desc[UR6][R2.64] ;
        |        /*0090*/                   LDG.E R7, desc[UR6][R2.64+0x4] ;
        |        /*00a0*/                   ULEA UR4, UR5, UR4, 0x18 ;
        |        /*00b0*/                   IMAD.WIDE.U32 R4, R11, 0x8, R4 ;
        |        /*00c0*/                   LEA R0, R11.reuse, UR4, 0x4 ;
        |        /*00d0*/                   LEA R8, R11, UR4, 0x3 ;
        |        /*00e0*/                   STSM.8.MT168.2 [R0], R6 ;
        |        /*00f0*/                   NOP ;
        |        /*0100*/                   LDS.64 R8, [R8] ;
        |        /*0110*/                   STG.E desc[UR6][R4.64], R8 ;
        |        /*0120*/                   STG.E desc[UR6][R4.64+0x4], R9 ;
        |        /*0130*/                   EXIT ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |	.type		.nv.reservedSmem.cap,@object
        |	.size		.nv.reservedSmem.cap,0x4
        |""".stripMargin,
    "stsmB16Sm90" ->
      """//--------------------- .text.stsmB16Sm90         --------------------------
        |	.section	.text.stsmB16Sm90,"ax",@progbits
        |	.align	128
        |        .global         stsmB16Sm90
        |        .type           stsmB16Sm90,@function
        |        .size           stsmB16Sm90,(.L_x_1 - stsmB16Sm90)
        |        .other          stsmB16Sm90,@"STO_CUDA_ENTRY STV_DEFAULT"
        |stsmB16Sm90:
        |.text.stsmB16Sm90:
        |        /*0000*/                   LDC R1, c[0x0][0x28] ;
        |        /*0010*/                   S2R R7, SR_LANEID ;
        |        /*0020*/                   LDC.64 R2, c[0x0][0x218] ;
        |        /*0030*/                   ULDC.64 UR6, c[0x0][0x208] ;
        |        /*0040*/                   S2UR UR5, SR_CgaCtaId ;
        |        /*0050*/                   UMOV UR4, 0x400 ;
        |        /*0060*/                   LDC.64 R4, c[0x0][0x210] ;
        |        /*0070*/                   IMAD.WIDE.U32 R2, R7, 0x10, R2 ;
        |        /*0080*/                   LDG.E R8, desc[UR6][R2.64] ;
        |        /*0090*/                   LDG.E R9, desc[UR6][R2.64+0x4] ;
        |        /*00a0*/                   LDG.E R10, desc[UR6][R2.64+0x8] ;
        |        /*00b0*/                   LDG.E R11, desc[UR6][R2.64+0xc] ;
        |        /*00c0*/                   ULEA UR4, UR5, UR4, 0x18 ;
        |        /*00d0*/                   IMAD.WIDE.U32 R4, R7, 0x10, R4 ;
        |        /*00e0*/                   LEA R0, R7, UR4, 0x4 ;
        |        /*00f0*/                   STSM.16.MT88.4 [R0], R8 ;
        |        /*0100*/                   NOP ;
        |        /*0110*/                   LDS.128 R12, [R0] ;
        |        /*0120*/                   STG.E desc[UR6][R4.64], R12 ;
        |        /*0130*/                   STG.E desc[UR6][R4.64+0x4], R13 ;
        |        /*0140*/                   STG.E desc[UR6][R4.64+0x8], R14 ;
        |        /*0150*/                   STG.E desc[UR6][R4.64+0xc], R15 ;
        |        /*0160*/                   EXIT ;
        |.L_x_1:
        |
        |
        |//--------------------- SYMBOLS --------------------------
        |
        |	.type		.nv.reservedSmem.offset0,@object
        |	.size		.nv.reservedSmem.offset0,0x4
        |""".stripMargin
  )
}
