package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object LegacyI2iCcFixtures {
  val legacyI2iCcSm35ProbePtxSource: String =
    """.version 6.0
      |.target sm_35
      |.address_size 64
      |.visible .entry probe(.param .u64 input, .param .u64 output) {
      |.reg .pred %p<2>;
      |.reg .b32 %r<2>;
      |.reg .b64 %rd<3>;
      |ld.param.u64 %rd0, [input];
      |ld.param.u64 %rd2, [output];
      |and.b64 %rd1, %rd0, 0x0000ffffffffffff;
      |setp.ne.u64 %p0, %rd1, 0;
      |selp.u32 %r1, 1, 0, %p0;
      |st.global.u32 [%rd2], %r1;
      |ret;
      |}
      |""".stripMargin

  val legacyI2iCcSm35Probe: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=5"
      |	.align	64
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0008*/                   MOV R1, c[0x0][0x44];
      |        /*0010*/                   I2I.U32.U32 RZ.CC, c[0x0] [0x140];
      |        /*0018*/                   MOV R0, c[0x0][0x144];
      |        /*0020*/                   MOV R2, c[0x0][0x148];
      |        /*0028*/                   MOV R3, c[0x0][0x14c];
      |        /*0030*/                   LOP32I.AND.X RZ.CC, R0, 0xffff;
      |        /*0038*/                   CSETP.NE.AND P0, PT, CC, PT;
      |        /*0048*/                   SEL R4, RZ, 0x1, !P0;
      |        /*0050*/                   ST.E [R2], R4;
      |        /*0058*/                   EXIT;
      |.L_x_0:
      |        /*0060*/                   BRA `(.L_x_0);
      |        /*0068*/                   NOP;
      |        /*0070*/                   NOP;
      |        /*0078*/                   NOP;
      |.L_x_1:
      |""".stripMargin

}

class LegacyI2iCcFixtures extends NvidiaFixtureSuite {
  import LegacyI2iCcFixtures.*

  reproduces("legacyI2iCcSm35Probe")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "probe.ptx" -> legacyI2iCcSm35ProbePtxSource
    ),
    script = Vec(
      "ptxas -arch=sm_35 probe.ptx -o probe.cubin",
      "nvdisasm -c -ndf probe.cubin > probe.sass"
    ),
    output = "probe.sass",
    expected = legacyI2iCcSm35Probe
  )

}
