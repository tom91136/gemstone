package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object LegacyI2iS16S8Fixtures {
  val legacyI2iS16S8Sm35ProbePtxSource: String =
    """.version 7.8
      |.target sm_35
      |.address_size 64
      |.visible .entry probe(.param .u64 out, .param .u64 input) {
      |.reg .b64 %rd<3>;
      |.reg .b8 %b1;
      |.reg .b16 %h<4>;
      |ld.param.u64 %rd1, [out];
      |ld.param.u64 %rd2, [input];
      |ld.global.b8 %b1, [%rd2];
      |cvt.s16.s8 %h1, %b1;
      |st.global.b16 [%rd1], %h1;
      |ld.global.s16 %h2, [%rd2+2];
      |neg.s16 %h3, %h2;
      |st.global.b16 [%rd1+2], %h3;
      |ret;
      |}
      |""".stripMargin

  val legacyI2iS16S8Sm35Probe: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=8"
      |	.align	64
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0008*/                   MOV R1, c[0x0][0x44];
      |        /*0010*/                   MOV R2, c[0x0][0x148];
      |        /*0018*/                   MOV R3, c[0x0][0x14c];
      |        /*0020*/                   LD.E.U8 R0, [R2];
      |        /*0028*/                   MOV R4, c[0x0][0x140];
      |        /*0030*/                   MOV R5, c[0x0][0x144];
      |        /*0038*/                   I2I.S16.S8 R0, R0;
      |        /*0048*/                   ST.E.U16 [R4], R0;
      |        /*0050*/                   LD.E.S16 R6, [R2+0x2];
      |        /*0058*/                   I2I.S16.S16 R7, -R6;
      |        /*0060*/                   ST.E.U16 [R4+0x2], R7;
      |        /*0068*/                   EXIT;
      |.L_x_0:
      |        /*0070*/                   BRA `(.L_x_0);
      |        /*0078*/                   NOP;
      |.L_x_1:
      |""".stripMargin

}

class LegacyI2iS16S8Fixtures extends NvidiaFixtureSuite {
  import LegacyI2iS16S8Fixtures.*

  reproduces("legacyI2iS16S8Sm35Probe")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "probe.ptx" -> legacyI2iS16S8Sm35ProbePtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_35 probe.ptx -o probe.cubin",
      "nvdisasm -c -ndf probe.cubin > probe.sass"
    ),
    output = "probe.sass",
    expected = legacyI2iS16S8Sm35Probe
  )

}
