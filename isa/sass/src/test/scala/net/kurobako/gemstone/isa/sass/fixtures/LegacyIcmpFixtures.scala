package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

object LegacyIcmpFixtures {
  val legacyIcmpSm35: String =
    """	.headerflags	@"EF_CUDA_TEXMODE_UNIFIED EF_CUDA_64BIT_ADDRESS EF_CUDA_SM35 EF_CUDA_VIRTUAL_SM(EF_CUDA_SM35)"
      |	.elftype	@"ET_EXEC"
      |
      |
      |//--------------------- .text.probe               --------------------------
      |	.section	.text.probe,"ax",@progbits
      |	.sectioninfo	@"SHI_REGISTERS=7"
      |	.align	64
      |        .global         probe
      |        .type           probe,@function
      |        .size           probe,(.L_x_1 - probe)
      |        .other          probe,@"STO_CUDA_ENTRY STV_DEFAULT"
      |probe:
      |.text.probe:
      |        /*0008*/                   MOV R1, c[0x0][0x44];
      |        /*0010*/                   MOV R6, c[0x0][0x14c];
      |        /*0018*/                   MOV R2, c[0x0][0x140];
      |        /*0020*/                   MOV R0, c[0x0][0x150];
      |        /*0028*/                   MOV R3, c[0x0][0x144];
      |        /*0030*/                   ICMP.EQ R4, R6, c[0x0][0x148], R0;
      |        /*0038*/                   ICMP.GE R5, R6, c[0x0][0x148], R0;
      |        /*0048*/                   ICMP.LE R6, R6, c[0x0][0x148], R0;
      |        /*0050*/                   ST.E [R2], R4;
      |        /*0058*/                   ST.E [R2+0x4], R5;
      |        /*0060*/                   ST.E [R2+0x8], R6;
      |        /*0068*/                   EXIT;
      |.L_x_0:
      |        /*0070*/                   BRA `(.L_x_0);
      |        /*0078*/                   NOP;
      |.L_x_1:
      |""".stripMargin

  val icmpSource: String =
    """.version 7.8
      |.target sm_35
      |.address_size 64
      |.visible .entry probe(.param .u64 out, .param .s32 a, .param .s32 b, .param .s32 c) {
      |.reg .b64 %rd;
      |.reg .s32 %a, %b, %c, %x, %y, %z;
      |.reg .pred %p;
      |ld.param.u64 %rd, [out];
      |ld.param.s32 %a, [a];
      |ld.param.s32 %b, [b];
      |ld.param.s32 %c, [c];
      |setp.eq.s32 %p, %c, 0;
      |selp.s32 %x, %b, %a, %p;
      |slct.b32.s32 %y, %b, %a, %c;
      |setp.ge.s32 %p, 0, %c;
      |selp.s32 %z, %b, %a, %p;
      |st.global.s32 [%rd], %x;
      |st.global.s32 [%rd+4], %y;
      |st.global.s32 [%rd+8], %z;
      |ret;
      |}
      |""".stripMargin

}

class LegacyIcmpFixtures extends NvidiaFixtureSuite {
  import LegacyIcmpFixtures.*

  reproduces("legacyIcmpSm35")(
    tools = (ptxas = "11.8.89", nvdisasm = Some("11.8.86")),
    inputs = Vec(
      "icmp.ptx" -> icmpSource
    ),
    script = Vec(
      "ptxas -arch=sm_35 icmp.ptx -o icmp.cubin",
      "nvdisasm -c -ndf icmp.cubin > icmp.sass"
    ),
    output = "icmp.sass",
    expected = legacyIcmpSm35
  )

}
