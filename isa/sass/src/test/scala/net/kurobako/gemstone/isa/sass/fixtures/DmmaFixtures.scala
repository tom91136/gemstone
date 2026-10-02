package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object DmmaFixtures {
  val dmmaSm80PtxSource: String =
    """.version 7.8
      |.target sm_80
      |.address_size 64
      |.visible .entry double_mma(.param .u64 out) {
      |  .reg .b64 %p, %offset;
      |  .reg .b32 %lane, %a, %b;
      |  .reg .f64 %left, %right, %c0, %c1;
      |  ld.param.u64 %p, [out];
      |  mov.u32 %lane, %laneid;
      |  add.u32 %a, %lane, 1;
      |  mad.lo.u32 %b, %lane, 2, 1;
      |  cvt.rn.f64.u32 %left, %a;
      |  cvt.rn.f64.u32 %right, %b;
      |  mov.f64 %c0, 0d4014000000000000;
      |  mov.f64 %c1, 0dc01c000000000000;
      |  mma.sync.aligned.m8n8k4.row.col.f64.f64.f64.f64 {%c0,%c1}, {%left}, {%right}, {%c0,%c1};
      |  mul.wide.u32 %offset, %lane, 16;
      |  add.u64 %p, %p, %offset;
      |  st.global.f64 [%p], %c0;
      |  st.global.f64 [%p+8], %c1;
      |  ret;
      |}
      |""".stripMargin

  val dmmaSm80: String =
    """	.target	sm_80
      |
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.double_mma          --------------------------
      |	.section	.text.double_mma,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=16"
      |	.align	128
      |        .global         double_mma
      |        .type           double_mma,@function
      |        .size           double_mma,(.L_x_1 - double_mma)
      |        .other          double_mma,@"STO_CUDA_ENTRY STV_DEFAULT"
      |double_mma:
      |.text.double_mma:
      |        /*0000*/                   IMAD.MOV.U32 R1, RZ, RZ, c[0x0][0x28] ;
      |        /*0010*/                   S2R R13, SR_LANEID ;
      |        /*0020*/                   UMOV UR4, 0x0 ;
      |        /*0030*/                   IMAD.MOV.U32 R8, RZ, RZ, 0x0 ;
      |        /*0040*/                   UMOV UR5, 0x401c0000 ;
      |        /*0050*/                   MOV R9, 0x40140000 ;
      |        /*0060*/                   DADD R10, -RZ, -UR4 ;
      |        /*0070*/                   ULDC.64 UR4, c[0x0][0x118] ;
      |        /*0080*/                   IADD3 R0, R13.reuse, 0x1, RZ ;
      |        /*0090*/                   LEA R12, R13, 0x1, 0x1 ;
      |        /*00a0*/                   I2F.F64.U32 R2, R0 ;
      |        /*00b0*/                   I2F.F64.U32 R4, R12 ;
      |        /*00c0*/                   DMMA.884 R4, R2, R4, R8 ;
      |        /*00d0*/                   MOV R2, 0x10 ;
      |        /*00e0*/                   IMAD.WIDE.U32 R2, R13, R2, c[0x0][0x160] ;
      |        /*00f0*/                   @!UPT UIADD3 URZ, URZ, URZ, URZ ;
      |        /*0100*/                   STG.E.64 [R2.64], R4 ;
      |        /*0110*/                   STG.E.64 [R2.64+0x8], R6 ;
      |        /*0120*/                   EXIT ;
      |.L_x_0:
      |        /*0130*/                   BRA `(.L_x_0);
      |        /*0140*/                   NOP;
      |        /*0150*/                   NOP;
      |        /*0160*/                   NOP;
      |        /*0170*/                   NOP;
      |        /*0180*/                   NOP;
      |        /*0190*/                   NOP;
      |        /*01a0*/                   NOP;
      |        /*01b0*/                   NOP;
      |        /*01c0*/                   NOP;
      |        /*01d0*/                   NOP;
      |        /*01e0*/                   NOP;
      |        /*01f0*/                   NOP;
      |.L_x_1:
      |""".stripMargin

}

class DmmaFixtures extends NvidiaFixtureSuite {
  import DmmaFixtures.*

  reproduces("dmmaSm80")(
    tools = (ptxas = "13.3.73", nvdisasm = Some("13.3.73")),
    inputs = Vec(
      "input.ptx" -> dmmaSm80PtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_80 input.ptx -o kernel.cubin",
      "nvdisasm -c -ndf kernel.cubin > output.sass"
    ),
    output = "output.sass",
    expected = dmmaSm80
  )

}
