package net.kurobako.gemstone.isa.sass.fixtures

object ConstantFixtures {
  val constantCuda: String =
    """__constant__ int values[2] = {3, 4};
      |
      |extern "C" __global__ void constant_read(int *out) {
      |  const int index = static_cast<int>(threadIdx.x);
      |  if (index < 2) out[index] = values[index];
      |}
      |""".stripMargin

  val listings: Map[String, String] = Map(
    "constantSm35" ->
      """//--------------------- .text.constant_read       --------------------------
        |	.section	.text.constant_read,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=6"
        |	.align	64
        |        .global         constant_read
        |        .type           constant_read,@function
        |        .size           constant_read,(.L_x_1 - constant_read)
        |        .other          constant_read,@"STO_CUDA_ENTRY STV_DEFAULT"
        |constant_read:
        |.text.constant_read:
        |        /*0008*/                   MOV R1, c[0x0][0x44];
        |        /*0010*/                   S2R R0, SR_TID.X;
        |        /*0018*/                   ISETP.GT.AND P0, PT, R0, 0x1, PT;
        |        /*0020*/               @P0 EXIT;
        |        /*0028*/                   SHL R5, R0, 0x2;
        |        /*0030*/                   MOV32I R3, 0x4;
        |        /*0038*/                   IADD R2.CC, R5, c[0x0][0x140];
        |        /*0048*/                   IMAD.HI.X R3, R0, R3, c[0x0][0x144];
        |        /*0050*/                   LDC R4, c[0x3][R5];
        |        /*0058*/                   ST.E [R2], R4;
        |        /*0060*/                   EXIT;
        |.L_x_1:
        |""".stripMargin,
    "constantSm89" ->
      """//--------------------- .text.constant_read       --------------------------
        |	.section	.text.constant_read,"ax",@progbits
        |	.sectioninfo	@"SHI_REGISTERS=8"
        |	.align	128
        |        .global         constant_read
        |        .type           constant_read,@function
        |        .size           constant_read,(.L_x_1 - constant_read)
        |        .other          constant_read,@"STO_CUDA_ENTRY STV_DEFAULT"
        |constant_read:
        |.text.constant_read:
        |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
        |        /*0010*/                   S2R R2, SR_TID.X ;
        |        /*0020*/                   ISETP.GT.AND P0, PT, R2, 0x1, PT ;
        |        /*0030*/               @P0 EXIT ;
        |        /*0040*/                   IMAD.WIDE R2, R2, 0x4, RZ ;
        |        /*0050*/                   ULDC.64 UR4, c[0x0][0x118] ;
        |        /*0060*/                   IMAD.MOV.U32 R0, RZ, RZ, R2 ;
        |        /*0070*/                   IADD3 R2, P0, R2, c[0x0][0x160], RZ ;
        |        /*0080*/                   LDC R5, c[0x3][R0] ;
        |        /*0090*/                   IADD3.X R3, R3, c[0x0][0x164], RZ, P0, !PT ;
        |        /*00a0*/                   STG.E [R2.64], R5 ;
        |        /*00b0*/                   EXIT ;
        |.L_x_1:
        |""".stripMargin
  )
}
